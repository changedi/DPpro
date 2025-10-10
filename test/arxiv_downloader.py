#!/usr/bin/env python3
"""
ArXiv Downloader

A small CLI utility to download arXiv papers by arXiv IDs or by search query
using the official arXiv API (Atom feed) and direct PDF links.

Features:
- Download by one or more arXiv IDs
- Search by query/author/title/category and download top N results
- Safe filenames, automatic output directory creation
- Optional overwrite control and polite pacing between requests
- Pure standard library (no third-party dependencies)

Examples:
  # Download specific IDs
  python arxiv_downloader.py --ids 1706.03762v5 2402.12345

  # Search by free-text query (all fields) and download 5 results
  python arxiv_downloader.py --query "large language models" --max-results 5

  # Search by title and category, download most recent submissions
  python arxiv_downloader.py --title "graph transformer" --category cs.LG \
      --sort-by submittedDate --sort-order descending --max-results 3

Notes:
- Be considerate: avoid sending too many requests quickly. The script adds a
  small delay between downloads by default. You can adjust with --sleep.
- For the API queries (metadata), requests go to export.arxiv.org as
  recommended. For PDFs, requests go to arxiv.org.
"""
from __future__ import annotations

import argparse
import os
import re
import sys
import time
import urllib.parse
import urllib.request
import xml.etree.ElementTree as ET
from dataclasses import dataclass
from pathlib import Path
from typing import Iterable, List, Optional


ARXIV_API_BASE = "http://export.arxiv.org/api/query"
ARXIV_PDF_BASE = "https://arxiv.org/pdf/"

ATOM_NS = "http://www.w3.org/2005/Atom"
ARXIV_NS = "http://arxiv.org/schemas/atom"
NS = {"atom": ATOM_NS, "arxiv": ARXIV_NS}


@dataclass
class ArxivEntry:
    entry_id_url: str
    arxiv_id: str
    title: str
    pdf_url: Optional[str]


def build_search_query(
    query: Optional[str],
    title: Optional[str],
    author: Optional[str],
    category: Optional[str],
) -> str:
    parts: List[str] = []
    if query:
        # 'all' searches across title, abstract, comments, author, and categories
        q = query.strip()
        if q:
            parts.append(f"all:{_quote_for_field(q)}")
    if title:
        t = title.strip()
        if t:
            parts.append(f"ti:{_quote_for_field(t)}")
    if author:
        a = author.strip()
        if a:
            parts.append(f"au:{_quote_for_field(a)}")
    if category:
        c = category.strip()
        if c:
            parts.append(f"cat:{_quote_for_field(c)}")

    if not parts:
        raise ValueError("At least one of --query/--title/--author/--category must be provided")

    return " +AND+ ".join(parts)


def _quote_for_field(text: str) -> str:
    # For fielded search, spaces are fine. Use quotes when spaces exist.
    # We will URL-encode later; here only wrap in quotes when needed.
    if " " in text:
        return f'"{text}"'
    return text


def query_arxiv_feed(
    *,
    search_query: Optional[str] = None,
    id_list: Optional[Iterable[str]] = None,
    start: int = 0,
    max_results: int = 10,
    sort_by: str = "relevance",
    sort_order: str = "descending",
    user_agent: str = "arxiv-downloader-cli/0.1 (https://github.com/example)",
) -> List[ArxivEntry]:
    """Query arXiv Atom API and return parsed entries."""
    params = {
        "start": str(start),
        "max_results": str(max_results),
        "sortBy": sort_by,
        "sortOrder": sort_order,
    }
    if search_query:
        params["search_query"] = search_query
    if id_list:
        # id_list takes comma-separated IDs; when present, search_query is ignored
        params["id_list"] = ",".join([_normalize_arxiv_id(i) for i in id_list])

    url = f"{ARXIV_API_BASE}?{urllib.parse.urlencode(params)}"

    req = urllib.request.Request(url, headers={"User-Agent": user_agent})
    with urllib.request.urlopen(req, timeout=60) as resp:
        data = resp.read()
    return parse_atom_feed(data)


def parse_atom_feed(xml_bytes: bytes) -> List[ArxivEntry]:
    root = ET.fromstring(xml_bytes)
    entries: List[ArxivEntry] = []

    for e in root.findall("atom:entry", NS):
        entry_id_url = _text(e.find("atom:id", NS)) or ""
        title = (_text(e.find("atom:title", NS)) or "").strip()

        # Determine arXiv ID from the entry id URL, typically .../abs/<id>
        arxiv_id = _extract_arxiv_id_from_abs_url(entry_id_url)

        # Prefer link with title="pdf" or type application/pdf
        pdf_url = None
        for link in e.findall("atom:link", NS):
            rel = link.attrib.get("rel", "")
            href = link.attrib.get("href", "")
            link_type = link.attrib.get("type", "")
            title_attr = link.attrib.get("title", "")
            if title_attr.lower() == "pdf" or link_type == "application/pdf":
                pdf_url = href
                break
            # Some feeds use rel="related" with the pdf URL
            if rel == "related" and href.endswith(".pdf"):
                pdf_url = href
                break

        # Fallback if not found in links
        if not pdf_url and arxiv_id:
            pdf_url = f"{ARXIV_PDF_BASE}{arxiv_id}.pdf"

        entries.append(ArxivEntry(
            entry_id_url=entry_id_url,
            arxiv_id=arxiv_id,
            title=title,
            pdf_url=pdf_url,
        ))

    return entries


def _text(elem: Optional[ET.Element]) -> Optional[str]:
    return elem.text if elem is not None else None


def _extract_arxiv_id_from_abs_url(url: str) -> str:
    # Typical: http://arxiv.org/abs/1706.03762v5 or http://arxiv.org/abs/cs/9901001v1
    m = re.search(r"/abs/([^/#?]+)$", url)
    return m.group(1) if m else url.rsplit("/", 1)[-1]


def _normalize_arxiv_id(arxiv_id: str) -> str:
    return arxiv_id.strip()


def sanitize_filename(name: str, replacement: str = "_") -> str:
    # Remove/replace characters unsafe for filesystems and compress whitespace
    name = re.sub(r"[\\/:*?\"<>|]", replacement, name)
    name = re.sub(r"\s+", " ", name).strip()
    # Limit filename length to something reasonable
    return name[:180]


def ensure_directory(path: Path) -> None:
    path.mkdir(parents=True, exist_ok=True)


def download_file(url: str, destination: Path, *, user_agent: str, timeout: int = 120) -> None:
    tmp_path = destination.with_suffix(destination.suffix + ".part")
    req = urllib.request.Request(url, headers={"User-Agent": user_agent})
    with urllib.request.urlopen(req, timeout=timeout) as resp, open(tmp_path, "wb") as out:
        while True:
            chunk = resp.read(64 * 1024)
            if not chunk:
                break
            out.write(chunk)
    tmp_path.replace(destination)


def entries_from_ids(ids: Iterable[str], *, user_agent: str) -> List[ArxivEntry]:
    id_list = [_normalize_arxiv_id(i) for i in ids]
    # Request metadata for nicer filenames; if it fails, fall back to plain IDs
    try:
        return query_arxiv_feed(id_list=id_list, user_agent=user_agent)
    except Exception:
        return [ArxivEntry(
            entry_id_url=f"https://arxiv.org/abs/{i}",
            arxiv_id=i,
            title=i,
            pdf_url=f"{ARXIV_PDF_BASE}{i}.pdf",
        ) for i in id_list]


def main(argv: Optional[List[str]] = None) -> int:
    parser = argparse.ArgumentParser(description="Download arXiv PDFs by IDs or search query.")
    src = parser.add_mutually_exclusive_group(required=True)
    src.add_argument("--ids", nargs="+", help="One or more arXiv IDs to download (e.g., 1706.03762v5)")
    src.add_argument("--query", help="Free-text query across all fields")

    parser.add_argument("--title", help="Search by title (ti)")
    parser.add_argument("--author", help="Search by author (au)")
    parser.add_argument("--category", help="Search by subject category (cat), e.g., cs.LG")

    parser.add_argument("--max-results", type=int, default=5, help="Max results to download for searches")
    parser.add_argument("--start", type=int, default=0, help="Start index for search pagination")
    parser.add_argument(
        "--sort-by",
        choices=["relevance", "lastUpdatedDate", "submittedDate"],
        default="relevance",
        help="Sort field for search results",
    )
    parser.add_argument(
        "--sort-order",
        choices=["ascending", "descending"],
        default="descending",
        help="Sort order for search results",
    )

    parser.add_argument("--out-dir", help="Output directory for PDFs; defaults to ./downloads next to this script")
    parser.add_argument("--overwrite", action="store_true", help="Overwrite existing files")
    parser.add_argument("--sleep", type=float, default=1.0, help="Seconds to sleep between downloads")
    parser.add_argument("--timeout", type=int, default=120, help="HTTP timeout in seconds for PDF downloads")
    parser.add_argument(
        "--user-agent",
        default="arxiv-downloader-cli/0.1 (+https://github.com/example)",
        help="User-Agent header to send; include contact info if possible",
    )

    args = parser.parse_args(argv)

    # Determine output directory; default to a 'downloads' folder next to this script
    if args.out_dir:
        out_dir = Path(args.out_dir)
    else:
        script_dir = Path(__file__).resolve().parent
        out_dir = script_dir / "downloads"
    ensure_directory(out_dir)

    # Build list of entries to download
    if args.ids:
        entries = entries_from_ids(args.ids, user_agent=args.user_agent)
    else:
        search_query = build_search_query(args.query, args.title, args.author, args.category)
        entries = query_arxiv_feed(
            search_query=search_query,
            start=args.start,
            max_results=args.max_results,
            sort_by=args.sort_by,
            sort_order=args.sort_order,
            user_agent=args.user_agent,
        )

    if not entries:
        print("No entries found.", file=sys.stderr)
        return 2

    # Download each entry
    success = 0
    for idx, entry in enumerate(entries, start=1):
        arxiv_id = entry.arxiv_id or f"entry-{idx}"
        base_name = f"{arxiv_id} - {entry.title or arxiv_id}".strip()
        file_name = sanitize_filename(base_name) + ".pdf"
        dest = out_dir / file_name

        if dest.exists() and not args.overwrite:
            print(f"[skip] {dest.name} (exists)")
            continue

        pdf_url = entry.pdf_url or f"{ARXIV_PDF_BASE}{arxiv_id}.pdf"
        try:
            print(f"[downloading] {arxiv_id}: '{entry.title}' -> {dest}")
            download_file(pdf_url, dest, user_agent=args.user_agent, timeout=args.timeout)
            print(f"[saved] {dest}")
            success += 1
            if args.sleep > 0:
                time.sleep(args.sleep)
        except Exception as ex:
            print(f"[error] Failed to download {arxiv_id} from {pdf_url}: {ex}", file=sys.stderr)

    print(f"Done. {success}/{len(entries)} file(s) downloaded to {out_dir}")
    return 0 if success > 0 else 1


if __name__ == "__main__":
    raise SystemExit(main())

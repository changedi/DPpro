import io
import re
import sys
import tempfile
import unittest
from pathlib import Path
from unittest.mock import patch

# Ensure we can import the module from the same directory
THIS_DIR = Path(__file__).resolve().parent
if str(THIS_DIR) not in sys.path:
    sys.path.insert(0, str(THIS_DIR))

import arxiv_downloader  # noqa: E402


class FakeResponse:
    def __init__(self, content: bytes):
        self._content = content
        self._pos = 0

    def read(self, n: int = -1) -> bytes:
        if n is None or n < 0:
            if self._pos >= len(self._content):
                return b""
            data = self._content[self._pos :]
            self._pos = len(self._content)
            return data
        if self._pos >= len(self._content):
            return b""
        end = min(self._pos + n, len(self._content))
        data = self._content[self._pos : end]
        self._pos = end
        return data

    def __enter__(self):
        return self

    def __exit__(self, exc_type, exc, tb):
        return False


def make_fake_urlopen(feed_bytes: bytes, pdf_bytes: bytes):
    def fake_urlopen(request, timeout=60):  # signature compatible enough
        try:
            url = request.full_url
        except AttributeError:
            url = request
        if "api/query" in url:
            return FakeResponse(feed_bytes)
        if url.endswith(".pdf"):
            return FakeResponse(pdf_bytes)
        raise AssertionError(f"Unexpected URL in test fake: {url}")

    return fake_urlopen


FEED_XML_ONE = b"""<?xml version="1.0" encoding="UTF-8"?>
<feed xmlns="http://www.w3.org/2005/Atom" xmlns:arxiv="http://arxiv.org/schemas/atom">
  <entry>
    <id>http://arxiv.org/abs/1234.5678v1</id>
    <title>Test Paper</title>
    <link rel="alternate" type="text/html" href="http://arxiv.org/abs/1234.5678v1"/>
    <link title="pdf" href="https://arxiv.org/pdf/1234.5678v1.pdf"/>
  </entry>
</feed>
"""


class TestArxivDownloader(unittest.TestCase):
    def test_build_search_query(self):
        q = arxiv_downloader.build_search_query(
            query="large language models", title="Graph", author="Hinton", category="cs.LG"
        )
        self.assertEqual(
            q,
            'all:"large language models" +AND+ ti:Graph +AND+ au:Hinton +AND+ cat:cs.LG',
        )

    def test_parse_atom_feed_extracts_entry(self):
        entries = arxiv_downloader.parse_atom_feed(FEED_XML_ONE)
        self.assertEqual(len(entries), 1)
        e = entries[0]
        self.assertEqual(e.arxiv_id, "1234.5678v1")
        self.assertEqual(e.title, "Test Paper")
        self.assertTrue(e.pdf_url.endswith("1234.5678v1.pdf"))

    def test_sanitize_filename(self):
        unsafe = 'a<>:"/\\|?*  name.pdf'
        safe = arxiv_downloader.sanitize_filename(unsafe)
        self.assertNotRegex(safe, r"[\\/:*?\"<>|]")
        self.assertNotIn("  ", safe)
        self.assertTrue(safe.endswith("name.pdf"))

    def test_download_via_main_query(self):
        pdf_bytes = b"%PDF-FAKE%\ncontent\n"
        fake = make_fake_urlopen(feed_bytes=FEED_XML_ONE, pdf_bytes=pdf_bytes)
        with tempfile.TemporaryDirectory() as td,
             patch("arxiv_downloader.urllib.request.urlopen", side_effect=fake):
            ret = arxiv_downloader.main(
                [
                    "--query",
                    "anything",
                    "--max-results",
                    "1",
                    "--out-dir",
                    td,
                    "--sleep",
                    "0",
                ]
            )
            self.assertEqual(ret, 0)
            out_files = list(Path(td).glob("*.pdf"))
            self.assertEqual(len(out_files), 1)
            self.assertEqual(out_files[0].name, "1234.5678v1 - Test Paper.pdf")
            self.assertEqual(out_files[0].read_bytes(), pdf_bytes)

    def test_main_ids_skip_existing_without_overwrite(self):
        pdf_bytes = b"existing"
        fake = make_fake_urlopen(feed_bytes=FEED_XML_ONE, pdf_bytes=b"new")
        with tempfile.TemporaryDirectory() as td:
            expected = Path(td) / "1234.5678v1 - Test Paper.pdf"
            expected.write_bytes(pdf_bytes)
            with patch("arxiv_downloader.urllib.request.urlopen", side_effect=fake):
                ret = arxiv_downloader.main([
                    "--ids",
                    "1234.5678v1",
                    "--out-dir",
                    td,
                    "--sleep",
                    "0",
                ])
            self.assertEqual(ret, 1)  # skipped existing, no new downloads
            self.assertEqual(expected.read_bytes(), pdf_bytes)


if __name__ == "__main__":
    unittest.main(verbosity=2)

package com.jybat.dp;

import java.util.SortedSet;
import java.util.TreeSet;

//deadline scheduling of unit-time jobs
public class DEADLINE {

	private static int[] profit = { 10, 15, 20, 1, 5 };
	private static int[] deadline = { 1, 2, 2, 3, 3 }; // sorted
	private static int N = profit.length; // no.of jobs

	private static SortedSet<Integer> setOfAllJobs = new TreeSet<Integer>();

	public DEADLINE() {
		for (int i = 0; i < N; i++) {
			setOfAllJobs.add(i);
		}
	}

	private static boolean feasible(SortedSet<Integer> jobs, int k, int d) {
		int j = 0;
		for (int i = 0; i < N; i++) {
			if (!(jobs.contains(new Integer(i))) || i == d) {
				// if i already chosen or next (and is j-th),
				// does it meet its deadline?
				if (deadline[i] < ++j) {
					return false;
				}
			}
		}
		return true;
	}

	private static int cost(SortedSet<Integer> jobs, int k, int d) {
		if (feasible(jobs, k, d)) {
			return profit[d];
		} else {
			return 0;
		}
	}

	// jobs not yet chosen at stage k
	public int f(SortedSet<Integer> jobs, int k) {
		if (k > N)
			return 0;
		int max = Integer.MIN_VALUE;
		for (int d : jobs) {
			SortedSet<Integer> nJobs = copySet(jobs);
			nJobs.remove(d);
			int t = cost(jobs, k, d) + f(nJobs, k + 1);
			if (t > max)
				max = t;
		}
		return max;
	}

	private SortedSet<Integer> copySet(SortedSet<Integer> jobs) {
		SortedSet<Integer> nJobs = new TreeSet<Integer>();
		for (int i : jobs) {
			nJobs.add(i);
		}
		return nJobs;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		DEADLINE d = new DEADLINE();
		System.out.println(d.f(setOfAllJobs, 1));
	}

}

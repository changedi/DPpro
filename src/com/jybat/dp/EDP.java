/*
 * Copyright (C) 2013 changedi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jybat.dp;

//EditDistanceProblem;
public class EDP {

	private static String s1 = "CAN";
	private static String s2 = "ANN";
	private static final int insertionCost = 1;
	private static final int deletionCost = 1;
	private static final int replacementCost = 1;
	// it is useful to have the string lengths as symbolic constants
	private static int s1Length = s1.length();
	private static int s2Length = s2.length();

	// The decision space is constant in this example.
	// It does not depend on the current state.
	// We chose to code the 3 allowed operations
	// as follows:
	// 1 stands for delete
	// 2 stands for insert
	// 12 stands for replace
	private static int[] decisionSet = { 1, 2, 12 };

	// costOfOperation()
	// returns 0 if the specified characters in the 2 strings
	// match and the decision is to perform
	// "replacement" operation for matching chars
	// (whose cost is usually defined as 0)
	// returns 1 otherwise (if delete, insert, or a real replacement operation
	// happens)
	private static int costOfOperation(int i, int j, int dec) {
		if (dec == 12) { // dec==12 means decision is to replace
			if (s1.charAt(i - 1) // note: subtract 1 because array
			// starts at index 0
			== s2.charAt(j - 1)) { // matching chars, cost is 0
				return 0;
			} else { // real replacement
				return replacementCost; // cost of real replacement
			}
		}
		if (dec == 1) { // dec==1 means decision is to delete
			return deletionCost;
		}
		// otherwise it must be that dec==2, decision to insert
		return insertionCost;
	}

	private static int s1Adjustment(int dec) {
		if (dec == 2) { // insert
			return 0;
		}
		return 1;
	}

	private static int s2Adjustment(int dec) {
		if (dec == 1) { // delete
			return 0;
		}
		return 1;
	}

	public static int d(int i, int j) {
		if (i == 0)
			return j;
		if (j == 0)
			return i;
		int min = Integer.MAX_VALUE;
		for (int dec : decisionSet) {
			int t = costOfOperation(i, j, dec)
					+ d(i - s1Adjustment(dec), j - s2Adjustment(dec));
			if (t < min)
				min = t;
		}
		return min;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println(d(s1Length, s2Length));

	}

}

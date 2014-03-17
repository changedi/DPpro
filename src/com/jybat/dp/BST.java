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

import java.util.SortedSet;
import java.util.TreeSet;

public class BST {

	private int[] data = { 0, 1, 2, 3, 4 };// assume the n data items are
											// the ints {0,1,..,n-1}
											// corresponding to strings
											// { "A", "B", "C", "D",
											// "E"}
	private double[] probability = { 0.25, 0.05, 0.2, 0.4, 0.1 };

	private static SortedSet<Integer> setOfAllItems = new TreeSet<Integer>();

	public BST() {
		for (int i = 0; i < data.length; i++)
			setOfAllItems.add(data[i]);
	}

	private double sumOfProbabilitiesOfItems(SortedSet items) {
		double result = 0.0;
		for (int i = ((Integer) items.first()).intValue(); i <= ((Integer) items
				.last()).intValue(); i++) {
			result += probability[i];
		}
		return result;
	}

	private SortedSet setOfItemsLessThanPivot(SortedSet items, int alpha) {
		// conveniently use method headSet() from SortedSet
		// headSet() DOES NOT include alpha
		return new TreeSet(items.headSet(new Integer(alpha)));
	}

	private SortedSet setOfItemsGreaterThanPivot(SortedSet items, int alpha) {
		// conveniently use method tailSet() from SortedSet
		// headSet() DOES include alpha
		SortedSet ss = new TreeSet(items.tailSet(new Integer(alpha)));
		ss.remove(alpha);
		return ss;
	}

	public double f(SortedSet<Integer> items) {
		if (items.size() == 0)
			return 0.0;
		double min = Double.MAX_VALUE;
		for (int a : items) {
			double t = sumOfProbabilitiesOfItems(items)
					+ f(setOfItemsLessThanPivot(items, a))
					+ f(setOfItemsGreaterThanPivot(items, a));
			if (t < min)
				min = t;
		}
		return min;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		BST bst = new BST();
		System.out.println(bst.f(setOfAllItems));
	}

}

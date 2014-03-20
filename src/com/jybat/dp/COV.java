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

//optimal COVering problem
public class COV {
	// cost of cover sizes 0,1,..,9 in dollar
	private static int[] cost = { 1, 4, 5, 7, 8, 12, 13, 18, 19, 21 };

	public static double f(int numberOfCoverSizes, int largestSize) {
		//numberOfCoverSizes denotes the stage number in this problem		
		if (numberOfCoverSizes == 1)
			return (largestSize + 1) * cost[largestSize];
		double min = Double.MAX_VALUE;
		for (int nextCoverSize = numberOfCoverSizes - 2; nextCoverSize <= largestSize - 1; nextCoverSize++) {
			double t = (largestSize - nextCoverSize) * cost[largestSize]
					+ f(numberOfCoverSizes - 1, nextCoverSize);
			if (t < min)
				min = t;
		}
		return min;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println(f(3, 9));
	}

}

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

public class ASMBAL {

	private static int[][] cost = // (stage,line)
	{ 
			{ 2, 4 }, // to next line
			{ 2, 2 }, { 1, 3 }, { 2, 1 }, { 2, 3 }, { 1, 4 }, // to next line
			{ 3, 2 } // from line
	};
	private static int[][] vertexcost = // (stage,line)
	{ 
		{ 0, 0 }, { 7, 8 }, { 9, 5 }, { 3, 6 }, { 4, 4 }, { 8, 5 }, { 4, 7 } 
	};
	private static int N = vertexcost.length - 1;

	private static int arccost(int g, int x, int d) {
		if (g == 0)
			return cost[g][d]; // to next line d
		else if (g == N)
			return cost[g][x]; // from line x
		else if (x == d)
			return 0; // stay same line
		else
			return cost[g][d]; // to next line d
	}
	
	public static double f(int g, int x) {
		if (g > N)
			return 0.0;
		double min = Double.MAX_VALUE;
		for (int d = 0; d <= 1; d++) {
			double c = vertexcost[g][x] + arccost(g, x, d) + f(g + 1, d);
			if (c < min)
				min = c;
		}
		return min;
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println(f(0,0));
	}

}

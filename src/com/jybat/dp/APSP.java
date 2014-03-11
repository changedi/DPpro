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

public class APSP {

	private static final int INF = Integer.MAX_VALUE;

	// nodes (s,x,y,t) = {0,1,2,3}, so (s,x) = 3 means d[0][1] = 3
	private static int[][] distance = { 
		{ INF, 5, 	 3, 	INF }, 
		{ INF, INF, 2, 	5 },
		{ INF, 1,  	 INF, 8 }, 
		{ INF, INF, INF, INF } 
	};
	
	private static int maxNodeIndex = distance.length-1;
	
	private static int[] possibleSuccessors = { 0, 1 };
	
	public static double f(int k, int p, int q) {
		if (k == 0 && p == q)
			return 0;
		if (k == 0 && p != q)
			return distance[p][q];
		double min = Double.MAX_VALUE;
		for (int d : possibleSuccessors) {
			double t = (1 - d) * f(k - 1, p, q) + d * f(k - 1, p, k) + d
					* f(k - 1, k, q);
			if (t < min)
				min = t;
		}
		return min;
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println(f(maxNodeIndex,0,3));
	}

}

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

import java.util.HashSet;
import java.util.Set;

public class ShortestPathAcyclic {

	private static final int INF = Integer.MAX_VALUE;

	// nodes (s,x,y,t) = {0,1,2,3}, so (s,x) = 3 means d[0][1] = 3
	private static int[][] distance = { 
		{ INF, 3, 	 5, 	INF }, 
		{ INF, INF, 1, 	8 },
		{ INF, INF, INF, 5 }, 
		{ INF, INF, INF, INF } 
	};

	private static Set<Integer> possibleNextNodes(int node) {
		Set<Integer> result = new HashSet<Integer>();
		for (int i = 0; i < distance[node].length; i++) {
			if (distance[node][i] != INF) {
				result.add(new Integer(i));
			}
		}
		return result;
	}
	
	public static double f(int currentNode){
		if(currentNode==3) return 0.0;
		else{
			Set<Integer> possibleSuccessors = possibleNextNodes(currentNode);
			double min = Double.MAX_VALUE;
			for(Integer d : possibleSuccessors){
				double dis = distance[currentNode][d]+f(d);
				if(dis<min) {
					min = dis;
				}
			}
			return min;
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		double shortest = f(0);
		System.out.println(shortest);
	}

}

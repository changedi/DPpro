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

public class ShortestPathCyclic {

	private static final int INF = Integer.MAX_VALUE;

	private static Set<Integer> path = new HashSet<Integer>();
	// nodes (s,x,y,t) = {0,1,2,3}, so (s,x) = 3 means d[0][1] = 3
	private static int[][] distance = { 
		{ INF, 5, 	 3, 	INF }, 
		{ INF, INF, 2, 	5 },
		{ INF, 1,  	 INF, 8 }, 
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
	
	public static double f(int currentNode, Set<Integer> nodesVisited){
		if(currentNode==3) return 0.0;
		else{
			Set<Integer> possibleSuccessors = possibleNextNodes(currentNode);
			possibleSuccessors.removeAll(nodesVisited);
			double min = Double.MAX_VALUE;
			for(Integer a : possibleSuccessors){
				Set<Integer> set = new HashSet<Integer>();
				set.addAll(nodesVisited);
				set.add(a);
				double dis = distance[currentNode][a]+f(a,set);
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
		path.add(0);
		double shortest = f(0,path);
		System.out.println(shortest);
	}

}

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

import java.util.HashMap;
import java.util.Map;

public class ALLOT {

	private static int N = 3;
	private static int M = 4;
	
	private static final double INF=Double.MAX_VALUE;
	
	private static Map<Integer,Integer> choices = new HashMap<Integer,Integer>();
	
	private static final double [][] c = {
		{INF,1.0,0.8,0.4,0.0},
		{INF,1.0,0.5,0.0,0.0},
		{INF,1.0,0.6,0.3,0.0}
	};
	
	private static double cost(int k, int d) {
		return c[k-1][d];
	}
	
	public static double f(int k, int m){
		if(k>N&&m>=0) return 0.0;
		else{
			double mm = Double.MAX_VALUE;
			int dispatch = -1;
			for(int d=0;d<=m;d++){
				double t = cost(k,d)+f(k+1,m-d);
				if(t<mm) {
					mm = t;
					dispatch = d;
				}
			}
			if(!choices.containsKey(k))
				choices.put(k, Integer.MAX_VALUE);
			if(mm<choices.get(k))
				choices.put(k,dispatch);				
			return mm;
		}
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		double minCost = f(1,M);
		System.out.println("min cost is : "+minCost);
		System.out.print("solution is : ");
		for(int i=1;i<=N;i++){			
			System.out.print(choices.get(i));
			if(i<N)
				System.out.print(",");
		}
	}

}

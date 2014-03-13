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

import java.util.Arrays;

public class ARC {

	private static double[] weight = { 2, 3, 1, 4 };

	private static int N = weight.length - 1;

	private static double sum(int p, int q) {
		double result = 0.0;
		for (int k = p; k <= q; k++) {
			result += weight[k];
		}
		return result;
	}
	
	public static double f(int firstIndex, int lastIndex){
		if(firstIndex == lastIndex)
			return 0.0;
		double min = Double.MAX_VALUE;
		for(int d=firstIndex;d<lastIndex;d++){
			double t = f(firstIndex,d) + f(d+1,lastIndex) + sum(firstIndex,lastIndex);
			if(t<min)
				min = t;
		}
		return min;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Arrays.sort(weight);
		System.out.println(f(0,N));
	}

}

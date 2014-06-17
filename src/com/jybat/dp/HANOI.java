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

//tower of hanoi problem
//Compute the number of moves when the recursive
//strategy is used. 
public class HANOI {

	// m: number of disks to move
	// i: index of source tower
	// j: index of destination tower
	// k: index of temporary tower
	public static int f(int m, int i, int j, int k) {
		if (m == 1)
			return 1;
		return f(m - 1, i, k, j) + f(1, i, j, k) + f(m - 1, k, j, i);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println(f(3,1,2,3));
	}

}

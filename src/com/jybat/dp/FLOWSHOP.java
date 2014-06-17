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

//Flowshop Problem
public class FLOWSHOP {

	private static int[] first = { 3, 4, 8, 10 }; // sum=25
	private static int[] second = { 6, 2, 9, 15 }; // sum=32
	// upper bound on final completion time is 25+32:
	private static int sum = 57;
	private static int m = first.length;

	private static Set<Integer> setOfAllItems = new HashSet<Integer>();
	
	static{
		setOfAllItems.add(0);
		setOfAllItems.add(1);
		setOfAllItems.add(2);
		setOfAllItems.add(3);
	}
	
	private static int fct(int t, int d) {
		return Math.max(t - first[d], 0) + second[d];
	}
	
	public static double f(Set<Integer> set, int t){
		if(set.isEmpty())
			return t;
		double min = Double.MAX_VALUE;
		for(int d : set){
			Set<Integer> tmpSet = copySet(set);
			tmpSet.remove(d);
			double tmp = first[d]+f(tmpSet,fct(t,d));
			if(tmp<min){
				min=tmp;
			}
		}
		return min;
	}

	private static Set<Integer> copySet(Set<Integer> set) {
		Set<Integer> s = new HashSet<Integer>();
		for(int x : set){
			s.add(x);
		}
		return s;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println(f(setOfAllItems,0));
	}

}

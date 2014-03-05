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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class LinearSearch {

	int N = 3;
	Map<String,Double> map = new HashMap<String,Double>();

	public LinearSearch(){
		map.put("a", 0.2);
		map.put("b", 0.5);
		map.put("c", 0.3);
	}
	public void dp() {
		Set<String> A = new HashSet<String>();
		for (String k : map.keySet()) {
			A.add(k);
		}
		System.out.println(f(A));
	}

	private double cost(String x, Set<String> A) {
		return map.get(x) * (N + 1 - A.size());
	}

	private double f(Set<String> A) {
		double fx = Double.MAX_VALUE;
		if(A.isEmpty()){
			fx = 0;
		}
		else{
			for(String key : A){
				Set<String> A1 = new HashSet<String>();
				for(String ik : A){
					A1.add(ik);
				}
				A1.remove(key);				
				double tmp = cost(key,A) + f(A1);
				if(tmp<fx)
					fx = tmp;
			}
		}
		return fx;
	}

	public static void main(String[] args) {
		LinearSearch ls = new LinearSearch();
		ls.dp();
	}

}

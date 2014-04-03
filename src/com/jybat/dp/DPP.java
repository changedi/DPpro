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

//Discounted Profits Problem
//A discounted DP problem from Winston/Venkataramanan
//pp.779--780
//Used a reproductionFactor of 2 instead of 1.2 so number of
//fish takes on integral values, without the need to use a
//round or a floor function.
public class DPP {
	private static int T = 2; // planning horizon T=2 years
	private static double interestRate = 0.05; // assume 5% interest rate
	private static int initialFishAmount = 10; // initially 10,000 fish in lake
	private static int reproductionFactor = 2; // in 1 year 100% more fish

	private static double revenue(int xt) {
		// simply assume that the sale price is $3 per fish, no matter what
		return 3.0 * xt;
	}

	private static double cost(int xt, int b) {
		// simply assume that it costs $2 to catch (and process) a fish, no
		// matter what
		return 2.0 * xt;
	}

	// t is stage number, each stage represents one year
	// b is current number of fish in lake (scaled to thousands)
	public static double f(int t, int b) {
		if (t == T + 1)// T+1 is outside planning horizon
			return 0.0;
		// xt is the number of fish to catch and sell
		// during year t
		int xt;
		double max = Double.MIN_VALUE;
		for (xt = 0; xt <= b; xt++) {
			double earn = revenue(xt) - cost(xt, b) + 1 / (1 + interestRate)
					* f(t + 1, reproductionFactor * (b - xt));
			if (earn > max)
				max = earn;
		}
		return max;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println(f(1, initialFishAmount));
	}

}

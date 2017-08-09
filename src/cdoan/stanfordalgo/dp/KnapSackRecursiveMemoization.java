package cdoan.stanfordalgo.dp;


import java.util.*;
import java.io.*;


/**
 * Question 2 of Programming Assignment 4 of Course 3.
 */
public class KnapSackRecursiveMemoization {
	
	private static class Subproblem {
		private int prefixLen;
		private int remainingSize;
		
		private Subproblem(int prefixLen, int remainingSize) {
			this.prefixLen = prefixLen;
			this.remainingSize = remainingSize;
		}
		
		public int hashCode() {
			return Objects.hash(prefixLen, remainingSize);
		}
		
		public boolean equals(Object obj) {
			if (obj == this) {
				return true;
			}
			if (obj == null || obj.getClass() != Subproblem.class) {
				return false;
			}
			Subproblem that = (Subproblem) obj;
			return prefixLen == that.prefixLen && remainingSize == that.remainingSize;
		}
	}

	public static long solve(int[] v, int[] w, int W) {
		Map<Subproblem, Long> cache = new HashMap<>();
		return solve(v, w, v.length - 1, W, cache);
	}
	
	private static Long solve(int[] v, int[] w, int prefixLen, int remainingSize, Map<Subproblem, Long> cache) {
		// Base case
		if (prefixLen == 0) {
			return 0L;
		}
		
		Subproblem curr = new Subproblem(prefixLen, remainingSize);
		Long sol = cache.get(curr);
		if (sol != null) {
			return sol;
		}
		
		sol = solve(v, w, prefixLen - 1, remainingSize, cache);
		if (remainingSize - w[prefixLen] >= 0) {
			sol = Math.max(sol, v[prefixLen] + solve(v, w, prefixLen - 1, remainingSize - w[prefixLen], cache));
		}
		cache.put(curr, sol);
		return sol;
	}
	

	public static void main(String[] args) throws IOException {
		if (args.length == 0) {
			System.out.println("Usage: java KnapSackRecursiveMemoization <input-data-file>");
			System.exit(1);
		}

		Scanner s = new Scanner(new File(args[0]));
		final int W = s.nextInt();
		final int N = s.nextInt() + 1;
		int[] v = new int[N], w = new int[N];
		for (int i = 1; i < N; i++) {
			v[i] = s.nextInt();
			w[i] = s.nextInt();
		}
		s.close();

		System.out.println("optimal solution value: " + solve(v, w, W));
	}

}

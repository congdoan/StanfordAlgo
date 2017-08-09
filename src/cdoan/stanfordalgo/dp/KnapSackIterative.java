package cdoan.stanfordalgo.dp;


import java.util.*;
import java.io.*;


/**
 * Question 1 of Programming Assignment 4 of Course 3.
 */
public class KnapSackIterative {

  public static long solve(int[] v, int[] w, int W) {
    final int N = v.length;
    long[][] A = new long[N][W+1];
    for (int i = 1; i < N; i++) {
      for (int x = 1; x <= W; x++) {
        if (x - w[i] < 0) {
          A[i][x] = A[i-1][x];
        } else {
          A[i][x] = Math.max(A[i-1][x], A[i-1][x-w[i]] + v[i]);
        }
      }
    }
    return A[N-1][W];
  }

  public static void main(String[] args) throws IOException {
    if (args.length == 0) {
      System.out.println("Usage: java KnapSackIterative <input-data-file>");
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

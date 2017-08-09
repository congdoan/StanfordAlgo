package cdoan.stanfordalgo.dp;


import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;


/**
 * Question 3 of Programming Assignment 3 of Course 3.
 */
public class MaxWeightIndependentSet {
  
  private Set<Integer> mwis;
  
  public MaxWeightIndependentSet(int[] weights) {
    // Compute the maximum weight sum over all independent sets
    final int N = weights.length;
    long[] A = new long[N]; // A[i] tells the maximum weight sum over all independent sets of the i first vertices
    A[0] = 0;
    A[1] = weights[1];
    for (int i = 2; i < N; i++) {
      A[i] = Math.max(A[i-1], A[i-2] + weights[i]);
    }
    
    // Reconstruct the maximum weight independent set itself
    mwis = new HashSet<>();
    int i = N-1;
    while (i >= 2) {
      if (A[i-1] >= A[i-2] + weights[i]) {
        i--;
      } else {
        mwis.add(i);
        i -= 2;
      }
    }
    if (i == 1) {
      mwis.add(1);
    }
  }
  
  public String includedStatusString(int[] vertices) {
    StringBuilder sb = new StringBuilder();
    for (int v: vertices) {
      sb.append(mwis.contains(v) ? "1" : "0");
    }
    return sb.toString();
  }
  
  public static void main(String[] args) throws IOException {
    if (args.length == 0) {
      System.out.println("Usage: cdoan.stanfordalgo.dp.MaxWeightIndependentSet <input-vertex-weight-list-file>");
      System.exit(1);
    }
    
    Scanner s = new Scanner(new File(args[0]));
    final int N = s.nextInt() + 1;
    int[] weights = new int[N];
    for (int i = 1; i < N; i++) {
      weights[i] = s.nextInt();
    }
    s.close();
    
    MaxWeightIndependentSet mwis = new MaxWeightIndependentSet(weights);
    int[] vertices = {1, 2, 3, 4, 17, 117, 517, 997};
    System.out.println(mwis.includedStatusString(vertices));
  }
  
}

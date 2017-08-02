package cdoan.stanfordalgo.greedy;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

/**
 * Question 1 of programming assignment 2 of course 3.
 */
public class MaxSpacingClustering {
  
  public static class EagerUnionUF {
    private int[] id;                  // id[i] = group identifier of i
    private List<List<Integer>> group; // group.get(i) = members of i's group
    private int size;                  // number of groups
    
    public EagerUnionUF(int n) {
      id = new int[n];
      group = new ArrayList<>();
      List<Integer> tmp;
      for (int i = 0; i < n; i++) {
        id[i] = i;
        tmp = new LinkedList<>();
        tmp.add(i);
        group.add(tmp);
      }
      size = n;
    }
    
    public boolean connected(int i, int j) {
      return id[i] == id[j];
    }
    
    public void union(int i, int j) {
      if (id[i] == id[j]) {
        return;
      }
      /* Merge smaller group into bigger one*/
      int small = id[i];
      int big = id[j];
      if (group.get(id[i]).size() > group.get(id[j]).size()) {
        small = id[j];
        big = id[i];
      }
      for (int s: group.get(small)) {
        id[s] = big;
      }
      List<Integer> join = group.get(big);
      join.addAll(group.get(small));
      for (int u: join) {
        group.set(u, join);
      }
      size--;
    }
    
    public int size() {
      return size;
    }
  }
  
  private static class Pair implements Comparable<Pair> {
    private int p;
    private int q;
    private int dist;
    
    private Pair(int p, int q, int dist) {
      this.p = p;
      this.q = q;
      this.dist = dist;
    }
    
    public int compareTo(Pair another) {
      return dist - another.dist;
    }
  }
  
  
  public static int doClustering(int numPoints, Pair[] pairs, int k) {
    if (numPoints < k || k < 1) {
      throw new IllegalArgumentException();
    }
    
    Arrays.sort(pairs);
    EagerUnionUF uf = new EagerUnionUF(numPoints);
    int i = 0;
    while (uf.size() > k) {
      uf.union(pairs[i].p, pairs[i++].q);
    }
    while (uf.connected(pairs[i].p, pairs[i].q)) {
      i++;
    }
    System.out.printf("close pair of separated nodes:%d %d %d%n", pairs[i].p + 1, pairs[i].q + 1, pairs[i].dist);
    return pairs[i].dist;
  }
  
  /**
   * Read input data from file then do clustering.
   */
  public static void main(String[] args) throws IOException {
    if (args.length == 0) {
      System.out.println("Usage: java cdoan.stanford.greedy.MaxSpacingClustering <input-data-file>");
      System.exit(1);
    }
    
    // Prepare data
    Scanner s = new Scanner(new File(args[0]));
    int numPoints = s.nextInt();
    int numPairs = (numPoints * (numPoints - 1)) / 2;
    Pair[] pairs = new Pair[numPairs];
    for (int i = 0; i < numPairs; i++) {
      pairs[i] = new Pair(s.nextInt() - 1, s.nextInt() - 1, s.nextInt());
    }
    s.close();
    
    // Call to do clustering
    int maxSpacing = doClustering(numPoints, pairs, 4);
    System.out.println("maxSpacing: " + maxSpacing);
  }
  
}

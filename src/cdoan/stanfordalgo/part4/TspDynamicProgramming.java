package cdoan.stanfordalgo.part4;


import java.io.*;
import java.util.*;


/**
 * Implement the Dynamic Programming algorithm to exactly solve the Traveling Salesman Problem.
 */
public class TspDynamicProgramming {
  
  private static class Point {
    private double x;
    private double y;
    
    private Point(double x, double y) {
     this.x = x;
     this.y = y;
    }
  }
  
  private static class Key {
    private Set<Integer> S;
    private Integer j;
    
    private Key(Set<Integer> S, Integer j) {
     this(S, j, false);
    }
    
    private Key(Set<Integer> S, Integer j, boolean noCopyS) {
     if (noCopyS) {
       this.S = S;
     } else {
       this.S = new HashSet<>(S);
     }
     this.j = j;
    }
    
    public int hashCode() {
     return Objects.hash(S, j);
    }
    
    public boolean equals(Object obj) {
     if (this == obj) {
       return true;
     }
     if (obj == null || obj.getClass() != Key.class) {
       return false;
     }
     Key that = (Key) obj;
     return S.equals(that.S) && j.equals(that.j);
    }
    
    public String toString() {
     return String.format("[%s, %d]", S, j);
    }
  }
  
  private static class CombinationsOfSizeIterator<E> implements Iterator<Set<E>> {
    private final E[] set;
    private final int n;
    private final int k;
    private final int[] idxArr;
    private boolean hasNext;
    
    private CombinationsOfSizeIterator(E[] set, int k) {
     this.set = set;
     n = set.length;
     this.k = k;
     idxArr = new int[k];
     for (int i = 0; i < k; i++) {
       idxArr[i] = i;
     }
     hasNext = true;
    }
    
    public boolean hasNext() {
     return hasNext;
    }
    
    public Set<E> next() {
     if (!hasNext) {
       throw new NoSuchElementException();
     }
     
     Set<E> subset = new HashSet<>(k);
     for (int i: idxArr) {
       subset.add(set[i]);
     }
     
     /* In idxArr find the right-most element whose value can be incremented by 1 */
     int i = k - 1;
     while ((idxArr[i] + 1 >= n) || (i + 1 < k && idxArr[i] + 1 >= idxArr[i + 1])) {
       i--;
       if (i < 0) {
        hasNext = false;
        break;
       }
     }
     /* Update idxArr[i..k-1] */
     if (hasNext) { // i.e. i >= 0
       idxArr[i]++;
       for (i++; i < k; i++) {
        idxArr[i] = idxArr[i - 1] + 1;
       }
     }
     
     
     return subset;
    }
    
    public void remove() {
     throw new UnsupportedOperationException();
    }
  }
  
  private static class CombinationsOfSize<E> implements Iterable<Set<E>> {
    private final E[] set;
    private final int k;
    
    private CombinationsOfSize(E[] set, int k) {
     this.set = set;
     this.k = k;
    }
    
    public Iterator<Set<E>> iterator() {
     return new CombinationsOfSizeIterator<E>(set, k);
    }
  }
  
  
  public static double tourMinCost(Point[] points) {
    final int n = points.length;
    Map<Key, Double> A = new HashMap<>((1 << (n-1)) - 1);
    
    /* Main loop (triple for loop) */
    Set<Integer> oneVertexSet;
    for (int v = 1; v < n; v++) {
      oneVertexSet = new HashSet<>(1);
      oneVertexSet.add(v);
      A.put(new Key(oneVertexSet, v), dist(points, 0, v));
    }
    Integer[] set = new Integer[n - 1];
    for (int val = 1; val < n; val++) {
      set[val - 1] = val;
    }
    for (int m = 2; m < n; m++) {
      for (Set<Integer> S: new CombinationsOfSize<Integer>(set, m)) {
        List<Integer> copyOfS = new LinkedList<>(S);
        for (Integer j: copyOfS) {
         double minOverK = Double.MAX_VALUE;
         for (Integer k: copyOfS) {
           if (!k.equals(j)) {
            S.remove(j);
            minOverK = Math.min(minOverK, A.get(new Key(S, k)) + dist(points, k, j));
            S.add(j);
           }
         }
         A.put(new Key(S, j, true), minOverK);
        }
      }
    }
    
    /* Return cost of an optimal TSP tour */
    Set<Integer> allExceptStartVertex0 = new HashSet<>(n-1);
    for (int v = 1; v < n; v++) {
      allExceptStartVertex0.add(v);
    }
    double minCost = A.get(new Key(allExceptStartVertex0, 1, true)) + dist(points, 1, 0);
    for (int j = 2; j < n; j++) {
      minCost = Math.min(minCost, A.get(new Key(allExceptStartVertex0, j, true)) + dist(points, j, 0));
    }
    return minCost;
  }
  
  private static double dist(Point[] points, int i, int j) {
    double dx = points[i].x - points[j].x, dy = points[i].y-points[j].y;
    return Math.sqrt(dx*dx + dy*dy);
  }
  
  
  public static void main(String[] args) throws IOException {
    if (args.length == 0) {
     System.out.println("Usage: java cdoan.stanfordalgo.part4.TspDynamicProgramming <tsp-instance-file>");
     System.exit(1);
    }
    
    Scanner s = new Scanner(new File(args[0]));
    int n = s.nextInt();
    //+ debug: read input size
    if (args.length > 1) {
      n = Integer.parseInt(args[1]);
    }
    //-
    Point[] points = new Point[n];
    for (int i = 0; i < n; i++) {
     points[i] = new Point(s.nextDouble(), s.nextDouble());
    }
    s.close();
    
    System.out.printf("Total Minimum Distance: %f%n", tourMinCost(points));
  }
  
}

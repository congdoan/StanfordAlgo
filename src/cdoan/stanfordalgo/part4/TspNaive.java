package cdoan.stanfordalgo.part4;


import java.io.*;
import java.util.*;


/**
 * Implement the Brute-Force algorithm to exactly solve the Traveling Salesman Problem.
 * Time complexity is O((n-1)!), so the maximum input size it can handle is about n = 14.
 */
public class TspNaive {
  
  private static class PermIterator implements Iterator<int[]> {
    private final int n;
    private final int[] a;
    private final long halfNumPerms;
    private long currPerm;
    
    private PermIterator(final int n) {
      if (n < 2) {
        throw new IllegalArgumentException("n less than 2: " + n);
      }
      this.n = n;
      a = new int[n];
      for (int i = 0; i < n; i++) {
        a[i] = i + 1;
      }
      long tmp = 1L;
      for (int i = 3; i <= n; i++) {
        tmp *= i;
      }
      halfNumPerms = tmp;
      currPerm = 1;
    }
    
    public boolean hasNext() {
      return currPerm <= halfNumPerms;
    }
    
    public int[] next() {
      if (currPerm > halfNumPerms) {
        throw new NoSuchElementException();
      }
      
      int[] copy = Arrays.copyOf(a, n);
      // 1. Find the largest index k such that a[k] < a[k+1]. If no such index exists then no more permutation exists.
      int k = n - 2;
      while (a[k] >= a[k + 1]) {
        k--;
      }
      // 2. Find the largest index r greater than k such that a[k] < a[r]
      int r = n - 1;
      while (a[k] >= a[r]) {
        r--;
      }
      // 3. Swap a[k] and a[r]
      swap(k, r);
      // 4. Reverse a[k+1..n-1]
      reverse(k + 1, n - 1);
      currPerm++;
      return copy;
    }
    
    private void swap(int i, int j) {
      int tmp = a[i];
      a[i] = a[j];
      a[j] = tmp;
    }
    
    private void reverse(int i, int j) {
      while (i < j) {
        swap(i++, j--);
      }
    }
  }
  
  
  private final double[][] d;
  private final int n;
  
  
  public TspNaive(final double[][] d) {
    this.d = d;
    n = d.length;
  }
  
  
  public double minDistTour(int[] tour) {
    PermIterator it = new PermIterator(n - 1);
    double minDist = Double.MAX_VALUE;
    int[] minDistPerm = null;
    while (it.hasNext()) {
      int[] perm = it.next();
      double dist = dist(perm);
      if (dist < minDist) {
        minDist = dist;
        minDistPerm = perm;
      }
    }
    System.arraycopy(minDistPerm, 0, tour, 1, n - 1);
    return minDist;
  }
  
  private double dist(int[] perm) {
    double tourDist = d[0][perm[0]] + d[perm[n - 2]][0];
    for (int i = 0; i < n - 2; i++) {
      tourDist += d[perm[i]][perm[i + 1]];
    }
    return tourDist;
  }
  
  
  private static class City {
    private final double x;
    private final double y;
    
    private City(double xCoordinate, double yCoordinate) {
      x = xCoordinate;
      y = yCoordinate;
    }
  }  
  
  private static double distance(City p, City q) {
    double dx = p.x - q.x, dy = p.y - q.y;
    return Math.sqrt(dx * dx + dy * dy);
  }
  
  private static double[][] distMatrix(City[] cities) {
    final int n = cities.length;
    double[][] d = new double[n][n];
    for (int i = 0; i < n - 1; i++) {
      for (int j = i + 1; j < n; j++) {
        double distance = distance(cities[i], cities[j]);
        d[i][j] = distance;
        d[j][i] = distance;
      }
    }
    return d;
  }
  
  public static void main(String[] args) throws IOException {
    if (args.length == 0) {
      System.out.println("Execution: java cdoan.stanfordalgo.part4.TspNaive <cities-location-file> [<number-of-cities> with MAX = 14]");
      System.exit(1);
    }
    
    Scanner s = new Scanner(new File(args[0]));
    final int NUM_CITIES = s.nextInt(), MAX_NUM_CITIES = 14;
    int n = NUM_CITIES;
    // Read input size
    if (args.length > 1) {
      n = Math.min(n, Integer.parseInt(args[1]));
    }
    //-
    if (n > MAX_NUM_CITIES) {
      n = MAX_NUM_CITIES;
    }
    if (n < NUM_CITIES) {
      System.out.printf("Use the First %d of %d cities in the file (%d is maximum size the algo can handle)%n", 
                         n, NUM_CITIES, MAX_NUM_CITIES);
    }
    City[] cities = new City[n];
    for (int i = 0; i < n; i++) {
      cities[i] = new City(s.nextDouble(), s.nextDouble());
    }
    s.close();
    
    long start = System.currentTimeMillis();
    double[][] d = distMatrix(cities);
    int[] tour = new int[n + 1];
    double minDist = (new TspNaive(d)).minDistTour(tour);
    System.out.printf("# of cities : %d%n", n);
    System.out.printf("TSP distance: %f%n", minDist);
    System.out.printf("TSP tour: %s%n", Arrays.toString(tour));
    System.out.printf("Running time: %d seconds%n", (System.currentTimeMillis() - start) / 1000);
  }
  
}

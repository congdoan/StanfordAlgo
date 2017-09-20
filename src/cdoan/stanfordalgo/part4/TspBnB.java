package cdoan.stanfordalgo.part4;


import java.io.*;
import java.util.*;


/**
 * Programming Assignment 2 of Part 4.
 * Implement a Branch-and-Bound algorithm to exactly solve the Traveling Salesman Problem.
 */
public class TspBnB {
  
  private final double[][] d;
  private final int N;
  
  private final int[] tour;            // current candidate solution being formed so far
  private final int[] finalTour;       // best tour so far
  private double finalDist;            // 2 * best total distance so far
  private final boolean[] visited;     // visited[i] indicates whether city i is already visited/explored
  private final double[] minDist;      // minDist[i] stores smallest distance among all egdes incident to i
  private final double[] secondMinDist;// secondMinDist[i] stores second-smallest distance among all egdes incident to i
  
  
  public TspBnB(double[][] dist) {
    d = dist;
    N = dist.length;
    
    tour = new int[N + 1];
    finalTour = new int[N + 1];
    finalDist = Double.MAX_VALUE;
    visited = new boolean[N];
    visited[0] = true;
    minDist = new double[N];
    secondMinDist = new double[N];
  }
  
  public double minTourDist() {
    // 2 * Lower bound on total cost of any tour (without loss of generality we start from node 0)
    double boundOnRoot = precompute2SmallestDists();
    // 2 * Current distance of the tour so far
    double distance = 0.0;
    // Current level; level 1 means we consider the second node (after the first node 0)
    int level = 1;
    tspRec(boundOnRoot, distance, level);
    return (finalDist / 2);
  }
  
  private double precompute2SmallestDists() {
    double dblBoundOnAnyTour = 0.0;
    if (N > 2) {
      for (int i = 0; i < N; i++) {
        twoSmallestDistances(i);
        dblBoundOnAnyTour += (minDist[i] + secondMinDist[i]);
      }
    } else {
      for (int i = 0; i < N; i++) {
        twoSmallestDistances(i);
        dblBoundOnAnyTour += (2 * minDist[i]);
      }
    }
    return dblBoundOnAnyTour;
  }
  
  /**
   * Among distances between i and all other cities compute two smallest ones.
   */
  private void twoSmallestDistances(int i) {
    double min = Double.MAX_VALUE;
    double secondMin = Double.MAX_VALUE;
    for (int j = 0; j < i; j++) {
      if (d[i][j] <= min) {
        secondMin = min;
        min = d[i][j];
      } else if (d[i][j] <= secondMin) {
        secondMin = d[i][j];
      }
    }
    for (int j = i + 1; j < N; j++) {
      if (d[i][j] <= min) {
        secondMin = min;
        min = d[i][j];
      } else if (d[i][j] <= secondMin) {
        secondMin = d[i][j];
      }
    }
    minDist[i] = min;
    secondMinDist[i] = secondMin;
  }
  
  /**
   * bound: 2 * current lower bound of the root node (updated based on the path so far)
   * distance: 2 * distance of the path so far
   * level: current level while exploring the search space tree
   */
  private void tspRec(double bound, double distance, int level) {
    // Base case is when we have reached level N (i.e. visited each of the cities exactly once)
    if (level == N) {
      // Update final result and final tour if current result is better
      double currTourDist = distance + 2 * d[tour[N - 1]][tour[0]];
      if (currTourDist < finalDist) {
        System.arraycopy(tour, 0, finalTour, 0, N + 1);
        finalDist = currTourDist;
      }
      return;
    }
    
    // For any other level iterate through all the unvisited cities to build the search space tree
    for (int i = 1; i < N; i++) {
      if (visited[i]) {
        continue;
      }
      
      // Different computation of current bound for level 2 from other levels
      double deltaBound = 0.0;
      if (level == 1) {
        deltaBound = (minDist[tour[0]] + minDist[i]);
      } else {
        deltaBound = (secondMinDist[tour[level - 1]] + secondMinDist[i]);
      }
      double deltaDistance = 2 * d[tour[level - 1]][i];
      
      // bound - deltaBound + distance + deltaDistance is the actual lower bound for the node (i)
      // If current lower bound < the best result so far, we need to explore the node further
      if (bound - deltaBound + distance + deltaDistance < finalDist) {
        // Mark i as visited for current level prior to proceeding to explore next level
        tour[level] = i;
        visited[i] = true;
        
        // Call tspRec for the next level
        tspRec(bound - deltaBound, distance + deltaDistance, level + 1);
        
        // Reset visited[i] to false upon backtracking fom the call for next level
        visited[i] = false;
      }
    }
  }
  
  public int[] getMinTour() {
    return finalTour;
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
      System.out.println("Execution: java cdoan.stanfordalgo.part4.TspBnB <cities-location-file>");
      System.exit(1);
    }
    
    Scanner s = new Scanner(new File(args[0]));
    int n = s.nextInt();
    //+ debug: read input size
    if (args.length > 1) {
      n = Integer.valueOf(args[1]);
    }
    //-
    City[] cities = new City[n];
    for (int i = 0; i < n; i++) {
      cities[i] = new City(s.nextDouble(), s.nextDouble());
    }
    s.close();
    
    long start = System.currentTimeMillis();
    TspBnB tsp = new TspBnB(distMatrix(cities));
    System.out.printf("TSP distance: %f%n", tsp.minTourDist());
    System.out.printf("TSP tour: %s%n", Arrays.toString(tsp.getMinTour()));
    System.out.printf("Running time: %d seconds%n", (System.currentTimeMillis() - start) / 1000);
    /* RESULT of Programming Assignment 2 of Part 4:
    TSP distance: 26442.730309
    TSP tour: [0, 1, 5, 9, 10, 11, 14, 18, 17, 21, 22, 20, 16, 19, 24, 23, 15, 13, 12, 8, 6, 2, 3, 7, 4, 0]
    Running time: 6171 seconds
    */
  }

}

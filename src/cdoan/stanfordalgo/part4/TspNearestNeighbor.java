package cdoan.stanfordalgo.part4;


import java.io.*;
import java.util.*;

/**
 * Programming Assignment 3 of Part 4.
 * Implement nearest-neighbor heuristic for Traveling Salesman Problem.
 */
public class TspNearestNeighbor {
  
  private static class City {
    private final double x;
    private final double y;
    
    private City(double xCoordinate, double yCoordinate) {
      x = xCoordinate;
      y = yCoordinate;
    }
  }

  
  public static double minTourDist(int n, City[] cities) {
    double tourDist = 0.0;
    LinkedHashSet<Integer> unvisited = new LinkedHashSet<>(n - 1);
    for (int i = 1; i < n; i++) {
      unvisited.add(i);
    }
    // Start the tour from the first city with index 0
    int last = 0;
    while (!unvisited.isEmpty()) {
      double minSquareDist = Double.MAX_VALUE;
      int nearest = -1;
      for (int neighbor: unvisited) {
        double squareDist = squareDist(cities[last], cities[neighbor]);
        if (squareDist < minSquareDist) {
          minSquareDist = squareDist;
          nearest = neighbor;
        }
      }
      unvisited.remove(nearest);
      tourDist += Math.sqrt(minSquareDist);
      last = nearest;
    }
    // Go back to the first city to complete the tour
    tourDist += Math.sqrt(squareDist(cities[last], cities[0]));
    return tourDist;
  }
  
  private static double squareDist(City p, City q) {
    double dx = p.x - q.x, dy = p.y - q.y;
    return dx * dx + dy * dy;
  }
  
  public static void main(String[] args) throws IOException {
    if (args.length == 0) {
      System.out.println("Execution: java java cdoan.stanfordalgo.part4.TspNearestNeighbor <cities-location-file>");
      System.exit(1);
    }
    
    Scanner s = new Scanner(new File(args[0]));
    final int n = s.nextInt();
    City[] cities = new City[n];
    for (int i = 0; i < n; i++) {
      s.nextInt();
      cities[i] = new City(s.nextDouble(), s.nextDouble());
    }
    s.close();
    
    long start = System.currentTimeMillis();
    double tourDist = minTourDist(n, cities);
    System.out.printf("TSP distance: %f%n", tourDist);
    System.out.printf("Running time: %d seconds%n", (System.currentTimeMillis() - start) / 1000);
  }
  
}

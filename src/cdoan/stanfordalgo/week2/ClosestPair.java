package cdoan.stanfordalgo.week2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;


public class ClosestPair {
  
  public static class Point {
    private int x;
    private int y;
    
    public Point(int x, int y) {
      this.x = x;
      this.y = y;
    }
    
    public long distanceTo(Point q) {
      if (q == null) {
        throw new NullPointerException("q is null");
      }
      long dx = q.x - x;
      long dy = q.y - y;
      return dx * dx + dy * dy;
    }
  }
  
  public static class Pair {
    private Point p;
    private Point q;
    
    public Pair(Point p, Point q) {
      if (p == null || q == null) {
        throw new NullPointerException("p or q is null");
      }
      this.p = p;
      this.q = q;
    }
    
    public long distance() {
      return p.distanceTo(q);
    }
  }
  

  /**
   *  Test method findNearestPair using randomly generated points,
   *  assumming method findNearestPairNaive is correct.
   */
  public static void main(String[] args) {
    int n = 100;
    if (args.length > 0) {
      try {
        n = Integer.parseInt(args[0]);
      } catch (NumberFormatException nfe) {}
    }
    Point[] points = randPoints(n);
    if (findNearestPair(points).distance() 
			  != findNearestPairNaive(points, 0, n - 1).distance()) {
      throw new AssertionError("findNearestPair's implementation is Incorrect!");
    }
  }
  
  /* Utility method for generating test data */
  private static Point[] randPoints(final int numPoints) {
    Point[] points = new Point[numPoints];
    Random rd = new Random();
    // All the points have distinct x coordinates
    HashSet<Integer> xSet = new HashSet<>();
    // All the points have distinct x coordinates
    HashSet<Integer> ySet = new HashSet<>();
    for (int i = 0; i < numPoints; i++) {
      int x = rd.nextInt();
      int y = rd.nextInt();
      while (xSet.contains(x) || ySet.contains(y)) {
        x = rd.nextInt();
        y = rd.nextInt();
      }
      xSet.add(x);
      ySet.add(y);
      points[i] = new Point(x, y);
    }
    return points;
  }
  
  /**
   * Find a closest pair in the point set points.
   */
  public static Pair findNearestPair(Point[] points) {
    final int n = points.length;
    // Make a copy pointsY of of points
    Point[] pointsY = new Point[n];
    System.arraycopy(points, 0, pointsY, 0, n);
    // Sort pointsY by y coordinates
    Arrays.sort(pointsY, (Point p, Point q) -> {long diff = (long) p.y - q.y;
                                          return diff < 0 ? -1 : (diff == 0 ? 0 : 1);});
    // Sort points by x coordinates
    Arrays.sort(points, (Point p, Point q) -> {long diff = (long) p.x - q.x;
                                          return diff < 0 ? -1 : (diff == 0 ? 0 : 1);});
    
    // Make outermost recursive call
    return findNearestPair(points, 0, n - 1, pointsY);
  }
  
  /* Find a closest pair in pointsX[lo..hi] */
  private static Pair findNearestPair(Point[] pointsX, int lo, int hi, Point[] pointsY) {
    // Base case
    if (hi - lo < 6) {
      return findNearestPairNaive(pointsX, lo, hi);
    }
    
    // Recursively find closest pair in Left half and Right half
    final int mid = (lo + hi) / 2;
    Point[] pointsYLeft = new Point[mid - lo + 1];
    Point[] pointsYRight = new Point[hi - mid];
    int i = 0;
    int j = 0;
    for (Point p : pointsY) {
      if (p.x <= pointsX[mid].x) {
        pointsYLeft[i++] = p;
      } else {
        pointsYRight[j++] = p;
      }
    }
    Pair leftClosest = findNearestPair(pointsX, lo, mid, pointsYLeft);
    Pair rightClosest = findNearestPair(pointsX, mid + 1, hi, pointsYRight);
    
    // Find the closest pair among leftClosest and rightClosest
    long minLeftDistance = leftClosest.distance();
    long minRightDistance = rightClosest.distance();
    Pair nonSplitClosest = leftClosest;
    long minNonSplitDistance = minLeftDistance;
    if (minRightDistance < minLeftDistance) {
      nonSplitClosest = rightClosest;
      minNonSplitDistance = minRightDistance;
    }
    
    // Find a split closest pair (one point in the left and the other in the right) if any, 
    // i.e. whose distance is strictly less than minNonSplitDistance
    List<Point> stripY = pointsInStrip(pointsX[mid].x, minNonSplitDistance, pointsY);
    Pair splitClosest = closestSplitPair(stripY, minNonSplitDistance);
    return splitClosest != null ? splitClosest : nonSplitClosest;
  }
  
  /* 
   * Extract those points from pointsY which distance to the 
   * vertical line 'x = centerX' is at most minNonSplitDistance
   */
  private static List<Point> pointsInStrip(int centerX, long minNonSplitDistance, Point[] pointsY) {
    List<Point> stripY = new ArrayList<>();
    for (Point p : pointsY) {
      if (centerX - minNonSplitDistance <= p.x 
          && p.x <= centerX + minNonSplitDistance) {
        stripY.add(p);
      }
    }
    return stripY;
  }
  
  /* Find a closest pair in stripY, if exists, whose distance is less than minNonSplitDistance */
  private static Pair closestSplitPair(List<Point> stripY, long minNonSplitDistance) {
    Point bestP = null;
    Point bestQ = null;
    long bestDist = minNonSplitDistance;
    int size = stripY.size();
    for (int i = 0; i < size - 1; i++) {
      Point p = stripY.get(i);
      for (int j = 1; j < Math.min(8, size - i); j++) {
        Point q = stripY.get(i + j);
        if (p.distanceTo(q) < bestDist) {
          bestP = p;
          bestQ = q;
          bestDist = p.distanceTo(q);
        }
      }
    }
    return bestP != null ? new Pair(bestP, bestQ) : null;
  }
  
  /* Find a closest pair in points using Naive approach */
  private static Pair findNearestPairNaive(Point[] points, int lo, int hi) {
    Point bestP = null;
    Point bestQ = null;
    long bestDist = Long.MAX_VALUE;
    for (int i = lo; i < hi; i++) {
      for (int j = i + 1; j <= hi; j++) {
        if (points[i].distanceTo(points[j]) < bestDist) {
          bestP = points[i];
          bestQ = points[j];
          bestDist = points[i].distanceTo(points[j]);
        }
      }
    }
    return new Pair(bestP, bestQ);
  }

}

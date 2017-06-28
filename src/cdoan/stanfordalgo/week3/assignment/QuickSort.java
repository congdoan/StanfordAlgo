
package cdoan.stanfordalgo.week3.assignment;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;


public class QuickSort {

  /* Read input data from file, sort it, then assert */
  public static void main(String[] args) throws IOException {
    List<Integer> list;
    if (args.length != 0) {
      list = readIntsFromTextFile(new File(args[0]));
    } else {
      list = Arrays.asList(6, 8, 5, 1, 7, 9, 3, 4, 2);
    }
    
    Integer[] a = list.toArray(new Integer[list.size()]);
    PivotingRule rule = new FirstItemAsPivot();
    long totalNumComparisions = sort(a, rule);
    assert isSorted(a);
    System.out.println("totalNumComparisions FirstItemAsPivot = " + totalNumComparisions);
    
    a = list.toArray(new Integer[list.size()]);
    rule = new LastItemAsPivot();
    totalNumComparisions = sort(a, rule);
    assert isSorted(a);
    System.out.println("totalNumComparisions LastItemAsPivot = " + totalNumComparisions);
    
    a = list.toArray(new Integer[list.size()]);
    rule = new MedianOfThreeAsPivot();
    totalNumComparisions = sort(a, rule);
    assert isSorted(a);
    System.out.println("totalNumComparisions MedianOfThreeAsPivot = " + totalNumComparisions);
  }
  
  /* Utility method for preparing test data */
  private static List<Integer> readIntsFromTextFile(File file) throws IOException {
    List<Integer> list = new ArrayList<>();
    Scanner scanner = new Scanner(file);
    while (scanner.hasNextInt()) {
      list.add(scanner.nextInt());
    }
    scanner.close();
    return list;
  }
  
  public static <T extends Comparable<T>> long sort(T[] a, PivotingRule rule) {
    if (a == null) {
      throw new NullPointerException("The input array is null!");
    }

    return sort(a, 0, a.length - 1, rule);
  }
  
  /* Return total number of comparisions */
  private static <T extends Comparable<T>> long sort(T[] a, int lo, int hi, PivotingRule rule) {
    // Base case: return when subarray has length at most 1
    if (hi <= lo) {
      return 0;
    }
    
    // 1. Choose pivot & swap it with first element
    int pivot = rule.getPivot(a, lo, hi);
    swap(a, pivot, lo);
    
    // 2. Partition around pivot
    pivot = partition(a, lo, hi);
    
    // 3. Recursively sort Left and Right subarrays of pivot. Then return number of comparisions.
    int totalNumCmps = hi - lo;
    totalNumCmps += sort(a, lo, pivot - 1, rule) + sort(a, pivot + 1, hi, rule);
    return totalNumCmps;
  }
  
  /*
   * The pivot p is a[lo]. As scan through a[lo+1..hi] maintains the invariant below:
   * Within the seen/scanned elements all of those less than p proceed all of those greater than p.
   */
  private static <T extends Comparable<T>> int partition(T[] a, int lo, int hi) {
    T p = a[lo];
    int finalIndexOfPivot = lo;
    for (int i = lo + 1; i <= hi; ++i) {
      if (less(a[i], p)) {
        swap(a, ++finalIndexOfPivot, i);
      }
    }
    swap(a, lo, finalIndexOfPivot);
    return finalIndexOfPivot;
  }
  
  
  public interface PivotingRule {
    <T extends Comparable<T>> int getPivot(T[] a, int lo, int hi);
  }
  
  public static class FirstItemAsPivot implements PivotingRule {
    public <T extends Comparable<T>> int getPivot(T[] a, int lo, int hi) {
      return lo;
    }
  }
  
  public static class LastItemAsPivot implements PivotingRule {
    public <T extends Comparable<T>> int getPivot(T[] a, int lo, int hi) {
      return hi;
    }
  }
  
  public static class MedianOfThreeAsPivot implements PivotingRule {
    /* Return the index of median element among a[lo],a[(lo+hi)/2], and a[hi] */
    public <T extends Comparable<T>> int getPivot(T[] a, int lo, int hi) {
      int mid = (lo + hi) / 2;
      if ((less(a[lo], a[mid]) && less(a[mid], a[hi])) 
          || (less(a[hi], a[mid]) && less(a[mid], a[lo]))) {
        return mid;
      } else if ((less(a[mid], a[lo]) && less(a[lo], a[hi])) 
                  || (less(a[hi], a[lo]) && less(a[lo], a[mid]))) {
        return lo;
      } else {
        return hi;
      }
    }
  }
  
  
  /* Helper method for testing if v is less than w */
  private static <T extends Comparable<T>> boolean less(T v, T w) {
    return v.compareTo(w) < 0;
  }
  
  /* Helper method for exchanging ith and jth elements */
  private static <T> void swap(T[] a, int i, int j) {
    T tmp = a[i];
    a[i] = a[j];
    a[j] = tmp;
  }
  
  /* Helper method for testing if given array is sorted */
  private static <T extends Comparable<T>> boolean isSorted(T[] a) {
    final int n = a.length;
    for (int i = 1; i < a.length; ++i) {
      if (less(a[i], a[i - 1])) {
        return false;
      }
    }
    return true;
  }
  
}

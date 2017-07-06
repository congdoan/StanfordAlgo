package cdoan.stanfordalgo.week4;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;


/**
 * Implements Deterministic Select algo, which computes k-th smallest element in the given list in O(n) time.
 */
public class DSelectList {
  
  /**
   * Run tests.
   */
  public static void main(String[] args) {
    List<Integer> input = randomIntegerList();
    final int n = input.size();
    Integer[] sorted = input.toArray(new Integer[n]);
    Arrays.sort(sorted);
    for (int k = 1; k <= n; k++) {
      Collections.shuffle(input);
      Integer actual = kthSmallest(input, k);
      if (!actual.equals(sorted[k - 1])) {
        throw new AssertionError(String.format("computed %d-th smallest = %d; expected = %d", 
                                               k, actual, sorted[k - 1]));
      }
    }
  }
  
  /* Utility method for test data */
  private static List<Integer> randomIntegerList() {
    Random rd = new Random();
    final int n = 2000 + rd.nextInt(1001);
    final int sz = (int) (n * 1.2);
    ArrayList<Integer> res = new ArrayList<>(sz);
    for (int i = 1; i <= sz; i++) {
      res.add(1 + rd.nextInt(n));
    }
    return res;
  }

  /**
   * Compute and return k-th smallest element (order statistic).
   */
  public static <T extends Comparable<T>> T kthSmallest(List<T> a, int k) {
    if (a == null || a.size() == 0) {
      throw new IllegalArgumentException("null or empty list a");
    }
    final int n = a.size();
    if (k < 1 || k > n) {
      throw new IllegalArgumentException(String.format("k %d out of range 1..%d", k, n));
    }
    return kthSmallest(a, 0, n, k, false);
  }
  
  /*
   * Recursive method to compute the k-th smallest element in subarray a[lo..lo+len-1].
   * recurringToComputePivot indecates whether the method is being recursively called to compute "median of medians".
   * 
   * 1 ) Compute a garanteed good pivot based on "Median of Medians" idea.
   * 2 ) Partition input array around pivot.
   * 3a) If pivot is k-th smallest then return it.
   * 3b) Otherwise, recur either on Left or Right half of pivot depending whether it > or < k-th smallest.
   */
  private static <T extends Comparable<T>> T kthSmallest(List<T> a, int lo, int len, 
                                                         int k, 
                                                         boolean recurringToComputePivot) {
    // Base case
    if (len == 1) {
      // Return either "median of medians" pivot or k-th smallest value (unlucky case)
      return a.get(lo);
    }
    
    
    /* 
     * 1) Compute "median of medians" pivot
     *  a) Logically divide given array range into groups of 5 elements then sort them
     *  b) Copy groups' medians into medians
     *  c) Recur to compute median of medians
     */
    int numGroups = len % 5 != 0 ? (len / 5) + 1 : len / 5;
    // medians is used to store medians of groups of 5 elements from sublist a[lo..lo+len-1]
    List<T> medians = new ArrayList<>(numGroups);
    int group = 1;
    int start = lo;
    for (; group * 5 <= len; group++, start += 5) {
      insertionSort(a, start, 5);
      medians.add(a.get(start + 2));
    }
    int numRemainingElements = len - (group - 1) * 5;
    if (numRemainingElements > 0) {
      insertionSort(a, start, numRemainingElements);
      medians.add(a.get(start + (numRemainingElements / 2)));
    }
    // Recur to compute "median of medians" pivot
    T pivot = kthSmallest(medians, 0, numGroups, (numGroups + 1) / 2, true);
    if (recurringToComputePivot) {
      return pivot;
    }

    /*
     * 2) Partition the given subarray around pivot
     */
    int finalIndex = partition(a, lo, len, pivot);
    
    /*
     * 3) If pivot is k-th smallest then return it.
     *    Otherwise, recur either on Left or Right half of it depending whether it > or < k-th smallest.
     */
    int orderStatistic = finalIndex - lo + 1;
    if (orderStatistic == k) {
      // Return k-th smallest value (lucky case)  
      return pivot;
    }
    if (orderStatistic > k) {
      return kthSmallest(a, lo, orderStatistic - 1, k, false);
    }
    return kthSmallest(a, finalIndex + 1, len - orderStatistic, k - orderStatistic, false);
  }
  
  /*
   * Helper method to partition sublist a[lo..lo+len-1] around pivot
   * Invariant: left <= pivot <= right
   * Return the final index of pivot
   */
  private static <T extends Comparable<T>> int partition(List<T> a, int lo, int len, T pivot) {
    /* Find index of pivot and swap it with entry a[lo] */
    for (int i = lo; i < lo + len; i++) {
      if (pivot.equals(a.get(i))) {
        swap(a, lo, i);
        break;
      }
    }    
    
    int i = lo + 1;
    int j = lo + len - 1;
    while (i <= j) {
      while (i <= j && less(a.get(i), pivot)) {
        i++;
      }
      //while (i <= j && less(pivot, a.get(j))) {
      while (less(pivot, a.get(j))) { //'i <= j' is unnecessary because a[lo] is itself pivot
        j--;
      }
      if (i <= j) { //Notice: finally i must be greater than j for the loop to terminate
        swap(a, i, j);
        i++;
        j--;
      }
    }
    swap(a, lo, j);
    return j;
  }
  
  /* Helper method to check if v < w */
  private static <T extends Comparable<T>> boolean less(T v, T w) {
    return v.compareTo(w) < 0;
  }
  
  /* Helper method to swap i-th and j-th elements */
  private static <T extends Comparable<T>> void swap(List<T> a, int i, int j) {
    T ai = a.get(i);
    a.set(i, a.get(j));
    a.set(j, ai);
  }
  
  /*
   * Helper method to sort sublist a[lo..lo+len-1] using InsertionSort algo
   * Invariant: left (i.e. seen elements) already sorted
   */
  private static <T extends Comparable<T>> void insertionSort(List<T> a, int lo, int len) {
    for (int i = lo + 1; i < lo + len; i++) {
      int pos = i;
      while (pos > lo && less(a.get(pos), a.get(pos - 1))) {
        swap(a, pos, pos - 1);
        pos--;
      }
    }
  }
  
}

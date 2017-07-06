package cdoan.stanfordalgo.week4;

import java.util.Arrays;
import java.util.Random;


/**
 * Implement Randomized Select, also called Quick Select, algo, which computes k-th smallest element in given array.
 */
public class RSelect {

  /**
   * Run tests.
   */
  public static void main(String[] args) {
    Integer[] input = randomIntegerArray();
    final int n = input.length;
    Integer[] sorted = new Integer[n];
    System.arraycopy(input, 0, sorted, 0, n);
    Arrays.sort(sorted);
    for (int k = 1; k <= n; k++) {
      Integer kthSmallest = kthSmallest(input, k);
      if (!kthSmallest.equals(sorted[k - 1])) {
        throw new AssertionError(String.format("computed %d-th smallest = %s; expected = %s", 
                                               k, kthSmallest, sorted[k - 1]));
      }
    }
  }
  
  /* Utility method for test data */
  private static Integer[] randomIntegerArray() {
    Random rd = new Random();
    final int n = 2000 + rd.nextInt(1001);
    final int sz = (int) (n * 1.2);
    Integer[] res = new Integer[sz];
    for (int i = 0; i < sz; i++) {
      res[i] = 1 + rd.nextInt(n);
    }
    return res;
  }

  /**
   * Compute k-th smallest element in given array.
   */
  public static <T extends Comparable<T>> T kthSmallest(T[] a, int k) {
    if (a == null || a.length == 0) {
      throw new IllegalArgumentException("null or empty array a");
    }
    final int n = a.length;
    if (k < 1 || k > n) {
      throw new IllegalArgumentException(String.format("k %d out of range 1..%d", k, n));
    }

    //PartitionStrategy strategy = new ThreewayPartition();
    PartitionStrategy strategy = new TwowayPartition();
    return kthSmallest(a, 0, n, k, strategy);
  }

  /*
   * Recursive method to compute the k-th smallest element in subarray a[lo..lo+len-1].
   * 
   * 1 ) Pick as pivot an element at random in given subarray.
   * 2 ) Partition input array around pivot.
   * 3a) If pivot is k-th smallest then return it.
   * 3b) Otherwise, recur either on Left or Right half of pivot depending whether it > or < k-th smallest.
   */
  private static <T extends Comparable<T>> T kthSmallest(T[] a, int lo, int len, int k, PartitionStrategy strategy) {
    // Base case
    if (len == 1) {
      // Return k-th smallest value (unlucky case)
      return a[lo];
    }
    
    int pivot = lo + (new Random()).nextInt(len);
    swap(a, lo, pivot);
    Pair pair = strategy.partition(a, lo, lo + len - 1);
    int orderStatisticLo = pair.start - lo + 1;
    int orderStatisticHi = pair.end - lo + 1;
    if (orderStatisticLo <= k && k <= orderStatisticHi) {
      // Return k-th smallest value (lucky case)
      return a[pair.start];
    }
    if (orderStatisticLo > k) {
      return kthSmallest(a, lo, orderStatisticLo - 1, k, strategy);
    }
    return kthSmallest(a, pair.end + 1, len - orderStatisticHi, k - orderStatisticHi, strategy);
  }
  
  /* Helper interface PartitionStrategy */
  private static interface PartitionStrategy {
    public <T extends Comparable<T>> Pair partition(T[] a, int lo, int hi);
  }
  
  /* Helper class TwowayPartition that implements 2-way partition */
  private static class TwowayPartition implements PartitionStrategy {
    public <T extends Comparable<T>> Pair partition(T[] a, int lo, int hi) {
      /* Maintain invariant a[lo..index-1] < pivot = a[index] <= a[index+1..hi] */
      int index = lo;
      for (int i = lo + 1; i <= hi; i++) {
        if (less(a[i], a[lo])) { // use a[lo] as pivot
          swap(a, ++index, i);
        }
      }
      swap(a, lo, index);
      return new Pair(index, index);
    }
  }
  
  /* Helper class ThreewayPartition that implements 3-way partition */
  private static class ThreewayPartition implements PartitionStrategy {
    public <T extends Comparable<T>> Pair partition(T[] a, int lo, int hi) {
      /* Maintain invariant a[lo..lt-1] < pivot = a[lt..gt] < a[gt+1..hi] */
      int lt = lo;
      int gt = hi;
      T pivot = a[lo];
      for (int i = lo; i <= gt;) {
        if (less(a[i], pivot)) {
          swap(a, lt++, i++);
        } else if (less(pivot, a[i])) {
          swap(a, i, gt--);
        } else {
          i++;
        }
      }
      return new Pair(lt, gt);
    }
  }
  
  /* Helper class to store a pair of indices */
  private static class Pair {
    private int start;
    private int end;
    
    private Pair(int start, int end) {
      this.start = start;
      this.end = end;
    }
  }

  /* Helper method to check if v < w */
  private static <T extends Comparable<T>> boolean less(T v, T w) {
    return v.compareTo(w) < 0;
  }
  
  /* Helper method to swap i-th and j-th elements */
  private static <T extends Comparable<T>> void swap(T[] a, int i, int j) {
    T ai = a[i];
    a[i] = a[j];
    a[j] = ai;
  }
  
}

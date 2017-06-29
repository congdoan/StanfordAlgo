package cdoan.stanfordalgo.week2;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;


public class CountInversions {
  
  /**
   * Test sort method using a random integer array as input.
   */
  public static void main(String[] args) {
    for (int test = 0; test < 1300; ++test) {
      Integer[] a = randomIntegerArr();
      if ((countInversionsNaive(a) != countInversions(a, new Integer[a.length])) || !isSorted(a)) {
        throw new AssertionError("countInversions is Not correct!");
      }
    }
  }

  /* Utility method for preparing test data */
  private static Integer[] randomIntegerArr() {
    Random random = new Random();
    int len = 1000 + random.nextInt(2001);
    Integer[] result = new Integer[len];
    for (int i = 0; i < len; i++) {
      result[i] = random.nextInt(1200) - 600;
    }
    return result;
  }
  
  /* Brute-force O(n^2) running time */
  private static <T extends Comparable<T>> long countInversionsNaive(T[] a) {
    long count = 0;
    final int n = a.length;
    for (int i = 0; i < n - 1; i++) {
      for (int j = i + 1; j < n; j++) {
        if (less(a[j], a[i])) {
          count++;
        }
      }
    }
    return count;
  }
  
  /**
   * Divide-and-Conquer O(nlogn) running time.
   */
  public static <T extends Comparable<T>> long countInversions(T[] a, T[] aux) {
    System.arraycopy(a, 0, aux, 0, a.length);
    return countInversions(a, 0, a.length - 1, aux);
  }
  
  /* Count number of inversions in a[lo..hi] (inclusive) using aux as buffer */
  private static <T extends Comparable<T>> long countInversions(T[] a, int lo, int hi, T[] aux) {
    // Base case
    if (lo >= hi) {
      return 0;
    }
      
    // Recursively Count inversions in Left half and Right half
    int mid = (lo + hi) / 2;
    long leftCount = countInversions(aux, lo, mid, a);
    long rightCount = countInversions(aux, mid + 1, hi, a);
    
    // Merge and Count split inversions
    long splitCount = cntSplitInvs(aux, lo, mid, hi, a);
    
    return leftCount + rightCount + splitCount;
  }
  
  /*
   * Simultaneously merge 2 sorted halves (aux[lo..mid] & aux[mid+1..hi])
   *  into a[lo..hi] and count split inversions.
   */
  private static <T extends Comparable<T>> long cntSplitInvs(T[] aux, int lo, int mid, int hi, T[] a) {
    long splitInversions = 0;
    int i = lo;
    int j = mid + 1;
    int k = lo;
    while (i <= mid && j <= hi) {
      if (less(aux[j], aux[i])) {
        a[k++] = aux[j++];
        // Pair (i, j) is an inversion => All pairs (i..mid, j) are inversions
        splitInversions += (mid - i + 1);
      } else {
        a[k++] = aux[i++];
      }
    }
    while (i <= mid) {
      a[k++] = aux[i++];
    }
    while (j <= hi) {
      a[k++] = aux[j++];
    }
    return splitInversions;
  }  
  
  /* Helper method for testing if v is less than w */
  private static <T extends Comparable<T>> boolean less(T v, T w) {
    return v.compareTo(w) < 0;
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

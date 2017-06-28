package cdoan.stanfordalgo.week1;

import java.util.Random;

public class MergeSort {

  /**
   * Test sort method using a random integer array as input.
   */
  public static void main(String[] args) {
    for (int test = 0; test < 1300; ++test) {
      Integer[] a = randomIntegerArr();
      sort(a, new Integer[a.length]);
      //assert isSorted(a);
      if (!isSorted(a)) {
        throw new AssertionError("The array is Not sorted!");
      }
    }
  }

  /* Utility method for preparing test data */
  private static Integer[] randomIntegerArr() {
    Random random = new Random();
    int len = 6 + random.nextInt(11) + random.nextInt(21);
    Integer[] result = new Integer[len];
    for (int i = 0; i < len; i++) {
      result[i] = random.nextInt(200) - 100;
    }
    return result;
  }

  /**
   * Sort array a using array aux as buffer.
   */
  public static <T extends Comparable<T>> void sort(T[] a, T[] aux) {
    if (a == null || aux == null) {
      throw new NullPointerException("The input array or auxiliary array is null!");
    }
    if (aux == a) {
      throw new IllegalArgumentException("Auxiliary array mustn't be input array!");
    }
    if (aux.length < a.length) {
      throw new IllegalArgumentException("Auxiliary array mustn't be shorter than input array!");
    }

    final int n = a.length;
    System.arraycopy(a, 0, aux, 0, n);
    sort(a, 0, n - 1, aux);
  }

  /* Recursively sort a[lo..hi] using aux as buffer */
  private static <T extends Comparable<T>> void sort(T[] a, int lo, int hi, T[] aux) {
    // base case
    if (lo >= hi) {
      return;
    }

    int mid = (lo + hi) / 2;
    sort(aux, lo, mid, a);
    sort(aux,mid + 1, hi, a);
    merge(aux, lo, mid, hi, a);
  }

  /* Merge sorted aux[lo..mid] and aux[mid+1..hi] into a[lo..hi] */
  private static <T extends Comparable<T>> void merge(T[] aux, int lo, int mid, int hi, T[] a) {
    int i = lo;
    int j = mid + 1;
    int k = lo;
    while (i <= mid && j <= hi) {
      if (less(aux[i], aux[j])) {
        a[k++] = aux[i++];
      } else {
        a[k++] = aux[j++];
      }
    }
    while (i <= mid) {
      a[k++] = aux[i++];
    }
    while (j <= hi) {
      a[k++] = aux[j++];
    }
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

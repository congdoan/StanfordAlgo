package cdoan.stanfordalgo.week1;

import cdoan.util.ArrayUtilInJava;

import java.util.Random;

/**
 * Created by co on 14/06/2017.
 */

public class MergeSortInJava {
    public static void main(String[] args) {
        //int[] a = {8, 6, 1, 7, 5};
        //int[] a = {6, 2, 8, 1, 7, 3, 5};
        int[] a = ArrayUtilInJava.randomArr();
        int[] acopy = new int[a.length];
        System.arraycopy(a, 0, acopy, 0, a.length);
        ArrayUtilInJava.printArr(a);
        sort(a);
        ArrayUtilInJava.printArr(a);
        System.out.println("array is sorted: " + ArrayUtilInJava.isArraySorted(a));

        System.out.println();

        ArrayUtilInJava.printArr(acopy);
        sortBottomUp(acopy);
        ArrayUtilInJava.printArr(acopy);
        System.out.println("array is sorted: " + ArrayUtilInJava.isArraySorted(acopy));
    }

    public static void sortBottomUp(final int[] a) {
        if (a == null || a.length <= 1) {
            return;
        }

        final int n = a.length;
        final int[] buff = new int[n];
        System.arraycopy(a,0, buff,0, n);
        final int numPasses = numDivideBy2ToReach1(n);
        for (int pass = 0, size = 1; pass < numPasses; pass++, size *= 2) {
            int lo = 0;
            int mid = lo + size - 1;
            int hi = mid + size;
            while (hi < n) {
                if (pass % 2 == 0) {
                    if (numPasses % 2 == 0) {
                        // NOTE: start merging from a into buff
                        merge(a, lo, mid, hi, buff); //merge from a into buff
                    } else {
                        // NOTE: start merging from buff into a
                        merge(buff, lo, mid, hi, a); //merge from buff into a
                    }
                } else {
                    if (numPasses % 2 == 0) {
                        // NOTE: start merging from buff into a
                        merge(buff, lo, mid, hi, a); //merge from buff into a
                    } else {
                        // NOTE: start merging from a into buff
                        merge(a, lo, mid, hi, buff); //merge from a into buff
                    }
                }
                lo = hi + 1;
                mid = lo + size - 1;
                hi = mid + size;
            }

            if (lo < n) {
                // handle the last n-lo elements
                if (n - lo > size) {
                    mid = lo + size - 1;
                    hi = n - 1;
                    if (pass % 2 == 0) {
                        if (numPasses % 2 == 0) {
                            merge(a, lo, mid, hi, buff); //merge from a into buff
                        } else {
                            merge(buff, lo, mid, hi, a); //merge from buff into a
                        }
                    } else {
                        if (numPasses % 2 == 0) {
                            merge(buff, lo, mid, hi, a); //merge from buff into a
                        } else {
                            merge(a, lo, mid, hi, buff); //merge from a into buff
                        }
                    }
                }
            }
        }
    }

    private static int numDivideBy2ToReach1(int n) {
        int logOfN = 0;
        int origin = n;
        while (n > 1) {
            n /= 2;
            logOfN++;
        }
        return (1 << logOfN) == origin ? logOfN : logOfN + 1;
    }

    public static void sort(int[] a) {
        if (a == null || a.length <= 1) {
            return;
        }

        int[] buff = new int[a.length];
        System.arraycopy(a,0, buff,0, a.length);
        sort(a,0,a.length - 1, buff);
    }

    /**
     * Sort a[lo..hi] in-place using buffer buff
     */
    private static void sort(int[] a, int lo, int hi, int[] buff) {
        // base case
        if (lo >= hi)
            return;

        int mid = (lo + hi) / 2;
        sort(buff, lo, mid, a);
        sort(buff,mid + 1, hi, a);
        merge(buff, lo, mid, hi, a);
    }

    /**
     * Merge sorted buff[lo..mid] and buff[mid+1..hi] into a[lo..hi]
     */
    private static void merge(int[] buff, int lo, int mid, int hi, int[] a) {
        int i = lo, j = mid+1, k = lo;
        while (i <= mid && j <= hi) {
            if (buff[i] < buff[j]) {
                a[k++] = buff[i++];
            } else {
                a[k++] = buff[j++];
            }
        }
        while (i <= mid) {
            a[k++] = buff[i++];
        }
        while (j <= hi) {
            a[k++] = buff[j++];
        }
    }

}

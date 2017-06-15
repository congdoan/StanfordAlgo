package cdoan.stanfordalgo.week1

import cdoan.util.ArrayUtil
import java.util.Random

/**
 * Created by co on 14/06/2017.
 */

object MergeSort {

    @JvmStatic fun main(args: Array<String>) {
        //val a = intArrayOf(8, 6, 1, 7, 5)
        //val a = intArrayOf(6, 2, 8, 1, 7, 3, 5)
        val a = ArrayUtil.randomArr()
        val acopy = IntArray(a.size)
        System.arraycopy(a, 0, acopy, 0, a.size)
        ArrayUtil.printArr(a)
        sort(a)
        ArrayUtil.printArr(a)
        println("array is sorted: ${ArrayUtil.isArraySorted(a)}")

        println()

        ArrayUtil.printArr(acopy)
        sortBottomUp(acopy)
        ArrayUtil.printArr(acopy)
        println("array is sorted: ${ArrayUtil.isArraySorted(acopy)}")
    }

    fun sortBottomUp(a: IntArray?) {
        if (a == null || a.size <= 1) {
            return
        }

        val n = a.size
        val buff = IntArray(n)
        System.arraycopy(a, 0, buff, 0, n)
        val numPasses = numDivideBy2ToReach1(n)
        var pass = 0
        var size = 1
        while (pass < numPasses) {
            var lo = 0
            var mid = lo + size - 1
            var hi = mid + size
            while (hi < n) {
                if (pass % 2 == 0) {
                    if (numPasses % 2 == 0) {
                        // NOTE: start merging from a into buff
                        merge(a, lo, mid, hi, buff) //merge from a into buff
                    } else {
                        // NOTE: start merging from buff into a
                        merge(buff, lo, mid, hi, a) //merge from buff into a
                    }
                } else {
                    if (numPasses % 2 == 0) {
                        // NOTE: start merging from buff into a
                        merge(buff, lo, mid, hi, a) //merge from buff into a
                    } else {
                        // NOTE: start merging from a into buff
                        merge(a, lo, mid, hi, buff) //merge from a into buff
                    }
                }
                lo = hi + 1
                mid = lo + size - 1
                hi = mid + size
            }

            if (lo < n) {
                // handle the last n-lo elements
                if (n - lo > size) {
                    mid = lo + size - 1
                    hi = n - 1
                    if (pass % 2 == 0) {
                        if (numPasses % 2 == 0) {
                            merge(a, lo, mid, hi, buff) //merge from a into buff
                        } else {
                            merge(buff, lo, mid, hi, a) //merge from buff into a
                        }
                    } else {
                        if (numPasses % 2 == 0) {
                            merge(buff, lo, mid, hi, a) //merge from buff into a
                        } else {
                            merge(a, lo, mid, hi, buff) //merge from a into buff
                        }
                    }
                }
            }
            pass++
            size *= 2
        }
    }

    private fun numDivideBy2ToReach1(n: Int): Int {
        var n = n
        var logOfN = 0
        val origin = n
        while (n > 1) {
            n /= 2
            logOfN++
        }
        return if (1 shl logOfN == origin) logOfN else logOfN + 1
    }

    fun sort(a: IntArray?) {
        if (a == null || a.size <= 1) {
            return
        }

        val buff = IntArray(a.size)
        System.arraycopy(a, 0, buff, 0, a.size)
        sort(a, 0, a.size - 1, buff)
    }

    /**
     * Sort a[lo to hi] in-place using buffer 'buff'
     */
    private fun sort(a: IntArray, lo: Int, hi: Int, buff: IntArray) {
        // base case
        if (lo >= hi)
            return

        val mid = (lo + hi) / 2
        sort(buff, lo, mid, a)
        sort(buff, mid + 1, hi, a)
        merge(buff, lo, mid, hi, a)
    }

    /**
     * Merge sorted buff[lo to mid] and buff[mid+1 to hi] into a[lo to hi]
     */
    private fun merge(buff: IntArray, lo: Int, mid: Int, hi: Int, a: IntArray) {
        var i = lo
        var j = mid + 1
        var k = lo
        while (i <= mid && j <= hi) {
            if (buff[i] < buff[j]) {
                a[k++] = buff[i++]
            } else {
                a[k++] = buff[j++]
            }
        }
        while (i <= mid) {
            a[k++] = buff[i++]
        }
        while (j <= hi) {
            a[k++] = buff[j++]
        }
    }

}

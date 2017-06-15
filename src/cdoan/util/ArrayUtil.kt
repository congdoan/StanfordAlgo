package cdoan.util

import java.util.Random

/**
 * Created by co on 15/06/2017.
 */
object ArrayUtil {

    fun printArr(a: IntArray) {
        println(arr2Str(a))
    }

    private fun arr2Str(a: IntArray?): String {
        if (a == null || a.size == 0) {
            return "[]"
        }

        val sb = StringBuilder("[")
        sb.append(a[0])
        for (i in 1..a.size - 1) {
            sb.append(", ").append(a[i])
        }
        sb.append("]")
        return sb.toString()
    }

    fun isArraySorted(a: IntArray?, nondecreasing: Boolean = true): Boolean {
        if (a == null || a.size <= 1) {
            return true
        }

        if (nondecreasing) {
            for (i in 1..a.size - 1) {
                if (a[i - 1] > a[i])
                    return false
            }
        } else {
            for (i in 1..a.size - 1) {
                if (a[i - 1] < a[i])
                    return false
            }
        }
        return true
    }

    fun randomArr(): IntArray {
        val random = Random()
        val len = 15 + random.nextInt(16)
        val result = IntArray(len)
        for (i in 0..len - 1) {
            val `val` = random.nextInt(100)
            result[i] = if (random.nextInt(2) == 0) `val` else -`val`
        }
        return result
    }

}

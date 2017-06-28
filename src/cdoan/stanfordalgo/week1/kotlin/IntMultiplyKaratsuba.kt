package cdoan.stanfordalgo.week1.kotlin

import java.math.BigDecimal
import java.math.BigInteger


/**
 * Created by co on 14/06/2017.
 */


fun main(args: Array<String>) {
    //val a = 698.bi
    //val b = 856.bi
    val a = BigInteger("3141592653589793238462643383279502884197169399375105820974944592")
    val b = BigInteger("2718281828459045235360287471352662497757247093699959574966967627")
    println("$a * $b = ${a * b}")
    val karatsuba = IntMultiplyKaratsuba()
    println("multiply($a, $b) = ${karatsuba.multiply(a, b)}")
    println("multiply($a, $b) == $a * $b : ${a * b == karatsuba.multiply(a, b)}")
}


class IntMultiplyKaratsuba {
    val base = BigInteger.TEN

    fun multiply(x: BigInteger, y: BigInteger): BigInteger {
        // base case
        if (x < base || y < base)
            return x * y

        // express x = ab, y = cd
        val xStr = x.toString()
        val yStr = y.toString()
        val xDigits = xStr.length
        val yDigits = yStr.length
        val n = xDigits / 2 //number of digits in b
        val a = BigInteger(xStr.substring(0, xDigits-n))
        val b = BigInteger(xStr.substring(xDigits-n))
        val m = yDigits / 2 //number of digits in d
        val c = BigInteger(yStr.substring(0, yDigits-m))
        val d = BigInteger(yStr.substring(yDigits-m))
        val a_times_c = multiply(a, c)
        val b_times_d = multiply(b, d)

        if (n == m) {
            // x * y = ab * cd = 10^2n * a*c + 10^n * (a*d + b*c) + b*d
            // Note: a*d + b*c = (a+b) * (c+d) - a*c - b*d
            val a_plus_b_times_c_plus_d = multiply(a+b, c+d)
            val a_times_d_plus_b_times_c = a_plus_b_times_c_plus_d - a_times_c - b_times_d
            return base.pow(2 * n) * a_times_c + base.pow(n) * a_times_d_plus_b_times_c + b_times_d
        }

        // x * y = ab * cd = 10^(n+m) * a*c + 10^n * a*d + 10^m * b*c + b*d
        val a_times_d = multiply(a, d)
        val b_times_c = multiply(b, c)
        return base.pow(n + m) * a_times_c + base.pow(n) * a_times_d + base.pow(m) * b_times_c + b_times_d
    }
}

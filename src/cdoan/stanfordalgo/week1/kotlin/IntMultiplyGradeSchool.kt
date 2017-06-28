package cdoan.stanfordalgo.week1.kotlin

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
    println("multiply($a, $b) = ${multiply(a, b)}")
    println("multiply($a, $b) == $a * $b : ${a * b == multiply(a, b)}")
}

fun multiply(a: BigInteger, b: BigInteger): BigInteger {
    if (a == 0.bi || b == 0.bi)
        return 0.bi

    val base = 10.bi
    var result = BigInteger.ZERO
    var y = b
    var yDigitPos = 0
    while (y != BigInteger.valueOf(0)) {
        val yDigit = y % base
        var xDigitPos = 0
        var x = a
        while (x != 0.bi) {
            val xDigit = x % base
            result += yDigit * xDigit * base.pow(yDigitPos + xDigitPos)
            xDigitPos++
            x /= base
        }
        yDigitPos++
        y /= base
    }
    return result
}

val  Int.bi: BigInteger
    get() = BigInteger.valueOf(this.toLong())

/*
private fun Int.pow(exponent: Int): Int {
    if (exponent == 0)
        return 1

    var result = 1
    for (i in 1..exponent) {
        result *= this
    }
    return result
}
*/

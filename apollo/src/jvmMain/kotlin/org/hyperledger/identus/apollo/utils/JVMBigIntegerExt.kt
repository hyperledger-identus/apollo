package org.hyperledger.identus.apollo.utils

import com.ionspin.kotlin.bignum.integer.Sign
import java.math.BigInteger

/**
 * Converts a BigInteger to an unsigned byte array.
 *
 * @return the unsigned byte array representation of the BigInteger
 */
fun BigInteger.toUnsignedByteArray(): ByteArray {
    return toByteArray().dropWhile { it == 0.toByte() }.toByteArray()
}

/**
 * Converts a Java BigInteger to the KotlinBigInteger class from the com.ionspin.kotlin.bignum.integer package.
 *
 * @return The KotlinBigInteger representation of the Java BigInteger.
 * @throws IllegalStateException if the signum of the Java BigInteger is not -1, 0, or 1.
 * @see BigInteger.toUnsignedByteArray
 */
fun BigInteger.toKotlinBigInteger(): com.ionspin.kotlin.bignum.integer.BigInteger {
    val sign =
        when (this.signum()) {
            -1 -> Sign.NEGATIVE
            0 -> Sign.ZERO
            1 -> Sign.POSITIVE
            else -> throw IllegalStateException("Illegal BigInteger sign")
        }
    return com.ionspin.kotlin.bignum.integer.BigInteger.fromByteArray(this.toUnsignedByteArray(), sign)
}

/**
 * Converts a `BigInteger` from the `com.ionspin.kotlin.bignum.integer` package to the Java `BigInteger` class.
 *
 * @return the converted `BigInteger` object.
 */
fun com.ionspin.kotlin.bignum.integer.BigInteger.toJavaBigInteger(): BigInteger {
    return BigInteger(this.signum(), this.toByteArray())
}

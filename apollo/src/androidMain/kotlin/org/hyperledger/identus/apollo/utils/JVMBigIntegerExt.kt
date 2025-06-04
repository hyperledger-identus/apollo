package org.hyperledger.identus.apollo.utils

import com.ionspin.kotlin.bignum.integer.Sign
import java.math.BigInteger

/**
 * Converts a BigInteger to an unsigned byte array.
 *
 * @return The unsigned byte array representation of the BigInteger.
 */
fun BigInteger.toUnsignedByteArray(): ByteArray {
    return toByteArray().dropWhile { it == 0.toByte() }.toByteArray()
}

/**
 * Converts a `java.math.BigInteger` to a `com.ionspin.kotlin.bignum.integer.BigInteger`.
 *
 * @return The converted `com.ionspin.kotlin.bignum.integer.BigInteger` representation of the original `BigInteger`.
 * @throws IllegalStateException if the original `BigInteger` has an illegal sign.
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
 * Converts this Kotlin BigInteger to a Java BigInteger.
 *
 * @return the Java BigInteger representation of this Kotlin BigInteger.
 */
fun com.ionspin.kotlin.bignum.integer.BigInteger.toJavaBigInteger(): BigInteger {
    return BigInteger(this.signum(), this.toByteArray())
}

package org.hyperledger.identus.apollo.derivation

import com.ionspin.kotlin.bignum.integer.BigInteger

/**
 * Data class representing the options for an HDKey.
 *
 * @property versions The versions for the HDKey.
 * @property chainCode The chain code for the HDKey.
 * @property depth The depth of the HDKey.
 * @property parentFingerprint The parent's fingerprint of the HDKey.
 * @property index The index of the HDKey.
 * @property privateKey The private key of the HDKey.
 * @property publicKey The public key of the HDKey.
 */
data class HDKeyOptions(
    val versions: Pair<Int, Int>,
    val chainCode: ByteArray,
    val depth: Int,
    val parentFingerprint: Int?,
    val index: BigInteger,
    var privateKey: ByteArray? = null,
    var publicKey: ByteArray? = null
) {
    /**
     * Determines whether the current [HDKeyOptions] object is equal to another object.
     *
     * @param other The object to compare with the current [HDKeyOptions] object.
     * @return `true` if the specified object is equal to the current [HDKeyOptions] object; otherwise, `false`.
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as HDKeyOptions

        if (versions != other.versions) return false
        if (!chainCode.contentEquals(other.chainCode)) return false
        if (depth != other.depth) return false
        if (parentFingerprint != other.parentFingerprint) return false
        if (index != other.index) return false
        if (privateKey != null) {
            if (other.privateKey == null) return false
            if (!privateKey.contentEquals(other.privateKey)) return false
        } else if (other.privateKey != null) {
            return false
        }
        if (publicKey != null) {
            if (other.publicKey == null) return false
            if (!publicKey.contentEquals(other.publicKey)) return false
        } else if (other.publicKey != null) {
            return false
        }

        return true
    }

    /**
     * Calculates the hash code for the current [HDKeyOptions] object.
     *
     * @return The hash code value for this object.
     */
    override fun hashCode(): Int {
        var result = versions.hashCode()
        result = 31 * result + chainCode.contentHashCode()
        result = 31 * result + depth
        result = 31 * result + (parentFingerprint ?: 0)
        result = 31 * result + index.hashCode()
        result = 31 * result + (privateKey?.contentHashCode() ?: 0)
        result = 31 * result + (publicKey?.contentHashCode() ?: 0)
        return result
    }
}

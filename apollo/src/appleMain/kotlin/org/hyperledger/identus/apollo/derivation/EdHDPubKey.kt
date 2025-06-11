package org.hyperledger.identus.apollo.derivation

import com.ionspin.kotlin.bignum.integer.toBigInteger
import uniffi.ed25519_bip32_wrapper.deriveBytesPub

/**
 * Represents and HDKey with its derive methods
 */
actual class EdHDPubKey actual constructor(
    actual val publicKey: ByteArray,
    actual val chainCode: ByteArray,
    actual val depth: Int,
    actual val index: BigIntegerWrapper
) {
    /**
     * Method to derive an HDKey by a path
     *
     * @param path value used to derive a key
     */
    actual fun derive(path: String): EdHDPubKey {
        if (!path.matches(Regex("^[mM].*"))) {
            throw Error("Path must start with \"m\" or \"M\"")
        }
        if (Regex("^[mM]'?$").matches(path)) {
            return this
        }
        val parts = path.replace(Regex("^[mM]'?/"), "").split("/")
        var child = this

        for (c in parts) {
            val m = Regex("^(\\d+)('?)$").find(c)?.groupValues
            if (m == null || m.size != 3) {
                throw Error("Invalid child index: $c")
            }
            val idx = m[1].toBigInteger()
            if (idx >= HDKey.HARDENED_OFFSET) {
                throw Error("Invalid index")
            }
            val finalIdx = if (m[2] == "'") idx + HDKey.HARDENED_OFFSET else idx

            child = child.deriveChild(BigIntegerWrapper(finalIdx))
        }

        return child
    }

    /**
     * Method to derive an HDKey child by index
     *
     * @param wrappedIndex value used to derive a key
     */
    actual fun deriveChild(wrappedIndex: BigIntegerWrapper): EdHDPubKey {
        val index = wrappedIndex.value.uintValue()
        val derived = deriveBytesPub(publicKey, chainCode, index)
        val publicKey = derived["public_key"]
        val chainCode = derived["chain_code"]

        if (publicKey == null || chainCode == null) {
            throw Error("Unable to derive key")
        }

        return EdHDPubKey(
            publicKey = publicKey,
            chainCode = chainCode,
            depth = depth + 1,
            index = wrappedIndex
        )
    }
}

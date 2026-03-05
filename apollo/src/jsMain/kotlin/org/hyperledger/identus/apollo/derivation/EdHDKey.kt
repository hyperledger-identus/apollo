package org.hyperledger.identus.apollo.derivation

import com.ionspin.kotlin.bignum.integer.toBigInteger
import org.hyperledger.identus.apollo.utils.ECConfig
import org.hyperledger.identus.apollo.utils.external.ed25519_bip32

/**
 * Represents and HDKey with its derive methods
 */
@OptIn(ExperimentalJsExport::class)
@JsExport
actual class EdHDKey actual constructor(
    actual val privateKey: ByteArray,
    actual val chainCode: ByteArray,
    actual val depth: Int,
    actual val index: BigIntegerWrapper
) {
    /**
     * Method to derive an HDKey by a path
     *
     * @param path value used to derive a key
     */
    actual fun derive(path: String): EdHDKey {
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
    actual fun deriveChild(wrappedIndex: BigIntegerWrapper): EdHDKey {
        val derived = ed25519_bip32.derive_bytes(privateKey, chainCode, wrappedIndex.value.uintValue())

        return EdHDKey(
            privateKey = derived[0],
            chainCode = derived[1],
            depth = depth + 1,
            index = wrappedIndex
        )
    }

    actual companion object {
        /**
         * Constructs a new EdHDKey object from a seed
         *
         * @param seed The seed used to derive the private key and chain code.
         * @throws IllegalArgumentException if the seed length is not equal to 64.
         */
        actual fun initFromSeed(seed: ByteArray): EdHDKey {
            require(seed.size == 64) {
                "Seed expected byte length to be ${ECConfig.SEED_BYTE_SIZE}"
            }

            val key = seed.sliceArray(0 until 32)
            val chainCode = seed.sliceArray(32 until seed.size)
            val result = ed25519_bip32.from_nonextended(key, chainCode)

            return EdHDKey(
                privateKey = result[0],
                chainCode = result[1]
            )
        }
    }
}

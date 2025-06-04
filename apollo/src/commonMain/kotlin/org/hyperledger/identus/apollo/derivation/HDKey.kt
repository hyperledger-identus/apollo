package org.hyperledger.identus.apollo.derivation

import com.ionspin.kotlin.bignum.integer.BigInteger
import com.ionspin.kotlin.bignum.integer.toBigInteger
import org.hyperledger.identus.apollo.utils.ECConfig
import org.hyperledger.identus.apollo.utils.ECPrivateKeyDecodingException
import org.hyperledger.identus.apollo.utils.KMMECSecp256k1PrivateKey
import org.kotlincrypto.macs.hmac.sha2.HmacSHA512
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport
import kotlin.js.JsName

@OptIn(ExperimentalJsExport::class)
@JsExport
class BigIntegerWrapper {
    internal val value: BigInteger

    @JsName("initFromInt")
    constructor(int: Int) {
        value = BigInteger(int)
    }

    @JsName("initFromLong")
    constructor(
        @Suppress("NON_EXPORTABLE_TYPE") long: Long
    ) {
        value = BigInteger(long)
    }

    @JsName("initFromShort")
    constructor(short: Short) {
        value = BigInteger(short)
    }

    @JsName("initFromByte")
    constructor(byte: Byte) {
        value = BigInteger(byte)
    }

    @JsName("initFromString")
    constructor(string: String) {
        value = BigInteger.parseString(string)
    }

    @JsName("initFromBigInteger")
    constructor(
        @Suppress("NON_EXPORTABLE_TYPE") bigInteger: BigInteger
    ) {
        value = bigInteger
    }

    /**
     * Checks whether this BigIntegerWrapper is equal to the specified object.
     *
     * Two BigIntegerWrapper objects are considered equal if they have the same value.
     *
     * @param other the object to compare with this BigIntegerWrapper
     * @return true if the specified object is equal to this BigIntegerWrapper, false otherwise
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as BigIntegerWrapper

        return value == other.value
    }

    /**
     * Returns a hash code value for the object.
     *
     * The hash code value is based on the value of the BigIntegerWrapper object.
     *
     * @return the hash code value for the object
     */
    override fun hashCode(): Int {
        return value.hashCode()
    }
}

/**
 * Represents and HDKey with its derive methods
 */
@OptIn(ExperimentalJsExport::class)
@JsExport
class HDKey(
    val privateKey: ByteArray? = null,
    val publicKey: ByteArray? = null,
    val chainCode: ByteArray? = null,
    val depth: Int = 0,
    val childIndex: BigIntegerWrapper = BigIntegerWrapper(0)
) {
    /**
     * Constructs a new HDKey object from a seed, depth, and child index.
     *
     * @param seed The seed used to derive the private key and chain code.
     * @param depth The depth of the HDKey.
     * @param childIndex The child index of the HDKey.
     *
     * @throws IllegalArgumentException if the seed length is not equal to 64.
     */
    @JsName("InitFromSeed")
    constructor(seed: ByteArray, depth: Int, childIndex: BigIntegerWrapper) : this(
        privateKey = sha512(key = "Bitcoin seed".encodeToByteArray(), input = seed).sliceArray(IntRange(0, 31)),
        chainCode = sha512("Bitcoin seed".encodeToByteArray(), seed).sliceArray(32 until seed.size),
        depth = depth,
        childIndex = childIndex
    ) {
        require(seed.size == 64) {
            "Seed expected byte length to be ${ECConfig.PRIVATE_KEY_BYTE_SIZE}"
        }
    }

    /**
     * Constructs an HDKey by initializing the private key, chain code, depth, and child index from the provided seed.
     *
     * @param seed        The seed used to derive the private key and chain code.
     * @param depth       The depth of the HDKey.
     * @param childIndex  The child index of the HDKey.
     * @throws IllegalArgumentException if the seed size is not 64 bytes.
     */
    @JsName("InitFromSeedFromBigIntegerString")
    constructor(seed: ByteArray, depth: Int, childIndex: Int) : this(
        privateKey = sha512(key = "Bitcoin seed".encodeToByteArray(), input = seed).sliceArray(IntRange(0, 31)),
        chainCode = sha512("Bitcoin seed".encodeToByteArray(), seed).sliceArray(32 until seed.size),
        depth = depth,
        childIndex = BigIntegerWrapper(childIndex)
    ) {
        require(seed.size == 64) {
            "Seed expected byte length to be ${ECConfig.PRIVATE_KEY_BYTE_SIZE}"
        }
    }

    /**
     * Method to derive an HDKey by a path
     *
     * @param path value used to derive a key
     */
    fun derive(path: String): HDKey {
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
            if (idx >= HARDENED_OFFSET) {
                throw Error("Invalid index")
            }
            val finalIdx = if (m[2] == "'") idx + HARDENED_OFFSET else idx
            child = child.deriveChild(BigIntegerWrapper(finalIdx))
        }
        return child
    }

    /**
     * Method to derive an HDKey child by index
     *
     * @param index value used to derive a key
     */
    fun deriveChild(index: BigIntegerWrapper): HDKey {
        @Suppress("NAME_SHADOWING")
        val index = index.value
        if (chainCode == null) {
            throw Exception("No chainCode set")
        }

        val data =
            if (index >= HARDENED_OFFSET) {
                val priv = privateKey ?: throw Error("Could not derive hardened child key")
                byteArrayOf(0) + priv + index.toByteArray()
            } else {
                throw Exception("Not supported")
            }

        val i = sha512(chainCode, data)
        val childTweak = i.sliceArray(IntRange(0, 31))
        val newChainCode = i.sliceArray(32 until i.size)

        if (!isValidPrivateKey(childTweak)) {
            throw ECPrivateKeyDecodingException(
                "Expected encoded byte length to be ${ECConfig.PRIVATE_KEY_BYTE_SIZE}, but got ${data.size}"
            )
        }

        val opt =
            HDKeyOptions(
                versions = Pair(BITCOIN_VERSIONS_PRIVATE, BITCOIN_VERSIONS_PUBLIC),
                chainCode = newChainCode,
                depth = depth + 1,
                parentFingerprint = null,
                index = index
            )

        opt.privateKey = KMMECSecp256k1PrivateKey.tweak(privateKey, childTweak).raw
        return HDKey(
            privateKey = opt.privateKey,
            chainCode = opt.chainCode,
            depth = opt.depth,
            childIndex = BigIntegerWrapper(opt.index)
        )
    }

    /**
     * Method to derive an HDKey child by index
     *
     * @param index value used to derive a key as Int
     */
    @JsName("deriveFromInt")
    fun deriveChild(index: Int): HDKey {
        return this.deriveChild(BigIntegerWrapper(BigInteger(index)))
    }

    /**
     * Method to get the KMMECSecp256k1PrivateKey from HDKey
     *
     * @return KMMECSecp256k1PrivateKey
     */
    fun getKMMSecp256k1PrivateKey(): KMMECSecp256k1PrivateKey {
        privateKey?.let {
            return KMMECSecp256k1PrivateKey.secp256k1FromByteArray(privateKey)
        } ?: throw Exception("Private key not available")
    }

    /**
     * Determines if the provided private key data is valid.
     *
     * @param data The byte array representing the private key data.
     * @return True if the private key data is valid, false otherwise.
     */
    private fun isValidPrivateKey(data: ByteArray): Boolean {
        return (data.size == ECConfig.PRIVATE_KEY_BYTE_SIZE)
    }

    companion object {
        /**
         * This variable represents the hardened offset for deriving HD keys.
         *
         * @suppress This symbol is not intended to be used outside of this module.
         */
        @Suppress("NON_EXPORTABLE_TYPE")
        const val HARDENED_OFFSET = 2147483648

        /**
         * Represents the version number for Bitcoin private keys.
         */
        const val BITCOIN_VERSIONS_PRIVATE = 0x0488ade4

        /**
         * Public bitcoin versions constant.
         */
        const val BITCOIN_VERSIONS_PUBLIC = 0x0488b21e
        const val FINGERPRINT = 0
        const val MASTER_SECRET = "Atala Prism"

        /**
         * Calculates the SHA-512 hash of the given key and input.
         *
         * @param key The key to use for HMAC-SHA512. It should be a byte array.
         * @param input The input data for which the hash needs to be calculated. It should be a byte array.
         * @return The SHA-512 hash of the input data. It is returned as a byte array.
         */
        fun sha512(key: ByteArray, input: ByteArray): ByteArray {
            val sha512 = HmacSHA512(key)
            sha512.update(input)
            return sha512.doFinal()
        }
    }
}

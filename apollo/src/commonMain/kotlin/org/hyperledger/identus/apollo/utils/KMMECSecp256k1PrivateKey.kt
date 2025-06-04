package org.hyperledger.identus.apollo.utils

import org.hyperledger.identus.apollo.secp256k1.Secp256k1Lib
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport
import kotlin.js.JsName

@OptIn(ExperimentalJsExport::class)
@JsExport
interface KMMECSecp256k1PrivateKeyCommonStaticInterface {
    /**
     * Constructs a [KMMECSecp256k1PrivateKey] from a byte array.
     *
     * @param d The byte array representing the private key.
     * @return The constructed [KMMECSecp256k1PrivateKey] object.
     */
    @JsName("secp256k1FromByteArray")
    fun secp256k1FromByteArray(d: ByteArray): KMMECSecp256k1PrivateKey {
        return KMMECSecp256k1PrivateKey(d)
    }

    /**
     * Creates a tweaked instance of [KMMECSecp256k1PrivateKey] by deriving a new private key from the
     * provided private key data and derivation private key data.
     *
     * @param privateKeyData The byte array representing the original private key.
     * @param derivationPrivateKeyData The byte array used for deriving the new private key.
     * @return A [KMMECSecp256k1PrivateKey] object representing the derived private key.
     * @throws ECPrivateKeyDecodingException if an error occurs while tweaking.
     */
    fun tweak(
        privateKeyData: ByteArray,
        derivationPrivateKeyData: ByteArray
    ): KMMECSecp256k1PrivateKey {
        val derivedKey =
            org.hyperledger.identus.apollo.secp256k1.Secp256k1Lib()
                .derivePrivateKey(privateKeyData, derivationPrivateKeyData)
        return derivedKey?.let { KMMECSecp256k1PrivateKey(derivedKey) }
            ?: run { throw ECPrivateKeyDecodingException("Error while tweaking") }
    }
}

/**
 * Definition of the KMMECSecp256k1PublicKey functionality
 */
@OptIn(ExperimentalJsExport::class)
@JsExport
class KMMECSecp256k1PrivateKey : Encodable {
    val raw: ByteArray

    @JsName("fromByteArray")
    constructor(raw: ByteArray) {
        this.raw = raw
    }

    /**
     * Method to fetch the KMMECSecp256k1PublicKey
     *
     * @return KMMECSecp256k1PublicKey
     */
    fun getPublicKey(): KMMECSecp256k1PublicKey {
        val pubKeyBytes = org.hyperledger.identus.apollo.secp256k1.Secp256k1Lib().createPublicKey(raw, false)
        return KMMECSecp256k1PublicKey(pubKeyBytes)
    }

    /**
     * Method to get the encoded raw value
     *
     * @return ByteArray representing the raw value of this KMMECSecp256k1PrivateKey
     */
    override fun getEncoded(): ByteArray {
        return raw
    }

    /**
     * Sign the provided data with EC Secp256K1
     * @param data data that you want to sign
     * @return signature
     */
    fun sign(data: ByteArray): ByteArray {
        val secp256k1Lib = org.hyperledger.identus.apollo.secp256k1.Secp256k1Lib()
        return secp256k1Lib.sign(raw, data)
    }

    /**
     * Verify provided signature with the public key that is generated using the private key
     * @param signature that we need to verify
     * @param data that was used in signature
     * @return true when valid, false when invalid
     */
    fun verify(signature: ByteArray, data: ByteArray): Boolean {
        val secp256k1Lib = org.hyperledger.identus.apollo.secp256k1.Secp256k1Lib()
        return secp256k1Lib.verify(getPublicKey().raw, signature, data)
    }

    companion object : KMMECSecp256k1PrivateKeyCommonStaticInterface
}

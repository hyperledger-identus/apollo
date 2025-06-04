package org.hyperledger.identus.apollo.utils

import kotlin.js.ExperimentalJsExport

/**
 * Represents a key pair consisting of a private key and a public key.
 *
 * @property privateKey The private key of the key pair.
 * @property publicKey The public key of the key pair.
 */
actual class KMMEdKeyPair actual constructor(
    actual val privateKey: KMMEdPrivateKey,
    actual val publicKey: KMMEdPublicKey
) {
    @OptIn(ExperimentalJsExport::class)
    public actual companion object : Ed25519KeyPairGeneration {
        /**
         * Generates a key pair consisting of a private key and a public key.
         *
         * @return A [KMMEdKeyPair] instance representing the generated public and private keys.
         */
        public actual override fun generateKeyPair(): KMMEdKeyPair {
            val privateKey = KMMEdPrivateKey()
            return KMMEdKeyPair(privateKey, privateKey.publicKey())
        }
    }

    /**
     * Method to sign a provided message.
     *
     * @param message A ByteArray with the message to be signed.
     * @return A ByteArray containing the signed message.
     * @throws RuntimeException if signing fails for any reason.
     */
    @Throws(RuntimeException::class)
    actual fun sign(message: ByteArray): ByteArray {
        return privateKey.sign(message)
    }

    /**
     * Method to verify a provided message and signature
     *
     * @param message A ByteArray with the original message
     * @param sig The signature to be verified
     * @return A boolean that tells us if the signature matches the original message
     * @throws RuntimeException if verification fails or result is null
     */
    @Throws(RuntimeException::class)
    actual fun verify(message: ByteArray, sig: ByteArray): Boolean {
        return publicKey.verify(message, sig)
    }
}

package org.hyperledger.identus.apollo.utils

import node.buffer.Buffer
import org.hyperledger.identus.apollo.utils.external.eddsa
import org.hyperledger.identus.apollo.utils.external.rand

/**
 * Represents a pair of cryptographic keys - a private key and a public key.
 *
 * @property privateKey The private key of the key pair.
 * @property publicKey The public key of the key pair.
 */
@OptIn(ExperimentalJsExport::class)
@JsExport
actual class KMMEdKeyPair actual constructor(
    actual val privateKey: KMMEdPrivateKey,
    actual val publicKey: KMMEdPublicKey
) {
    /**
     * Cryptographically sign a given message.
     *
     * @param message The message to be signed.
     * @return The signature of the message.
     */
    actual fun sign(message: ByteArray): ByteArray {
        return privateKey.sign(message)
    }

    /**
     * Confirms a message signature was signed with the corresponding PrivateKey.
     *
     * @param message The message that was signed.
     * @param sig The signature.
     * @return Returns true if the signature matches the original message, false otherwise.
     */
    actual fun verify(message: ByteArray, sig: ByteArray): Boolean {
        return publicKey.verify(message, sig)
    }

    actual companion object : Ed25519KeyPairGeneration {
        /**
         * Generates a pair of cryptographic keys - a private key and a public key.
         *
         * @return The generated key pair.
         */
        actual override fun generateKeyPair(): KMMEdKeyPair {
            val ed25519 = eddsa("ed25519")
            val rnd = rand(32)
            val secret = Buffer.from(rnd)
            val keypair = ed25519.keyFromSecret(secret)
            val public = keypair.getPublic()

            return KMMEdKeyPair(KMMEdPrivateKey(secret.toByteArray()), KMMEdPublicKey(public))
        }
    }
}

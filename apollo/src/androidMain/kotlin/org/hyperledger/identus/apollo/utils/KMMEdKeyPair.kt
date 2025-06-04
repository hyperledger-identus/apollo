package org.hyperledger.identus.apollo.utils

import org.bouncycastle.crypto.generators.Ed25519KeyPairGenerator
import org.bouncycastle.crypto.params.Ed25519KeyGenerationParameters
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters
import java.security.SecureRandom
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
    actual companion object : Ed25519KeyPairGeneration {
        /**
         * Generates a key pair consisting of a private key and a public key.
         *
         * @return A [KMMEdKeyPair] instance representing the generated key pair.
         */
        actual override fun generateKeyPair(): KMMEdKeyPair {
            val generator = Ed25519KeyPairGenerator()
            generator.init(Ed25519KeyGenerationParameters(SecureRandom()))
            val pair = generator.generateKeyPair()
            return KMMEdKeyPair(
                privateKey = KMMEdPrivateKey((pair.private as Ed25519PrivateKeyParameters).encoded),
                publicKey = KMMEdPublicKey((pair.public as Ed25519PublicKeyParameters).encoded)
            )
        }
    }

    /**
     * Method to sign a provided message
     *
     * @param message A ByteArray with the message to be signed
     * @return A ByteArray conforming the signed message
     */
    actual fun sign(message: ByteArray): ByteArray {
        return privateKey.sign(message)
    }

    /**
     * Verifies the signature of a message using the provided public key.
     *
     * @param message The message to verify.
     * @param sig The signature to verify.
     * @return true if the signature is valid, false otherwise.
     */
    actual fun verify(message: ByteArray, sig: ByteArray): Boolean {
        return publicKey.verify(message, sig)
    }
}

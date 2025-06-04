package org.hyperledger.identus.apollo.utils

import org.hyperledger.identus.apollo.utils.external.generateKeyPair as stableLibGenerateKeyPair

/**
 * Represents a key pair for the X25519 elliptic curve encryption algorithm.
 *
 * @property privateKey The private key of the key pair.
 * @property publicKey The public key of the key pair.
 */
@OptIn(ExperimentalJsExport::class)
@JsExport
actual class KMMX25519KeyPair actual constructor(
    actual val privateKey: KMMX25519PrivateKey,
    actual val publicKey: KMMX25519PublicKey
) {
    actual companion object : X25519KeyPairGeneration {
        /**
         * Generates a key pair using the X25519 elliptic curve encryption algorithm.
         *
         * @return The generated key pair as a [KMMX25519KeyPair] object.
         */
        actual override fun generateKeyPair(): KMMX25519KeyPair {
            val keyPair = stableLibGenerateKeyPair()

            return KMMX25519KeyPair(
                KMMX25519PrivateKey(keyPair.secretKey.buffer.toByteArray()),
                KMMX25519PublicKey(keyPair.publicKey.buffer.toByteArray())
            )
        }
    }
}

package org.hyperledger.identus.apollo.utils

import kotlin.js.ExperimentalJsExport

/**
 * Represents a key pair for the X25519 elliptic curve encryption algorithm.
 *
 * @property privateKey The private key of the key pair.
 * @property publicKey The public key of the key pair.
 */
actual class KMMX25519KeyPair actual constructor(
    actual val privateKey: KMMX25519PrivateKey,
    actual val publicKey: KMMX25519PublicKey
) {
    @OptIn(ExperimentalJsExport::class)
    public actual companion object : X25519KeyPairGeneration {
        /**
         * Generates a key pair using the KMMX25519 algorithm.
         *
         * @return The generated key pair, consisting of a private key and its corresponding public key.
         */
        public actual override fun generateKeyPair(): KMMX25519KeyPair {
            val privateKey = KMMX25519PrivateKey()
            return KMMX25519KeyPair(privateKey, privateKey.publicKey())
        }
    }
}

package org.hyperledger.identus.apollo.utils

import kotlin.js.ExperimentalJsExport

/**
 * Interface defining the functionality for generating KMMX25519 key pairs.
 */
@OptIn(ExperimentalJsExport::class)
expect class KMMX25519KeyPair(privateKey: KMMX25519PrivateKey, publicKey: KMMX25519PublicKey) {
    val privateKey: KMMX25519PrivateKey
    val publicKey: KMMX25519PublicKey

    companion object : X25519KeyPairGeneration {
        override fun generateKeyPair(): KMMX25519KeyPair
    }
}

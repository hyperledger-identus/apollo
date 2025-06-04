package org.hyperledger.identus.apollo.utils

import kotlin.js.ExperimentalJsExport

/**
 * Interface defining the functionality for generating KMMEd key pairs.
 */
expect class KMMEdKeyPair(privateKey: KMMEdPrivateKey, publicKey: KMMEdPublicKey) {
    val privateKey: KMMEdPrivateKey
    val publicKey: KMMEdPublicKey

    @OptIn(ExperimentalJsExport::class)
    companion object : Ed25519KeyPairGeneration {
        override fun generateKeyPair(): KMMEdKeyPair
    }

    /**
     * Method to sign a provided message
     *
     * @param message A ByteArray with the message to be signed
     * @return A ByteArray conforming the signed message
     */
    fun sign(message: ByteArray): ByteArray

    /**
     * Method to verify a provided message and signature
     *
     * @param message A ByteArray with the original message
     * @param sig The signature to be verified
     * @return A boolean that tell us if the signature matches the original message
     */
    fun verify(message: ByteArray, sig: ByteArray): Boolean
}

package org.hyperledger.identus.apollo.utils

/**
 * Definition of the KMMEdPrivateKey functionality
 */
public expect class KMMEdPrivateKey {
    /**
     * Method to sign a message using this KMMEdPrivateKey
     *
     * @param message ByteArray representing the message to be signed
     * @return ByteArray representing the signed message
     */
    fun sign(message: ByteArray): ByteArray

    /**
     * Method convert an ed25519 private key to a x25519 private key
     *
     * @return KMMX25519PrivateKey private key
     */
    fun x25519PrivateKey(): KMMX25519PrivateKey
}

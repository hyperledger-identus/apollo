package org.hyperledger.identus.apollo.utils

/**
 * Definition of the KMMEdPublicKey functionality
 */
public expect class KMMEdPublicKey {
    /**
     * Method to verify a signature against the original message
     *
     * @param message ByteArray representing the original message
     * @param sig ByteArray representing the signed message
     * @return Boolean that tell us if the signature matches the original message
     */
    fun verify(message: ByteArray, sig: ByteArray): Boolean
}

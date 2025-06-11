package org.hyperledger.identus.apollo.derivation

expect class EdHDPubKey constructor(
    publicKey: ByteArray,
    chainCode: ByteArray,
    depth: Int = 0,
    index: BigIntegerWrapper = BigIntegerWrapper(0)
) {
    val publicKey: ByteArray
    val chainCode: ByteArray
    val depth: Int
    val index: BigIntegerWrapper

    /**
     * Method to derive an HDKey by a path
     *
     * @param path value used to derive a key
     */
    fun derive(path: String): EdHDPubKey

    /**
     * Method to derive an HDKey child by index
     *
     * @param wrappedIndex value used to derive a key
     */
    fun deriveChild(wrappedIndex: BigIntegerWrapper): EdHDPubKey
}

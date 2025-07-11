package org.hyperledger.identus.apollo.securerandom

/**
 * The SecureRandom class provides a platform-specific implementation for generating secure random numbers.
 */
expect class SecureRandom
/**
 * The SecureRandom class provides a platform-specific implementation for generating secure random numbers.
 *
 * @constructor Creates an instance of SecureRandom with an optional seed value.
 * @param seed The seed value used for initializing the random number generator.
 */
constructor(
    seed: ByteArray = ByteArray(0)
) : org.hyperledger.identus.apollo.securerandom.SecureRandomInterface {
    val seed: ByteArray

    override fun nextBytes(size: Int): ByteArray

    companion object : SecureRandomStaticInterface {
        override fun generateSeed(numBytes: Int): ByteArray
    }
}

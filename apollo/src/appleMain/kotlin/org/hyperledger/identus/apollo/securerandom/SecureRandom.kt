package org.hyperledger.identus.apollo.securerandom

import kotlinx.cinterop.ExperimentalForeignApi
import org.hyperledger.identus.apollo.utils.toByteArray
import swift.secureRandomGeneration.IOHKSecureRandomGeneration

/**
 * The SecureRandom class provides a platform-specific implementation for generating secure random numbers.
 */
@OptIn(ExperimentalForeignApi::class)
actual class SecureRandom actual constructor(
    actual val seed: ByteArray
) : org.hyperledger.identus.apollo.securerandom.SecureRandomInterface {
    /**
     * Generates a specified number of secure random bytes.
     *
     * @param size The number of random bytes to generate.
     * @return A byte array containing the generated random bytes.
     */
    actual override fun nextBytes(size: Int): ByteArray {
        return IOHKSecureRandomGeneration.randomDataWithLength(size.toLong()).toByteArray()
    }

    actual companion object : SecureRandomStaticInterface {
        /**
         * Generates a random seed of the specified length in bytes.
         *
         * @param numBytes The length of the seed in bytes.
         * @return The generated seed as a ByteArray.
         */
        actual override fun generateSeed(numBytes: Int): ByteArray {
            return IOHKSecureRandomGeneration.randomDataWithLength(numBytes.toLong()).toByteArray()
        }
    }
}

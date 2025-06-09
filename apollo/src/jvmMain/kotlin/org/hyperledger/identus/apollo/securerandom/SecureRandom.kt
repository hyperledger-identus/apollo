package org.hyperledger.identus.apollo.securerandom

/**
 * The SecureRandom class provides a platform-specific implementation for generating secure random numbers.
 *
 * @property seed The seed value used for initializing the random number generator.
 *
 * @see [java.security.SecureRandom]
 */
actual class SecureRandom actual constructor(
    actual val seed: ByteArray
) : org.hyperledger.identus.apollo.securerandom.SecureRandomInterface, java.security.SecureRandom() {
    /**
     * The `jvmSecureRandom` variable is a private property of type `java.security.SecureRandom`. It is used for generating secure random numbers on the JVM platform.
     *
     * This property is initialized using `java.security.SecureRandom.getInstanceStrong()` method, which provides a platform-specific implementation for generating secure random numbers
     *.
     *
     * The `jvmSecureRandom` property is typically used within the `SecureRandom` class and its companion object for generating random bytes by calling the `nextBytes()` method.
     *
     * Example usage:
     * ```
     * val secureRandom = SecureRandom(byteArrayOf(1, 2, 3))
     */
    private val jvmSecureRandom: java.security.SecureRandom = java.security.SecureRandom.getInstanceStrong()

    init {
        jvmSecureRandom.setSeed(seed)
    }

    /**
     * Generates a specified number of secure random bytes.
     *
     * @param size The number of random bytes to generate.
     * @return A byte array containing the generated random bytes.
     */
    actual override fun nextBytes(size: Int): ByteArray {
        val bytes = ByteArray(size)
        jvmSecureRandom.nextBytes(bytes)
        return bytes
    }

    actual companion object : SecureRandomStaticInterface {
        /**
         * The `jvmSecureRandom` variable is a private property of type `java.security.SecureRandom`. It is used for generating secure random numbers on the JVM platform.
         *
         * This property is initialized using `java.security.SecureRandom.getInstanceStrong()` method, which provides a platform-specific implementation for generating secure random numbers
         *.
         *
         * The `jvmSecureRandom` property is typically used within the `SecureRandom` class and its companion object for generating random bytes by calling the `nextBytes()` method.
         *
         * Example usage:
         * ```
         * val secureRandom = SecureRandom(byteArrayOf(1, 2, 3))
         */
        private val jvmSecureRandom: java.security.SecureRandom = java.security.SecureRandom.getInstanceStrong()

        /**
         * Generates a specified number of random bytes using the underlying JVM secure random generator.
         *
         * @param numBytes The number of random bytes to generate.
         * @return A byte array containing the generated random bytes.
         */
        actual override fun generateSeed(numBytes: Int): ByteArray {
            return jvmSecureRandom.generateSeed(numBytes)
        }
    }
}

package org.hyperledger.identus.apollo.hashing

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.convert
import kotlinx.cinterop.usePinned
import platform.CoreCrypto.CCKeyDerivationPBKDF
import platform.CoreCrypto.kCCPBKDF2
import platform.CoreCrypto.kCCPRFHmacAlgSHA512

/**
 * The PBKDF2SHA512 class provides a platform-specific implementation for
 * deriving a key from a password using the PBKDF2 algorithm with SHA-512
 * hash function.
 */
actual object PBKDF2SHA512 {
    /**
     * Derives a key from a password using the PBKDF2 algorithm with SHA-512 hash function.
     *
     * @param p The password used for key derivation.
     * @param s The salt value.
     * @param c The iteration count.
     * @param dkLen The desired length of the derived key in bytes.
     * @return The derived key as a ByteArray.
     */
    @OptIn(ExperimentalUnsignedTypes::class, ExperimentalForeignApi::class)
    actual fun derive(
        p: String,
        s: String,
        c: Int,
        dkLen: Int
    ): ByteArray {
        val rounds = c.toUInt()
        val salt = s.encodeToByteArray().toUByteArray()
        val derivedKey = UByteArray(dkLen) // initialize buffer for derived key

        derivedKey.usePinned { derivedKeyPin ->
            salt.usePinned { saltPin ->

                val result =
                    CCKeyDerivationPBKDF(
                        algorithm = kCCPBKDF2,
                        password = p,
                        passwordLen = p.length.convert(),
                        salt = if (saltPin.get().isNotEmpty()) saltPin.addressOf(0) else null,
                        saltLen = saltPin.get().size.convert(),
                        prf = kCCPRFHmacAlgSHA512,
                        rounds = rounds,
                        derivedKey = derivedKeyPin.addressOf(0),
                        derivedKeyLen = derivedKeyPin.get().size.convert()
                    )

                if (result != 0) {
                    throw Error("Key derivation failed with error: $result")
                }
            }
        }

        return derivedKey.toByteArray()
    }
}

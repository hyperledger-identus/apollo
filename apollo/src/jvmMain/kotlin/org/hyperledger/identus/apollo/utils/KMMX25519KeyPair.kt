package org.hyperledger.identus.apollo.utils

import org.bouncycastle.crypto.generators.X25519KeyPairGenerator
import org.bouncycastle.crypto.params.X25519KeyGenerationParameters
import org.bouncycastle.crypto.params.X25519PrivateKeyParameters
import org.bouncycastle.crypto.params.X25519PublicKeyParameters
import java.security.SecureRandom
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
    actual companion object : X25519KeyPairGeneration {
        /**
         * Generates a new key pair for the X25519 elliptic curve encryption algorithm.
         *
         * @return the generated key pair as a [KMMX25519KeyPair] object.
         */
        actual override fun generateKeyPair(): KMMX25519KeyPair {
            val generator = X25519KeyPairGenerator()
            generator.init(X25519KeyGenerationParameters(SecureRandom()))
            val keyPair = generator.generateKeyPair()

            return KMMX25519KeyPair(
                KMMX25519PrivateKey((keyPair.private as X25519PrivateKeyParameters).encoded),
                KMMX25519PublicKey((keyPair.public as X25519PublicKeyParameters).encoded)
            )
        }
    }
}

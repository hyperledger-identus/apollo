package org.hyperledger.identus.apollo.secp256k1

import fr.acinq.secp256k1.Secp256k1
import fr.acinq.secp256k1.Secp256k1Native
import org.kotlincrypto.hash.sha2.SHA256

/**
 * This class provides various Secp256k1 cryptographic functionalities such as creating public keys, signing data,
 * verifying signatures, and compressing or decompressing public keys.
 */
actual class Secp256k1Lib {
    /**
     * Creates a public key from a given private key.
     *
     * @param privateKey The private key in byte array format.
     * @param compressed A boolean indicating whether the public key should be compressed.
     * @return A byte array representing the public key.
     */
    actual fun createPublicKey(privateKey: ByteArray, compressed: Boolean): ByteArray {
        val pubKey = Secp256k1Native.pubkeyCreate(privateKey)
        if (Secp256k1Helper.validatePublicKey(pubKey)) {
            if (compressed) {
                return Secp256k1.pubKeyCompress(pubKey)
            }
            return pubKey
        } else {
            throw Secp256k1Exception("invalid public key")
        }
    }

    /**
     * Derives a new private key from an existing private key and derived bytes.
     *
     * @param privateKeyBytes The original private key in byte array format.
     * @param derivedPrivateKeyBytes The byte array used for deriving the new private key.
     * @return A byte array representing the derived private key, or null if derivation fails.
     */
    actual fun derivePrivateKey(
        privateKeyBytes: ByteArray,
        derivedPrivateKeyBytes: ByteArray
    ): ByteArray? {
        return Secp256k1Native.privKeyTweakAdd(privateKeyBytes, derivedPrivateKeyBytes)
    }

    /**
     * Signs data using a given private key.
     *
     * @param privateKey The private key used for signing, in byte array format.
     * @param data The data to be signed, in byte array format.
     * @return A byte array representing the signature.
     */
    actual fun sign(privateKey: ByteArray, data: ByteArray): ByteArray {
        val sha = SHA256().digest(data)
        val compactSign = Secp256k1Native.sign(sha, privateKey)
        return Secp256k1Native.compact2der(compactSign)
    }

    /**
     * Verifies a signature against a public key and data.
     *
     * @param publicKey The public key in byte array format.
     * @param signature The signature to be verified, in byte array format.
     * @param data The data against which the signature will be verified, in byte array format.
     * @return A boolean indicating whether the signature is valid.
     */
    actual fun verify(
        publicKey: ByteArray,
        signature: ByteArray,
        data: ByteArray
    ): Boolean {
        val sha = SHA256().digest(data)
        if (Secp256k1Native.verify(signature, sha, publicKey)) {
            return true
        }
        val normalisedSignature = Secp256k1Native.signatureNormalize(signature).first
        if (Secp256k1Native.verify(normalisedSignature, sha, publicKey)) {
            return true
        }
        return Secp256k1Native.verify(transcodeSignatureToBitcoin(normalisedSignature), sha, publicKey)
    }

    /**
     * Transcodes a signature to format for use in Bitcoin transactions.
     *
     * @param signature The signature to be transcoded, in byte array format.
     * @return A byte array representing the signature in format.
     */
    private fun transcodeSignatureToBitcoin(signature: ByteArray): ByteArray {
        val rawLen = signature.size / 2
        val r = signature.copyOfRange(0, rawLen)
        val s = signature.copyOfRange(rawLen, signature.size)
        return r.reversedArray() + s.reversedArray()
    }

    /**
     * Decompresses a compressed public key.
     *
     * @param compressed The compressed public key in byte array format.
     * @return A byte array representing the uncompressed public key.
     */
    actual fun uncompressPublicKey(compressed: ByteArray): ByteArray {
        return Secp256k1Native.pubkeyParse(compressed)
    }

    /**
     * Compresses an uncompressed public key.
     *
     * @param uncompressed The uncompressed public key in byte array format.
     * @return A byte array representing the compressed public key.
     */
    actual fun compressPublicKey(uncompressed: ByteArray): ByteArray {
        return Secp256k1Native.pubKeyCompress(uncompressed)
    }
}

package org.hyperledger.identus.apollo.secp256k1

import com.ionspin.kotlin.bignum.integer.BigInteger
import org.hyperledger.identus.apollo.utils.ECConfig
import org.hyperledger.identus.apollo.utils.asByteArray
import org.hyperledger.identus.apollo.utils.asUint8Array
import org.hyperledger.identus.apollo.utils.external.BN
import org.hyperledger.identus.apollo.utils.external.ec
import org.hyperledger.identus.apollo.utils.external.secp256k1.SignatureType
import org.hyperledger.identus.apollo.utils.external.secp256k1.secp256k1
import org.hyperledger.identus.apollo.utils.toHexString
import org.kotlincrypto.hash.sha2.SHA256

/**
 * This class provides utility methods for working with a specific implementation of the secp256k1 elliptic curve cryptography.
 * It supports generating public keys, deriving private keys, signing data, verifying signatures, and compressing/uncompressing public keys.
 */
actual class Secp256k1Lib actual constructor() {
    /**
     * Creates a public key from a given private key.
     *
     * @param privateKey The private key in byte array format.
     * @param compressed A boolean indicating whether the public key should be compressed.
     * @return A byte array representing the public key.
     */
    actual fun createPublicKey(privateKey: ByteArray, compressed: Boolean): ByteArray {
        return secp256k1.getPublicKey(privateKey.asUint8Array(), compressed).asByteArray()
    }

    /**
     * Derives a new private key from an existing private key and derived bytes.
     *
     * @param privateKeyBytes The original private key in byte array format.
     * @param derivedPrivateKeyBytes The byte array used for deriving the new private key.
     * @return A byte array representing the derived private key, or null if derivation fails.
     */
    actual fun derivePrivateKey(privateKeyBytes: ByteArray, derivedPrivateKeyBytes: ByteArray): ByteArray? {
        val privKeyString = privateKeyBytes.toHexString()
        val derivedPrivKeyString = derivedPrivateKeyBytes.toHexString()
        val privKey = BigInteger.parseString(privKeyString, 16)
        val derivedPrivKey = BigInteger.parseString(derivedPrivKeyString, 16)
        val added = (privKey + derivedPrivKey) % ECConfig.n
        return added.toByteArray()
    }

    /**
     * Signs the given data using the provided private key.
     *
     * @param privateKey The private key used for signing the data.
     * @param data The data to be signed.
     * @return The signature of the data in DER format.
     */
    actual fun sign(privateKey: ByteArray, data: ByteArray): ByteArray {
        val sha = SHA256().digest(data)
        val signature =
            secp256k1.sign(
                sha.asUint8Array(),
                privateKey.asUint8Array(),
                {}
            )
        return signature.toDERRawBytes().asByteArray()
    }

    private fun normalise(signatureBytes: ByteArray): SignatureType {
        val jsSignatureByteArray = signatureBytes.asUint8Array()
        val signature =
            try {
                secp256k1.Signature.fromDER(jsSignatureByteArray)
            } catch (e: dynamic) {
                secp256k1.Signature.fromCompact(jsSignatureByteArray)
            }
        return if (signature.hasHighS()) {
            signature.normalizeS()
        } else {
            signature
        }
    }

    /**
     * Verifies the signature of the given data using the provided public key.
     *
     * @param publicKey The public key used for verification.
     * @param signature The signature to be verified.
     * @param data The data to be verified.
     * @return true if the signature is valid, false otherwise.
     */
    actual fun verify(
        publicKey: ByteArray,
        signature: ByteArray,
        data: ByteArray
    ): Boolean {
        val normalised = this.normalise(signature)
        val sha = SHA256().digest(data)
        if (secp256k1.verify(normalised, sha.asUint8Array(), publicKey.asUint8Array(), {})) {
            return true
        }
        return try {
            val transcoded = transcodeSignatureToBitcoin(normalised.toCompactRawBytes().asByteArray())
            secp256k1.verify(transcoded, sha.asUint8Array(), publicKey.asUint8Array(), {})
        } catch (e: dynamic) {
            secp256k1.verify(normalised, sha.asUint8Array(), publicKey.asUint8Array(), {})
        }
    }

    private fun transcodeSignatureToBitcoin(signature: ByteArray): SignatureType {
        val rawLen = signature.size / 2
        val r = signature.copyOfRange(0, rawLen).reversedArray()
        val s = signature.copyOfRange(rawLen, signature.size).reversedArray()
        val lenR = r.size
        val lenS = s.size
        val derLength = 6 + lenR + lenS
        val derSignature = ByteArray(derLength)
        derSignature[0] = 0x30
        derSignature[1] = (4 + lenR + lenS).toByte()
        derSignature[2] = 0x02
        derSignature[3] = lenR.toByte()
        derSignature.fill(r, 4, lenR + 4)
        derSignature[4 + lenR] = 0x02
        derSignature[5 + lenR] = lenS.toByte()
        derSignature.fill(s, 6 + lenR, lenS + 6 + lenR)
        return secp256k1.Signature.fromDER(derSignature.asUint8Array())
    }

    private fun ByteArray.fill(from: ByteArray, start: Int, end: Int) {
        for (i in start until end) {
            this[i] = from[i - start]
        }
    }

    /**
     * Converts a compressed public key to an uncompressed public key.
     *
     * @param compressed The compressed public key as a byte array.
     * @return The uncompressed public key as a byte array.
     */
    @OptIn(ExperimentalUnsignedTypes::class)
    actual fun uncompressPublicKey(compressed: ByteArray): ByteArray {
        val ecjs = ec("secp256k1")
        val decoded = ecjs.curve.decodePoint(compressed.asUint8Array())
        val x = ByteArray(decoded.getX().toArray().size) { index -> decoded.getX().toArray()[index].asDynamic() as Byte }
        val y = ByteArray(decoded.getX().toArray().size) { index -> decoded.getY().toArray()[index].asDynamic() as Byte }
        val header: Byte = 0x04
        return byteArrayOf(header) + x + y
    }

    /**
     * Compresses an uncompressed public key.
     *
     * @param uncompressed The uncompressed public key to compress.
     * @return The compressed public key.
     */
    actual fun compressPublicKey(uncompressed: ByteArray): ByteArray {
        val ecjs = ec("secp256k1")
        val pubKeyBN = BN(ecjs.keyFromPublic(uncompressed.asUint8Array()).getPublic().encodeCompressed())
        return ByteArray(pubKeyBN.toArray().size) { index -> pubKeyBN.toArray()[index].asDynamic() as Byte }
    }
}

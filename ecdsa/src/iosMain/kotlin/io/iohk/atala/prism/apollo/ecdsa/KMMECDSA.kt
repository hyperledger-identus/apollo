package io.iohk.atala.prism.apollo.ecdsa

import io.iohk.atala.prism.apollo.hashing.SHA256
import io.iohk.atala.prism.apollo.hashing.SHA384
import io.iohk.atala.prism.apollo.hashing.SHA512
import io.iohk.atala.prism.apollo.secp256k1.ECDSA
import io.iohk.atala.prism.apollo.utils.KMMECPrivateKey
import io.iohk.atala.prism.apollo.utils.KMMECPublicKey

actual object KMMECDSA {
    @OptIn(ExperimentalUnsignedTypes::class)
    actual fun sign(
        type: ECDSAType,
        data: ByteArray,
        privateKey: KMMECPrivateKey
    ): ByteArray {
        val hashedData = when (type) {
            ECDSAType.ECDSA_SHA256 -> SHA256().digest(data)
            ECDSAType.ECDSA_SHA384 -> SHA384().digest(data)
            ECDSAType.ECDSA_SHA512 -> SHA512().digest(data)
        }
        val ecdsa = ECDSA()
        val compressedBytes = ecdsa.sign(hashedData, privateKey.nativeValue.toByteArray())
        return ecdsa.compact2der(compressedBytes)
    }

    @OptIn(ExperimentalUnsignedTypes::class)
    actual fun verify(
        type: ECDSAType,
        data: ByteArray,
        publicKey: KMMECPublicKey,
        signature: ByteArray
    ): Boolean {
        val hashedData = when (type) {
            ECDSAType.ECDSA_SHA256 -> SHA256().digest(data)
            ECDSAType.ECDSA_SHA384 -> SHA384().digest(data)
            ECDSAType.ECDSA_SHA512 -> SHA512().digest(data)
        }
        val ecdsa = ECDSA()
        return ecdsa.verify(signature, hashedData, publicKey.nativeValue.toByteArray())
    }
}

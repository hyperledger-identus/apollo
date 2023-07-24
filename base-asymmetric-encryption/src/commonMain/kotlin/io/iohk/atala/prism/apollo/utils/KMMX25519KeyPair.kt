package io.iohk.atala.prism.apollo.utils

expect class KMMX25519KeyPair(privateKey: KMMX25519PrivateKey, publicKey: KMMX25519PublicKey) {
    val privateKey: KMMX25519PrivateKey
    val publicKey: KMMX25519PublicKey

    companion object : X25519KeyPairGeneration
}

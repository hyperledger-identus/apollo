package org.hyperledger.identus.apollo.derivation

import uniffi.ed25519_bip32_wrapper.deriveBytesPub
import kotlin.test.Test

class Ed25519Test {
    @Test
    fun test_if_linking_is_correct() {
        val publicKey = "6fd8d9c696b01525cc45f15583fc9447c66e1c71fd1a11c8885368404cd0a4ab".decodeHex()
        val chainCode = "00b5f1652f5cbe257e567c883dc2b16e0a9568b19c5b81ea8bd197fc95e8bdcf".decodeHex()
        deriveBytesPub(publicKey, chainCode, 0.toUInt())
    }
}

fun String.decodeHex(): ByteArray {
    check(length % 2 == 0) { "Must have an even length" }

    return chunked(2)
        .map { it.toInt(16).toByte() }
        .toByteArray()
}

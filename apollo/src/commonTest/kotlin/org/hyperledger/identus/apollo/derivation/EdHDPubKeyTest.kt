package org.hyperledger.identus.apollo.derivation

import org.hyperledger.identus.apollo.Platform
import org.hyperledger.identus.apollo.utils.decodeHex
import org.hyperledger.identus.apollo.utils.toHexString
import kotlin.test.Test
import kotlin.test.assertEquals

class EdHDPubKeyTest {
    @Test
    fun test_derive_m_1852_1815_0() {
        if (!Platform.OS.contains("Android")) {
            val publicKey = "6fd8d9c696b01525cc45f15583fc9447c66e1c71fd1a11c8885368404cd0a4ab".decodeHex()
            val chainCode = "00b5f1652f5cbe257e567c883dc2b16e0a9568b19c5b81ea8bd197fc95e8bdcf".decodeHex()

            val key = EdHDPubKey(publicKey, chainCode)
            val derivationPath = listOf(1852, 1815, 0)
            val pathString = derivationPath.joinToString(separator = "/", prefix = "m/") { "$it" }
            val derived = key.derive(pathString)

            assertEquals(
                derived.publicKey.toHexString(),
                "b857a8cd1dbbfed1824359d9d9e58bc8ffb9f66812b404f4c6ffc315629835bf"
            )
            assertEquals(derived.chainCode.toHexString(), "9db12d11a3559131a47f51f854a6234725ab8767d3fcc4c9908be55508f3c712")
        }
    }
}

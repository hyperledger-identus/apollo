package org.hyperledger.identus.apollo.derivation

import org.hyperledger.identus.apollo.utils.decodeHex
import org.hyperledger.identus.apollo.utils.toHexString
import kotlin.test.Test
import kotlin.test.assertEquals

@RunWith(AndroidJUnit4::class)
class EdHDKeyTest {
    @Test
    fun test_derive_m1() {
        val privateKey = "f8a29231ee38d6c5bf715d5bac21c750577aa3798b22d79d65bf97d6fadea15adcd1ee1abdf78bd4be64731a12deb94d3671784112eb6f364b871851fd1c9a24".decodeHex()
        val chainCode = "7384db9ad6003bbd08b3b1ddc0d07a597293ff85e961bf252b331262eddfad0d".decodeHex()

        val key = EdHDKey(privateKey, chainCode)
        val derived = key.derive("m/1'")

        assertEquals(
            derived.privateKey.toHexString(),
            "4057eb6cab9000e3b6fe7e556341da1ca2f5dde0b689a7b58cb93f1902dfa15a5a10732ff348051c6e0865c62931d4a73fa8050b8ff543b43fc0000a7e2c5700"
        )
        assertEquals(
            derived.chainCode.toHexString(),
            "9a170f689c8b9b3502ee846f457ab3dd1b017cfb2cd68865c7f24dbabcbc2256"
        )
    }
}

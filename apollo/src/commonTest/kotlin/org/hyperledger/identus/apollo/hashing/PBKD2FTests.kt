package org.hyperledger.identus.apollo.hashing

import org.hyperledger.identus.apollo.utils.toHexString
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals

class PBKD2FTests {
    @Test
    fun test_random_mnemonic_PBKDF2SHA512_withPW() {
        val key = "random seed mnemonic words"
        val password = "123456"
        val c = 2048
        val dkLen = 64
        val seed = PBKDF2SHA512.derive(key, password, c, dkLen)
        assertEquals(seed.size, 64)

        val privateKey = seed.slice(IntRange(0, 31))
        assertContains(privateKey.toByteArray().toHexString(), "b3a8af66eca002e8b4ca868c5b55a8c865f15e0cfea483d6a164a6fbecf83625")
    }
}

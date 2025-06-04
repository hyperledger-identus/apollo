package org.hyperledger.identus.apollo

import kotlin.test.Test
import kotlin.test.assertTrue

class PlatformTest {
    @Test
    fun testPlatformName() {
        assertTrue(org.hyperledger.identus.apollo.Platform.OS.contains("iOS"), "Check iOS is mentioned")
    }
}

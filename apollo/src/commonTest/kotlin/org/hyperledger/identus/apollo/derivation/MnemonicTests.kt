package org.hyperledger.identus.apollo.derivation

import kotlin.test.Test
import kotlin.test.fail

class MnemonicTests {
    @Test
    fun testCreatingSeedWithEmptyPassphrase() {
        val words =
            listOf(
                "bicycle",
                "monster",
                "swap",
                "cave",
                "bulk",
                "fossil",
                "nominee",
                "crisp",
                "tail",
                "parent",
                "fossil",
                "eyebrow",
                "fold",
                "manage",
                "custom",
                "burst",
                "flight",
                "lawn",
                "survey",
                "snake",
                "brown",
                "bridge",
                "hard",
                "perfect"
            )
        val passphrase = ""

        assertDoesNotThrow {
            MnemonicHelper.createSeed(words, passphrase)
        }
    }
}

/**
 * Asserts that the given executable does not throw an exception.
 *
 * @param block The executable function to be tested.
 * @throws AssertionError if the executable throws any exception.
 */
inline fun assertDoesNotThrow(block: () -> Unit) {
    try {
        block()
    } catch (e: Exception) {
        fail("Expected no exception to be thrown, but got ${e.stackTraceToString()}")
    }
}

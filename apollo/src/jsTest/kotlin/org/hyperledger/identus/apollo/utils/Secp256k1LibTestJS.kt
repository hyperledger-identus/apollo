package org.hyperledger.identus.apollo.utils

import org.hyperledger.identus.apollo.derivation.Mnemonic
import org.hyperledger.identus.apollo.secp256k1.Secp256k1Lib
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals

class Secp256k1LibTestJS {
    @Test
    fun testCreateApolloSignatureAndVerify() {
        val mnemonics = Mnemonic.Companion.createRandomMnemonics()
        val seed = Mnemonic.Companion.createSeed(mnemonics)
        val secret = seed.slice(0..31).toTypedArray()
        val sk = KMMECSecp256k1PrivateKey.Companion.secp256k1FromByteArray(secret.toByteArray())
        val pk = sk.getPublicKey()
        val data = "Data 0002".encodeToByteArray()
        val signature = sk.sign(data)
        val verified = pk.verify(signature, data)
        assertEquals(
            verified,
            true
        )
    }

    @Test
    fun derivePrivateKey_alwaysReturns32Bytes() {
        val lib = Secp256k1Lib()
        val rnd = Random(42)
        repeat(2000) { i ->
            val a = ByteArray(32) { rnd.nextInt(256).toByte() }
            val b = ByteArray(32) { rnd.nextInt(256).toByte() }
            val r = lib.derivePrivateKey(a, b)
            assertEquals(32, r!!.size, "iteration $i")
        }
    }
}

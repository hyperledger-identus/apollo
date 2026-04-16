package org.hyperledger.identus.apollo.utils

import org.hyperledger.identus.apollo.derivation.Mnemonic
import org.hyperledger.identus.apollo.secp256k1.Secp256k1Lib
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

    /**
     * Regression: JS [derivePrivateKey] used ionspin [BigInteger.toByteArray], which is minimal-length.
     * For (priv + tweak) % n == 1, that is a single non-zero byte — callers expect a fixed 32-byte secret.
     * (This is unrelated to public-key 0x02/0x03/0x04 prefix bytes; those apply to encoded public points, not private scalars.)
     */
    @Test
    fun derivePrivateKey_leftPadsMinimalScalarTo32Bytes() {
        val lib = Secp256k1Lib()
        val priv = ByteArray(32) { 0 }.also { it[31] = 1 }
        val tweak = ByteArray(32) { 0 }
        val r = lib.derivePrivateKey(priv, tweak)!!
        assertEquals(32, r.size)
        assertEquals(0, r[0].toInt() and 0xff)
        assertEquals(1, r[31].toInt() and 0xff)
    }
}

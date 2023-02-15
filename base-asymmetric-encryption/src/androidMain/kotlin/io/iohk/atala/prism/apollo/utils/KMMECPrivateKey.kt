package io.iohk.atala.prism.apollo.utils

import com.ionspin.kotlin.bignum.integer.BigInteger
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey
import org.bouncycastle.jce.ECNamedCurveTable
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.jce.spec.ECNamedCurveSpec
import java.security.KeyFactory
import java.security.spec.ECParameterSpec
import java.security.spec.ECPrivateKeySpec

actual class KMMECPrivateKey(val nativeValue: BCECPrivateKey) : KMMECPrivateKeyCommon(privateKeyD(nativeValue)) {
    companion object {
        fun secp256k1FromBigInteger(d: BigInteger): KMMECPrivateKey {
            val ecParameterSpec = ECNamedCurveTable.getParameterSpec(KMMEllipticCurve.SECP256k1.value)
            val ecNamedCurveSpec: ECParameterSpec = ECNamedCurveSpec(
                ecParameterSpec.name,
                ecParameterSpec.curve,
                ecParameterSpec.g,
                ecParameterSpec.n
            )
            val provider = BouncyCastleProvider()
            val keyFactory = KeyFactory.getInstance("EC", provider)
            val spec = ECPrivateKeySpec(d.toJavaBigInteger(), ecNamedCurveSpec)
            return KMMECPrivateKey(keyFactory.generatePrivate(spec) as BCECPrivateKey)
        }

        private fun privateKeyD(privateKey: BCECPrivateKey): BigInteger {
            return privateKey.d.toKotlinBigInteger()
        }
    }
}

package io.iohk.prism.hashing

import io.iohk.prism.hashing.internal.HMACInterface
import io.iohk.prism.hashing.internal.KeccakDigest

/**
 * This class implements the SHA3-512 digest algorithm under the [KeccakDigest] API.
 * SHA3-512 is defined by FIPS PUB 202.
 */
final class SHA3_512: KeccakDigest(0x06), HMACInterface {
    override val digestLength: Int
        get() = 64

    override fun toString() = "SHA3-512"
}

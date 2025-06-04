package org.hyperledger.identus.apollo.utils

import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

/**
 * Interface defining the functionality for generating Ed25519 key pairs.
 */
@ExperimentalJsExport
@JsExport
interface Ed25519KeyPairGeneration {
    /**
     * Generates an Ed25519 key pair.
     *
     * @return A [KMMEdKeyPair] instance representing the generated public and private keys.
     */
    fun generateKeyPair(): KMMEdKeyPair
}

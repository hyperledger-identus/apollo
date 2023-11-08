// Automatically generated by dukat and then slightly adjusted manually to make it compile
@file:Suppress("ktlint", "internal:ktlint-suppression")
@file:JsModule("bip32")

package io.iohk.atala.prism.apollo.utils.external

import node.buffer.Buffer
import kotlin.js.*

internal external interface Bip32KeyPair {
    var public: Number
    var private: Number
}

internal external interface Network {
    var wif: Number
    var bip32: Bip32KeyPair
    var messagePrefix: String?
        get() = definedExternally
        set(value) = definedExternally
    var bech32: String?
        get() = definedExternally
        set(value) = definedExternally
    var pubKeyHash: Number?
        get() = definedExternally
        set(value) = definedExternally
    var scriptHash: Number?
        get() = definedExternally
        set(value) = definedExternally
}

internal external interface BIP32Interface {
    var chainCode: Buffer
    var network: Network
    var lowR: Boolean
    var depth: Number
    var index: Number
    var parentFingerprint: Number
    var publicKey: Buffer
    var privateKey: Buffer?
        get() = definedExternally
        set(value) = definedExternally
    var identifier: Buffer
    var fingerprint: Buffer
    fun isNeutered(): Boolean
    fun neutered(): BIP32Interface
    fun toBase58(): String
    fun toWIF(): String
    fun derive(index: Number): BIP32Interface
    fun deriveHardened(index: Number): BIP32Interface
    fun derivePath(path: String): BIP32Interface
    fun sign(hash: Buffer, lowR: Boolean = definedExternally): Buffer
    fun verify(hash: Buffer, signature: Buffer): Boolean
}

internal external fun fromBase58(inString: String, network: Network = definedExternally): BIP32Interface

internal external fun fromPrivateKey(privateKey: Buffer, chainCode: Buffer, network: Network = definedExternally): BIP32Interface

internal external fun fromPublicKey(publicKey: Buffer, chainCode: Buffer, network: Network = definedExternally): BIP32Interface

internal external fun fromSeed(seed: Buffer, network: Network = definedExternally): BIP32Interface

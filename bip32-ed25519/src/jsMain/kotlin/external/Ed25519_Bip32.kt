package org.hyperledger.identus.apollo.utils.external

external interface ed25519_bip32_export {
    fun from_nonextended(key: ByteArray, chain_code: ByteArray): Array<ByteArray>

    fun derive_bytes(key: ByteArray, chain_code: ByteArray, index: Any): Array<ByteArray>

    fun derive_bytes_pub(key: ByteArray, chain_code: ByteArray, index: Any): Array<ByteArray>
}

@JsModule("./ed25519_bip32_wasm.js")
@JsNonModule
external val ed25519_bip32: ed25519_bip32_export

// Automatically generated by dukat and then slightly adjusted manually to make it compile
@file:Suppress("ktlint", "internal:ktlint-suppression")
// @file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
@file:JsModule("@noble/curves/secp256k1")

package io.iohk.atala.prism.apollo.utils.external.secp256k1

import io.iohk.atala.prism.apollo.utils.external.BN
import kotlin.js.*
import org.khronos.webgl.*
import org.w3c.dom.*
import org.w3c.dom.events.*
import org.w3c.dom.parsing.*
import org.w3c.dom.svg.*
import org.w3c.dom.url.*
import org.w3c.fetch.*
import org.w3c.files.*
import org.w3c.notifications.*
import org.w3c.performance.*
import org.w3c.workers.*
import org.w3c.xhr.*
import org.khronos.webgl.Uint8Array

external interface `T$0` {
    var k1neg: Boolean
    var k1: Any
    var k2neg: Boolean
    var k2: Any
}

external interface `T$1` {
    var beta: Any
    var splitScalar: (k: Any) -> `T$0`
}


external interface `T$2` {
    var nBitLength: Number
    var nByteLength: Number
    var Fp: Any
    var n: Any
    var h: Any
    var hEff: Any?
        get() = definedExternally
        set(value) = definedExternally
    var Gx: Any
    var Gy: Any
    var allowInfinityPoint: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var a: Any
    var b: Any
    var allowedPrivateKeyLengths: Array<Number>?
        get() = definedExternally
        set(value) = definedExternally
    var wrapPrivateKey: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var endo: `T$1`?
        get() = definedExternally
        set(value) = definedExternally
    var hash: Any
    var hmac: (key: Uint8Array, messages: Uint8Array) -> Uint8Array
    var randomBytes: (bytesLength: Number?) -> Uint8Array
    var lowS: Boolean
    var bits2int: ((bytes: Uint8Array) -> Any)?
        get() = definedExternally
        set(value) = definedExternally
    var bits2int_modN: ((bytes: Uint8Array) -> Any)?
        get() = definedExternally
        set(value) = definedExternally
    var p: Any
}

external interface `T$4` {
    var normPrivateKeyToScalar: (key: dynamic /* Uint8Array | String | Any */) -> Any
    fun isValidPrivateKey(privateKey: Uint8Array): Boolean
    fun isValidPrivateKey(privateKey: String): Boolean
    fun isValidPrivateKey(privateKey: Any): Boolean
    var randomPrivateKey: () -> Uint8Array
}

external interface `T$5` {
    var create: (hash: Any) -> Any
    var CURVE:`T$2`
    var getPublicKey: (privateKey: dynamic /* Uint8Array | String | Any */, isCompressed: Boolean?) -> Uint8Array
    var getSharedSecret: (privateA: dynamic /* Uint8Array | String | Any */, publicB: dynamic /* Uint8Array | String */, isCompressed: Boolean?) -> Uint8Array
    var sign: (msgHash: dynamic /* Uint8Array | String */, privKey: dynamic /* Uint8Array | String | Any */, opts: Any?) -> Any
    var verify: (signature: dynamic /* Uint8Array | String | `T$3` */, msgHash: dynamic /* Uint8Array | String */, publicKey: dynamic /* Uint8Array | String */, opts: Any?) -> Boolean
    var ProjectivePoint: Any
    var Signature: Any
    var utils: `T$4`
}


external var secp256k1: `T$5`

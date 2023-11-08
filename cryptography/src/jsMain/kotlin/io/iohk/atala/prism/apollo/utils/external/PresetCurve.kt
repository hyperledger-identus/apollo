// Automatically generated by dukat and then slightly adjusted manually to make it compile
@file:Suppress("ktlint", "internal:ktlint-suppression")
// @file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
@file:JsQualifier("curves")
@file:JsModule("elliptic")

package io.iohk.atala.prism.apollo.utils.external

open external class PresetCurve(options: Options) {
    open var type: String
    open var g: Any
    open var n: Any?
    open var hash: Any
    interface Options {
        var type: String
        var prime: String?
        var p: String
        var a: String
        var b: String
        var n: String
        var hash: Any
        var gRed: Boolean
        var g: Any
        var beta: String?
            get() = definedExternally
            set(value) = definedExternally
        var lambda: String?
            get() = definedExternally
            set(value) = definedExternally
        var basis: Any?
            get() = definedExternally
            set(value) = definedExternally
    }
}

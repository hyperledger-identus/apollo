@file:Suppress("ktlint", "internal:ktlint-suppression")
@file:JsQualifier("curve")
@file:JsModule("elliptic")

package org.hyperledger.identus.apollo.utils.external

import org.khronos.webgl.Uint8Array

external interface BaseCurveOptions {
    var p: dynamic
    var prime: dynamic
    var n: dynamic
    var g: dynamic
    var gRed: dynamic
}

external interface EdwardsConf : BaseCurveOptions {
    var a: dynamic
    var c: dynamic
    var d: dynamic
}

external interface ShortConf : BaseCurveOptions {
    var a: dynamic
    var b: dynamic
    var beta: dynamic
    var lambda: dynamic
}

open external class base {
    var p: dynamic
    var type: String
    var red: dynamic
    var zero: dynamic
    var one: dynamic
    var two: dynamic
    var n: dynamic
    var g: BasePoint
    var redN: dynamic
    fun validate(point: BasePoint): Boolean
    fun decodePoint(bytes: String, enc: String = definedExternally): BasePoint

    open class BasePoint {
        var curve: base
        var type: String
        var precomputed: dynamic
        fun encode(enc: String, compact: Boolean): dynamic
        fun encodeCompressed(enc: String): dynamic
        fun encodeCompressed(): Array<Number>
        fun validate(): Boolean
        fun precompute(power: Number): BasePoint
        fun dblp(k: Number): BasePoint
        fun inspect(): String
        fun isInfinity(): Boolean
        fun add(p: BasePoint): BasePoint
        fun mul(k: dynamic): BasePoint
        fun dbl(): BasePoint
        fun getX(): dynamic
        fun getY(): dynamic
        fun eq(p: BasePoint): Boolean
        fun neg(): BasePoint
    }
}

open external class edwards {
    var a: dynamic
    var c: dynamic
    var c2: dynamic
    var d: dynamic
    var dd: dynamic

    fun point(x: dynamic, y: dynamic, z: dynamic = definedExternally, t: dynamic = definedExternally): EdwardsPoint
    fun pointFromX(x: dynamic, odd: Boolean = definedExternally): EdwardsPoint
    fun pointFromY(y: dynamic, odd: Boolean = definedExternally): EdwardsPoint
    fun pointFromJSON(obj: dynamic): EdwardsPoint

    open class EdwardsPoint : base.BasePoint {
        var x: dynamic
        var y: dynamic
        var z: dynamic
        var t: dynamic
        fun normalize(): EdwardsPoint
        fun eqXToP(x: dynamic): Boolean
    }
}

open external class short {
    var a: dynamic
    var b: dynamic
    var g: base.BasePoint

    fun point(x: dynamic, y: dynamic, isRed: Boolean = definedExternally): ShortPoint
    fun pointFromX(x: dynamic, odd: Boolean = definedExternally): ShortPoint
    fun pointFromJSON(obj: dynamic, red: Boolean): ShortPoint

    open class ShortPoint : base.BasePoint {
        var x: dynamic
        var y: dynamic
        var inf: Boolean
        fun toJSON(): Array<dynamic>
    }
}

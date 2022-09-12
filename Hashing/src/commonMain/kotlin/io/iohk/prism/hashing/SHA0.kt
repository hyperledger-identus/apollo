package io.iohk.prism.hashing

import io.iohk.prism.hashing.internal.HMACInterface
import io.iohk.prism.hashing.internal.MathHelper
import io.iohk.prism.hashing.internal.MDHelper

/**
 * This class implements the SHA-0 digest algorithm under the [MDHelper] API.
 * SHA-0 was defined by FIPS 180.
 */
final class SHA0: MDHelper(false, 8), HMACInterface {
    private lateinit var currentVal: IntArray
    override val digestLength: Int
        get() = 20
    override val blockLength: Int
        get() = 64

    override fun doInit() {
        currentVal = IntArray(5)
        engineReset()
    }

    override fun engineReset() {
        currentVal[0] = 0x67452301
        currentVal[1] = -0x10325477
        currentVal[2] = -0x67452302
        currentVal[3] = 0x10325476
        currentVal[4] = -0x3c2d1e10
    }

    override fun doPadding(output: ByteArray, outputOffset: Int) {
        makeMDPadding()
        for (i in 0..4) MathHelper.encodeBEInt(
            currentVal[i],
            output, outputOffset + 4 * i
        )
    }

    override fun processBlock(data: ByteArray) {
        var a = currentVal[0]
        var b = currentVal[1]
        var c = currentVal[2]
        var d = currentVal[3]
        var e = currentVal[4]
        var w0 = MathHelper.decodeBEInt(data, 0)
        e = ((a shl 5 or (a ushr 27)) + (b and c or (b.inv() and d)) +
                e + w0 + 0x5A827999)
        b = b shl 30 or (b ushr 2)
        var w1 = MathHelper.decodeBEInt(data, 4)
        d = ((e shl 5 or (e ushr 27)) + (a and b or (a.inv() and c)) +
                d + w1 + 0x5A827999)
        a = a shl 30 or (a ushr 2)
        var w2 = MathHelper.decodeBEInt(data, 8)
        c = ((d shl 5 or (d ushr 27)) + (e and a or (e.inv() and b)) +
                c + w2 + 0x5A827999)
        e = e shl 30 or (e ushr 2)
        var w3 = MathHelper.decodeBEInt(data, 12)
        b = ((c shl 5 or (c ushr 27)) + (d and e or (d.inv() and a)) +
                b + w3 + 0x5A827999)
        d = d shl 30 or (d ushr 2)
        var w4 = MathHelper.decodeBEInt(data, 16)
        a = ((b shl 5 or (b ushr 27)) + (c and d or (c.inv() and e)) +
                a + w4 + 0x5A827999)
        c = c shl 30 or (c ushr 2)
        var w5 = MathHelper.decodeBEInt(data, 20)
        e = ((a shl 5 or (a ushr 27)) + (b and c or (b.inv() and d)) +
                e + w5 + 0x5A827999)
        b = b shl 30 or (b ushr 2)
        var w6 = MathHelper.decodeBEInt(data, 24)
        d = ((e shl 5 or (e ushr 27)) + (a and b or (a.inv() and c)) +
                d + w6 + 0x5A827999)
        a = a shl 30 or (a ushr 2)
        var w7 = MathHelper.decodeBEInt(data, 28)
        c = ((d shl 5 or (d ushr 27)) + (e and a or (e.inv() and b)) +
                c + w7 + 0x5A827999)
        e = e shl 30 or (e ushr 2)
        var w8 = MathHelper.decodeBEInt(data, 32)
        b = ((c shl 5 or (c ushr 27)) + (d and e or (d.inv() and a)) +
                b + w8 + 0x5A827999)
        d = d shl 30 or (d ushr 2)
        var w9 = MathHelper.decodeBEInt(data, 36)
        a = ((b shl 5 or (b ushr 27)) + (c and d or (c.inv() and e)) +
                a + w9 + 0x5A827999)
        c = c shl 30 or (c ushr 2)
        var wa = MathHelper.decodeBEInt(data, 40)
        e = ((a shl 5 or (a ushr 27)) + (b and c or (b.inv() and d)) +
                e + wa + 0x5A827999)
        b = b shl 30 or (b ushr 2)
        var wb = MathHelper.decodeBEInt(data, 44)
        d = ((e shl 5 or (e ushr 27)) + (a and b or (a.inv() and c)) +
                d + wb + 0x5A827999)
        a = a shl 30 or (a ushr 2)
        var wc = MathHelper.decodeBEInt(data, 48)
        c = ((d shl 5 or (d ushr 27)) + (e and a or (e.inv() and b)) +
                c + wc + 0x5A827999)
        e = e shl 30 or (e ushr 2)
        var wd = MathHelper.decodeBEInt(data, 52)
        b = ((c shl 5 or (c ushr 27)) + (d and e or (d.inv() and a)) +
                b + wd + 0x5A827999)
        d = d shl 30 or (d ushr 2)
        var we = MathHelper.decodeBEInt(data, 56)
        a = ((b shl 5 or (b ushr 27)) + (c and d or (c.inv() and e)) +
                a + we + 0x5A827999)
        c = c shl 30 or (c ushr 2)
        var wf = MathHelper.decodeBEInt(data, 60)
        e = ((a shl 5 or (a ushr 27)) + (b and c or (b.inv() and d)) +
                e + wf + 0x5A827999)
        b = b shl 30 or (b ushr 2)
        w0 = wd xor w8 xor w2 xor w0
        d = ((e shl 5 or (e ushr 27)) + (a and b or (a.inv() and c)) +
                d + w0 + 0x5A827999)
        a = a shl 30 or (a ushr 2)
        w1 = we xor w9 xor w3 xor w1
        c = ((d shl 5 or (d ushr 27)) + (e and a or (e.inv() and b)) +
                c + w1 + 0x5A827999)
        e = e shl 30 or (e ushr 2)
        w2 = wf xor wa xor w4 xor w2
        b = ((c shl 5 or (c ushr 27)) + (d and e or (d.inv() and a)) +
                b + w2 + 0x5A827999)
        d = d shl 30 or (d ushr 2)
        w3 = w0 xor wb xor w5 xor w3
        a = ((b shl 5 or (b ushr 27)) + (c and d or (c.inv() and e)) +
                a + w3 + 0x5A827999)
        c = c shl 30 or (c ushr 2)
        w4 = w1 xor wc xor w6 xor w4
        e = ((a shl 5 or (a ushr 27)) + (b xor c xor d) +
                e + w4 + 0x6ED9EBA1)
        b = b shl 30 or (b ushr 2)
        w5 = w2 xor wd xor w7 xor w5
        d = ((e shl 5 or (e ushr 27)) + (a xor b xor c) +
                d + w5 + 0x6ED9EBA1)
        a = a shl 30 or (a ushr 2)
        w6 = w3 xor we xor w8 xor w6
        c = ((d shl 5 or (d ushr 27)) + (e xor a xor b) +
                c + w6 + 0x6ED9EBA1)
        e = e shl 30 or (e ushr 2)
        w7 = w4 xor wf xor w9 xor w7
        b = ((c shl 5 or (c ushr 27)) + (d xor e xor a) +
                b + w7 + 0x6ED9EBA1)
        d = d shl 30 or (d ushr 2)
        w8 = w5 xor w0 xor wa xor w8
        a = ((b shl 5 or (b ushr 27)) + (c xor d xor e) +
                a + w8 + 0x6ED9EBA1)
        c = c shl 30 or (c ushr 2)
        w9 = w6 xor w1 xor wb xor w9
        e = ((a shl 5 or (a ushr 27)) + (b xor c xor d) +
                e + w9 + 0x6ED9EBA1)
        b = b shl 30 or (b ushr 2)
        wa = w7 xor w2 xor wc xor wa
        d = ((e shl 5 or (e ushr 27)) + (a xor b xor c) +
                d + wa + 0x6ED9EBA1)
        a = a shl 30 or (a ushr 2)
        wb = w8 xor w3 xor wd xor wb
        c = ((d shl 5 or (d ushr 27)) + (e xor a xor b) +
                c + wb + 0x6ED9EBA1)
        e = e shl 30 or (e ushr 2)
        wc = w9 xor w4 xor we xor wc
        b = ((c shl 5 or (c ushr 27)) + (d xor e xor a) +
                b + wc + 0x6ED9EBA1)
        d = d shl 30 or (d ushr 2)
        wd = wa xor w5 xor wf xor wd
        a = ((b shl 5 or (b ushr 27)) + (c xor d xor e) +
                a + wd + 0x6ED9EBA1)
        c = c shl 30 or (c ushr 2)
        we = wb xor w6 xor w0 xor we
        e = ((a shl 5 or (a ushr 27)) + (b xor c xor d) +
                e + we + 0x6ED9EBA1)
        b = b shl 30 or (b ushr 2)
        wf = wc xor w7 xor w1 xor wf
        d = ((e shl 5 or (e ushr 27)) + (a xor b xor c) +
                d + wf + 0x6ED9EBA1)
        a = a shl 30 or (a ushr 2)
        w0 = wd xor w8 xor w2 xor w0
        c = ((d shl 5 or (d ushr 27)) + (e xor a xor b) +
                c + w0 + 0x6ED9EBA1)
        e = e shl 30 or (e ushr 2)
        w1 = we xor w9 xor w3 xor w1
        b = ((c shl 5 or (c ushr 27)) + (d xor e xor a) +
                b + w1 + 0x6ED9EBA1)
        d = d shl 30 or (d ushr 2)
        w2 = wf xor wa xor w4 xor w2
        a = ((b shl 5 or (b ushr 27)) + (c xor d xor e) +
                a + w2 + 0x6ED9EBA1)
        c = c shl 30 or (c ushr 2)
        w3 = w0 xor wb xor w5 xor w3
        e = ((a shl 5 or (a ushr 27)) + (b xor c xor d) +
                e + w3 + 0x6ED9EBA1)
        b = b shl 30 or (b ushr 2)
        w4 = w1 xor wc xor w6 xor w4
        d = ((e shl 5 or (e ushr 27)) + (a xor b xor c) +
                d + w4 + 0x6ED9EBA1)
        a = a shl 30 or (a ushr 2)
        w5 = w2 xor wd xor w7 xor w5
        c = ((d shl 5 or (d ushr 27)) + (e xor a xor b) +
                c + w5 + 0x6ED9EBA1)
        e = e shl 30 or (e ushr 2)
        w6 = w3 xor we xor w8 xor w6
        b = ((c shl 5 or (c ushr 27)) + (d xor e xor a) +
                b + w6 + 0x6ED9EBA1)
        d = d shl 30 or (d ushr 2)
        w7 = w4 xor wf xor w9 xor w7
        a = ((b shl 5 or (b ushr 27)) + (c xor d xor e) +
                a + w7 + 0x6ED9EBA1)
        c = c shl 30 or (c ushr 2)
        w8 = w5 xor w0 xor wa xor w8
        e = ((a shl 5 or (a ushr 27)) + (b and c or (b and d) or (c and d)) +
                e + w8 + -0x70e44324)
        b = b shl 30 or (b ushr 2)
        w9 = w6 xor w1 xor wb xor w9
        d = ((e shl 5 or (e ushr 27)) + (a and b or (a and c) or (b and c)) +
                d + w9 + -0x70e44324)
        a = a shl 30 or (a ushr 2)
        wa = w7 xor w2 xor wc xor wa
        c = ((d shl 5 or (d ushr 27)) + (e and a or (e and b) or (a and b)) +
                c + wa + -0x70e44324)
        e = e shl 30 or (e ushr 2)
        wb = w8 xor w3 xor wd xor wb
        b = ((c shl 5 or (c ushr 27)) + (d and e or (d and a) or (e and a)) +
                b + wb + -0x70e44324)
        d = d shl 30 or (d ushr 2)
        wc = w9 xor w4 xor we xor wc
        a = ((b shl 5 or (b ushr 27)) + (c and d or (c and e) or (d and e)) +
                a + wc + -0x70e44324)
        c = c shl 30 or (c ushr 2)
        wd = wa xor w5 xor wf xor wd
        e = ((a shl 5 or (a ushr 27)) + (b and c or (b and d) or (c and d)) +
                e + wd + -0x70e44324)
        b = b shl 30 or (b ushr 2)
        we = wb xor w6 xor w0 xor we
        d = ((e shl 5 or (e ushr 27)) + (a and b or (a and c) or (b and c)) +
                d + we + -0x70e44324)
        a = a shl 30 or (a ushr 2)
        wf = wc xor w7 xor w1 xor wf
        c = ((d shl 5 or (d ushr 27)) + (e and a or (e and b) or (a and b)) +
                c + wf + -0x70e44324)
        e = e shl 30 or (e ushr 2)
        w0 = wd xor w8 xor w2 xor w0
        b = ((c shl 5 or (c ushr 27)) + (d and e or (d and a) or (e and a)) +
                b + w0 + -0x70e44324)
        d = d shl 30 or (d ushr 2)
        w1 = we xor w9 xor w3 xor w1
        a = ((b shl 5 or (b ushr 27)) + (c and d or (c and e) or (d and e)) +
                a + w1 + -0x70e44324)
        c = c shl 30 or (c ushr 2)
        w2 = wf xor wa xor w4 xor w2
        e = ((a shl 5 or (a ushr 27)) + (b and c or (b and d) or (c and d)) +
                e + w2 + -0x70e44324)
        b = b shl 30 or (b ushr 2)
        w3 = w0 xor wb xor w5 xor w3
        d = ((e shl 5 or (e ushr 27)) + (a and b or (a and c) or (b and c)) +
                d + w3 + -0x70e44324)
        a = a shl 30 or (a ushr 2)
        w4 = w1 xor wc xor w6 xor w4
        c = ((d shl 5 or (d ushr 27)) + (e and a or (e and b) or (a and b)) +
                c + w4 + -0x70e44324)
        e = e shl 30 or (e ushr 2)
        w5 = w2 xor wd xor w7 xor w5
        b = ((c shl 5 or (c ushr 27)) + (d and e or (d and a) or (e and a)) +
                b + w5 + -0x70e44324)
        d = d shl 30 or (d ushr 2)
        w6 = w3 xor we xor w8 xor w6
        a = ((b shl 5 or (b ushr 27)) + (c and d or (c and e) or (d and e)) +
                a + w6 + -0x70e44324)
        c = c shl 30 or (c ushr 2)
        w7 = w4 xor wf xor w9 xor w7
        e = ((a shl 5 or (a ushr 27)) + (b and c or (b and d) or (c and d)) +
                e + w7 + -0x70e44324)
        b = b shl 30 or (b ushr 2)
        w8 = w5 xor w0 xor wa xor w8
        d = ((e shl 5 or (e ushr 27)) + (a and b or (a and c) or (b and c)) +
                d + w8 + -0x70e44324)
        a = a shl 30 or (a ushr 2)
        w9 = w6 xor w1 xor wb xor w9
        c = ((d shl 5 or (d ushr 27)) + (e and a or (e and b) or (a and b)) +
                c + w9 + -0x70e44324)
        e = e shl 30 or (e ushr 2)
        wa = w7 xor w2 xor wc xor wa
        b = ((c shl 5 or (c ushr 27)) + (d and e or (d and a) or (e and a)) +
                b + wa + -0x70e44324)
        d = d shl 30 or (d ushr 2)
        wb = w8 xor w3 xor wd xor wb
        a = ((b shl 5 or (b ushr 27)) + (c and d or (c and e) or (d and e)) +
                a + wb + -0x70e44324)
        c = c shl 30 or (c ushr 2)
        wc = w9 xor w4 xor we xor wc
        e = ((a shl 5 or (a ushr 27)) + (b xor c xor d) +
                e + wc + -0x359d3e2a)
        b = b shl 30 or (b ushr 2)
        wd = wa xor w5 xor wf xor wd
        d = ((e shl 5 or (e ushr 27)) + (a xor b xor c) +
                d + wd + -0x359d3e2a)
        a = a shl 30 or (a ushr 2)
        we = wb xor w6 xor w0 xor we
        c = ((d shl 5 or (d ushr 27)) + (e xor a xor b) +
                c + we + -0x359d3e2a)
        e = e shl 30 or (e ushr 2)
        wf = wc xor w7 xor w1 xor wf
        b = ((c shl 5 or (c ushr 27)) + (d xor e xor a) +
                b + wf + -0x359d3e2a)
        d = d shl 30 or (d ushr 2)
        w0 = wd xor w8 xor w2 xor w0
        a = ((b shl 5 or (b ushr 27)) + (c xor d xor e) +
                a + w0 + -0x359d3e2a)
        c = c shl 30 or (c ushr 2)
        w1 = we xor w9 xor w3 xor w1
        e = ((a shl 5 or (a ushr 27)) + (b xor c xor d) +
                e + w1 + -0x359d3e2a)
        b = b shl 30 or (b ushr 2)
        w2 = wf xor wa xor w4 xor w2
        d = ((e shl 5 or (e ushr 27)) + (a xor b xor c) +
                d + w2 + -0x359d3e2a)
        a = a shl 30 or (a ushr 2)
        w3 = w0 xor wb xor w5 xor w3
        c = ((d shl 5 or (d ushr 27)) + (e xor a xor b) +
                c + w3 + -0x359d3e2a)
        e = e shl 30 or (e ushr 2)
        w4 = w1 xor wc xor w6 xor w4
        b = ((c shl 5 or (c ushr 27)) + (d xor e xor a) +
                b + w4 + -0x359d3e2a)
        d = d shl 30 or (d ushr 2)
        w5 = w2 xor wd xor w7 xor w5
        a = ((b shl 5 or (b ushr 27)) + (c xor d xor e) +
                a + w5 + -0x359d3e2a)
        c = c shl 30 or (c ushr 2)
        w6 = w3 xor we xor w8 xor w6
        e = ((a shl 5 or (a ushr 27)) + (b xor c xor d) +
                e + w6 + -0x359d3e2a)
        b = b shl 30 or (b ushr 2)
        w7 = w4 xor wf xor w9 xor w7
        d = ((e shl 5 or (e ushr 27)) + (a xor b xor c) +
                d + w7 + -0x359d3e2a)
        a = a shl 30 or (a ushr 2)
        w8 = w5 xor w0 xor wa xor w8
        c = ((d shl 5 or (d ushr 27)) + (e xor a xor b) +
                c + w8 + -0x359d3e2a)
        e = e shl 30 or (e ushr 2)
        w9 = w6 xor w1 xor wb xor w9
        b = ((c shl 5 or (c ushr 27)) + (d xor e xor a) +
                b + w9 + -0x359d3e2a)
        d = d shl 30 or (d ushr 2)
        wa = w7 xor w2 xor wc xor wa
        a = ((b shl 5 or (b ushr 27)) + (c xor d xor e) +
                a + wa + -0x359d3e2a)
        c = c shl 30 or (c ushr 2)
        wb = w8 xor w3 xor wd xor wb
        e = ((a shl 5 or (a ushr 27)) + (b xor c xor d) +
                e + wb + -0x359d3e2a)
        b = b shl 30 or (b ushr 2)
        wc = w9 xor w4 xor we xor wc
        d = ((e shl 5 or (e ushr 27)) + (a xor b xor c) +
                d + wc + -0x359d3e2a)
        a = a shl 30 or (a ushr 2)
        wd = wa xor w5 xor wf xor wd
        c = ((d shl 5 or (d ushr 27)) + (e xor a xor b) +
                c + wd + -0x359d3e2a)
        e = e shl 30 or (e ushr 2)
        we = wb xor w6 xor w0 xor we
        b = ((c shl 5 or (c ushr 27)) + (d xor e xor a) +
                b + we + -0x359d3e2a)
        d = d shl 30 or (d ushr 2)
        wf = wc xor w7 xor w1 xor wf
        a = ((b shl 5 or (b ushr 27)) + (c xor d xor e) +
                a + wf + -0x359d3e2a)
        c = c shl 30 or (c ushr 2)
        currentVal[0] += a
        currentVal[1] += b
        currentVal[2] += c
        currentVal[3] += d
        currentVal[4] += e
    }

    override fun toString() = "SHA-0"
}

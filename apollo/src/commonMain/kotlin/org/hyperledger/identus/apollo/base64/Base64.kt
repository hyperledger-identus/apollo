package org.hyperledger.identus.apollo.base64

import kotlin.math.min

/**
 * Base64 implementation
 */
internal final object Base64 {
    /**
     * Encode ByteArray to Base64 String
     */
    fun encodeToString(input: ByteArray, encoding: Encoding = Encoding.Standard): String {
        if (input.contentEquals("".encodeToByteArray())) {
            return ""
        }
        return when (encoding) {
            Encoding.Standard -> getEncoder().withoutPadding().encodeToString(input)
            Encoding.StandardPad -> getEncoder().encodeToString(input)
            Encoding.UrlSafe -> getUrlEncoder().withoutPadding().encodeToString(input)
            Encoding.UrlSafePad -> getUrlEncoder().encodeToString(input)
        }
    }

    /**
     * Encode ByteArray to Base64 ByteArray
     */
    fun encode(input: ByteArray, encoding: Encoding = Encoding.Standard): ByteArray {
        if (input.contentEquals("".encodeToByteArray())) {
            return input
        }
        return when (encoding) {
            Encoding.Standard -> getEncoder().withoutPadding().encode(input)
            Encoding.StandardPad -> getEncoder().encode(input)
            Encoding.UrlSafe -> getUrlEncoder().withoutPadding().encode(input)
            Encoding.UrlSafePad -> getUrlEncoder().encode(input)
        }
    }

    /**
     * Decode string to Base64
     */
    fun decode(input: String, encoding: Encoding = Encoding.Standard): ByteArray {
        return when (encoding) {
            Encoding.Standard, Encoding.StandardPad -> getDecoder().decode(input)
            Encoding.UrlSafe, Encoding.UrlSafePad -> getUrlDecoder().decode(input)
        }
    }

    private fun getEncoder(): Encoder {
        return Encoder.RFC4648
    }

    private fun getDecoder(): Decoder {
        return Decoder.RFC4648
    }

    private fun getUrlEncoder(): Encoder {
        return Encoder.RFC4648_URLSAFE
    }

    private fun getUrlDecoder(): Decoder {
        return Decoder.RFC4648_URLSAFE
    }

    /**
     * This class implements a decoder for decoding byte data using the
     * Base64 encoding scheme as specified in RFC 4648 and RFC 2045.
     *
     *
     *  The Base64 padding character `'='` is accepted and
     * interpreted as the end of the encoded byte data, but is not
     * required. So if the final unit of the encoded byte data only has
     * two or three Base64 characters (without the corresponding padding
     * character(s) padded), they are decoded as if followed by padding
     * character(s). If there is a padding character present in the
     * final unit, the correct number of padding character(s) must be
     * present, otherwise `IllegalArgumentException` (
     * `IOException` when reading from a Base64 stream) is thrown
     * during decoding.
     *
     *
     *  Instances of [Decoder] class are safe for use by
     * multiple concurrent threads.
     *
     *
     *  Unless otherwise noted, passing a `null` argument to
     * a method of this class will cause a
     * [NullPointerException][NullPointerException] to
     * be thrown.
     *
     * @see Encoder
     *
     * @since 1.8
     */
    private class Decoder private constructor(private val isURL: Boolean, private val isMIME: Boolean) {
        /**
         * Decodes all bytes from the input byte array using the [Base64]
         * encoding scheme, writing the results into a newly-allocated output
         * byte array. The returned byte array is of the length of the resulting
         * bytes.
         *
         * @param src
         * the byte array to decode
         *
         * @return A newly-allocated byte array containing the decoded bytes.
         *
         * @throws IllegalArgumentException
         * if `src` is not in valid Base64 scheme
         */
        fun decode(src: ByteArray): ByteArray {
            val dst = ByteArray(outLength(src, 0, src.size))
            val ret = decode0(src, 0, src.size, dst)
            if (ret != dst.size) {
                dst.copyInto(ByteArray(ret))
                // dst = java.util.Arrays.copyOf(dst, ret)
            }
            return dst
        }

        /**
         * Decodes a Base64 encoded String into a newly-allocated byte array
         * using the [Base64] encoding scheme.
         *
         *
         *  An invocation of this method has exactly the same effect as invoking
         * `decode(src.getBytes(StandardCharsets.ISO_8859_1))`
         *
         * @param src
         * the string to decode
         *
         * @return A newly-allocated byte array containing the decoded bytes.
         *
         * @throws IllegalArgumentException
         * if `src` is not in valid Base64 scheme
         */
        fun decode(src: String): ByteArray {
            return decode(src.encodeToByteArray())
            // return decode(src.toByteArray(java.nio.charset.StandardCharsets.ISO_8859_1))
        }

        /**
         * Decodes all bytes from the input byte array using the [Base64]
         * encoding scheme, writing the results into the given output byte array,
         * starting at offset 0.
         *
         *
         *  It is the responsibility of the invoker of this method to make
         * sure the output byte array `dst` has enough space for decoding
         * all bytes from the input byte array. No bytes will be be written to
         * the output byte array if the output byte array is not big enough.
         *
         *
         *  If the input byte array is not in valid Base64 encoding scheme
         * then some bytes may have been written to the output byte array before
         * [IllegalArgumentException] is thrown.
         *
         * @param src
         * the byte array to decode
         * @param dst
         * the output byte array
         *
         * @return The number of bytes written to the output byte array
         *
         * @throws IllegalArgumentException
         * if `src` is not in valid Base64 scheme, or `dst`
         * does not have enough space for decoding all input bytes.
         */
        @Throws(IllegalArgumentException::class)
        fun decode(src: ByteArray, dst: ByteArray): Int {
            val len = outLength(src, 0, src.size)
            if (dst.size < len) {
                throw IllegalArgumentException("Output byte array is too small for decoding all input bytes")
            }
            return decode0(src, 0, src.size, dst)
        }

        @Suppress("NAME_SHADOWING")
        @Throws(IllegalArgumentException::class)
        private fun outLength(src: ByteArray, sp: Int, sl: Int): Int {
            var sp = sp
            val base64 =
                if (isURL) {
                    fromBase64URL
                } else {
                    fromBase64
                }
            var paddings = 0
            var len = sl - sp
            if (len == 0) {
                return 0
            }
            if (len < 2) {
                if (isMIME && base64[0] == -1) {
                    return 0
                }
                throw IllegalArgumentException("Input byte[] should at least have 2 bytes for base64 bytes")
            }
            if (isMIME) {
                // scan all bytes to fill out all non-alphabet. a performance
                // trade-off of pre-scan or Arrays.copyOf
                var n = 0
                while (sp < sl) {
                    var b = src[sp++].toInt() and 0xff
                    if (b == '='.code) {
                        len -= sl - sp + 1
                        break
                    }
                    if (base64[b].also { b = it } == -1) {
                        n++
                    }
                }
                len -= n
            } else {
                if (src[sl - 1] == '='.code.toByte()) {
                    paddings++
                    if (src[sl - 2] == '='.code.toByte()) {
                        paddings++
                    }
                }
            }
            if (paddings == 0 && len and 0x3 != 0) paddings = 4 - (len and 0x3)
            return 3 * ((len + 3) / 4) - paddings
        }

        @Suppress("NAME_SHADOWING")
        @Throws(IllegalArgumentException::class)
        private fun decode0(src: ByteArray, sp: Int, sl: Int, dst: ByteArray): Int {
            var sp = sp
            val base64 = if (isURL) fromBase64URL else fromBase64
            var dp = 0
            var bits = 0
            var shiftto = 18 // pos of first byte of 4-byte atom
            while (sp < sl) {
                var b = src[sp++].toInt() and 0xff
                if (base64[b].also { b = it } < 0) {
                    if (b == -2) { // padding byte '='
                        // =     shiftto==18 unnecessary padding
                        // x=    shiftto==12 a dangling single x
                        // x     to be handled together with non-padding case
                        // xx=   shiftto==6&&sp==sl missing last =
                        // xx=y  shiftto==6 last is not =
                        if (shiftto == 6 && (sp == sl || src[sp++] != '='.code.toByte()) ||
                            shiftto == 18
                        ) {
                            throw IllegalArgumentException("Input byte array has wrong 4-byte ending unit")
                        }
                        break
                    }
                    if (isMIME) { // skip if for rfc2045
                        continue
                    } else {
                        throw IllegalArgumentException("Illegal base64 character ${src[sp - 1].toInt().toString(16)}")
                    }
                }
                bits = bits or (b shl shiftto)
                shiftto -= 6
                if (shiftto < 0) {
                    dst[dp++] = (bits shr 16).toByte()
                    dst[dp++] = (bits shr 8).toByte()
                    dst[dp++] = bits.toByte()
                    shiftto = 18
                    bits = 0
                }
            }
            // reached end of byte array or hit padding '=' characters.
            when (shiftto) {
                6 -> {
                    dst[dp++] = (bits shr 16).toByte()
                }
                0 -> {
                    dst[dp++] = (bits shr 16).toByte()
                    dst[dp++] = (bits shr 8).toByte()
                }
                12 -> {
                    // dangling single "x", incorrectly encoded.
                    throw IllegalArgumentException("Last unit does not have enough valid bits")
                }
            }
            // anything left is invalid, if is not MIME.
            // if MIME, ignore all non-base64 character
            while (sp < sl) {
                if (isMIME && base64[src[sp++].toInt()] < 0) {
                    continue
                }
                throw IllegalArgumentException("Input byte array has incorrect ending byte at $sp")
            }
            return dp
        }

        companion object {
            /**
             * Lookup table for decoding unicode characters drawn from the
             * "Base64 Alphabet" (as specified in Table 1 of RFC 2045) into
             * their 6-bit positive integer equivalents.  Characters that
             * are not in the Base64 alphabet but fall within the bounds of
             * the array are encoded to -1.
             *
             */
            private val fromBase64 = IntArray(256)

            init {
                fromBase64.fill(-1)
                // java.util.Arrays.fill(fromBase64, -1)
                for (i in Encoder.toBase64.indices) fromBase64[Encoder.toBase64.get(i).code] =
                    i
                fromBase64['='.code] = -2
            }

            /**
             * Lookup table for decoding "URL and Filename safe Base64 Alphabet"
             * as specified in Table2 of the RFC 4648.
             */
            private val fromBase64URL = IntArray(256)

            init {
                fromBase64URL.fill(-1)
                // java.util.Arrays.fill(fromBase64URL, -1)
                for (i in Encoder.toBase64URL.indices) fromBase64URL[Encoder.toBase64URL[i].code] = i
                fromBase64URL['='.code] = -2
            }

            val RFC4648 = Decoder(false, false)
            val RFC4648_URLSAFE = Decoder(true, false)
            val RFC2045 = Decoder(false, true)
        }
    }

    /**
     * This class implements an encoder for encoding byte data using
     * the Base64 encoding scheme as specified in RFC 4648 and RFC 2045.
     *
     *
     *  Instances of [Encoder] class are safe for use by
     * multiple concurrent threads.
     *
     *
     *  Unless otherwise noted, passing a `null` argument to
     * a method of this class will cause a
     * [NullPointerException][java.lang.NullPointerException] to
     * be thrown.
     *
     * @see Decoder
     *
     * @since 1.8
     */
    private class Encoder private constructor(
        private val isURL: Boolean,
        private val newline: ByteArray?,
        private val linemax: Int,
        private val doPadding: Boolean
    ) {
        private fun outLength(srclen: Int): Int {
            var len =
                if (doPadding) {
                    4 * ((srclen + 2) / 3)
                } else {
                    val n = srclen % 3
                    4 * (srclen / 3) + if (n == 0) 0 else n + 1
                }
            if (linemax > 0) { // line separators
                len += (len - 1) / linemax * newline!!.size
            }
            return len
        }

        /**
         * Encodes all bytes from the specified byte array into a newly-allocated
         * byte array using the [Base64] encoding scheme. The returned byte
         * array is of the length of the resulting bytes.
         *
         * @param src
         * the byte array to encode
         * @return A newly-allocated byte array containing the resulting
         * encoded bytes.
         */
        fun encode(src: ByteArray): ByteArray {
            val len = outLength(src.size) // dst array size
            val dst = ByteArray(len)
            val ret = encode0(src, 0, src.size, dst)
            return if (ret != dst.size) {
                dst.copyInto(ByteArray(ret))
                // java.util.Arrays.copyOf(dst, ret)
            } else {
                dst
            }
        }

        /**
         * Encodes all bytes from the specified byte array using the
         * [Base64] encoding scheme, writing the resulting bytes to the
         * given output byte array, starting at offset 0.
         *
         *
         *  It is the responsibility of the invoker of this method to make
         * sure the output byte array `dst` has enough space for encoding
         * all bytes from the input byte array. No bytes will be written to the
         * output byte array if the output byte array is not big enough.
         *
         * @param src
         * the byte array to encode
         * @param dst
         * the output byte array
         * @return The number of bytes written to the output byte array
         *
         * @throws IllegalArgumentException if `dst` does not have enough
         * space for encoding all input bytes.
         */
        @Throws(IllegalArgumentException::class)
        fun encode(src: ByteArray, dst: ByteArray): Int {
            val len = outLength(src.size) // dst array size
            if (dst.size < len) {
                throw IllegalArgumentException("Output byte array is too small for encoding all input bytes")
            }
            return encode0(src, 0, src.size, dst)
        }

        /**
         * Encodes the specified byte array into a String using the [Base64]
         * encoding scheme.
         *
         *
         *  This method first encodes all input bytes into a base64 encoded
         * byte array and then constructs a new String by using the encoded byte
         * array and the [ ISO-8859-1][java.nio.charset.StandardCharsets.ISO_8859_1] charset.
         *
         *
         *  In other words, an invocation of this method has exactly the same
         * effect as invoking
         * `new String(encode(src), StandardCharsets.ISO_8859_1)`.
         *
         * @param src
         * the byte array to encode
         * @return A String containing the resulting Base64 encoded characters
         */
        fun encodeToString(src: ByteArray): String {
            val encoded: ByteArray = encode(src)
            return encoded.decodeToString()
            // return String(encoded, 0, 0, encoded.size)
        }

        /**
         * Returns an encoder instance that encodes equivalently to this one,
         * but without adding any padding character at the end of the encoded
         * byte data.
         *
         *
         *  The encoding scheme of this encoder instance is unaffected by
         * this invocation. The returned encoder instance should be used for
         * non-padding encoding operation.
         *
         * @return an equivalent encoder that encodes without adding any
         * padding character at the end
         */
        fun withoutPadding(): Encoder {
            return if (!doPadding) this else Encoder(isURL, newline, linemax, false)
        }

        @Suppress("UNUSED_CHANGED_VALUE")
        private fun encode0(src: ByteArray, off: Int, end: Int, dst: ByteArray): Int {
            val base64 = if (isURL) toBase64URL else toBase64
            var sp = off
            var slen = (end - off) / 3 * 3
            val sl = off + slen
            if (linemax > 0 && slen > linemax / 4 * 3) slen = linemax / 4 * 3
            var dp = 0
            while (sp < sl) {
                val sl0: Int = min(sp + slen, sl)
                var sp0 = sp
                var dp0 = dp
                while (sp0 < sl0) {
                    val bits =
                        src[sp0++].toInt() and 0xff shl 16 or (
                            src[sp0++].toInt() and 0xff shl 8
                            ) or
                            (src[sp0++].toInt() and 0xff)
                    dst[dp0++] = base64[bits ushr 18 and 0x3f].code.toByte()
                    dst[dp0++] = base64[bits ushr 12 and 0x3f].code.toByte()
                    dst[dp0++] = base64[bits ushr 6 and 0x3f].code.toByte()
                    dst[dp0++] = base64[bits and 0x3f].code.toByte()
                }
                val dlen = (sl0 - sp) / 3 * 4
                dp += dlen
                sp = sl0
                if (dlen == linemax && sp < end) {
                    for (b in newline!!) {
                        dst[dp++] = b
                    }
                }
            }
            if (sp < end) { // 1 or 2 leftover bytes
                val b0 = src[sp++].toInt() and 0xff
                dst[dp++] = base64[b0 shr 2].code.toByte()
                if (sp == end) {
                    dst[dp++] = base64[b0 shl 4 and 0x3f].code.toByte()
                    if (doPadding) {
                        dst[dp++] = '='.code.toByte()
                        dst[dp++] = '='.code.toByte()
                    }
                } else {
                    val b1 = src[sp++].toInt() and 0xff
                    dst[dp++] = base64[b0 shl 4 and 0x3f or (b1 shr 4)].code.toByte()
                    dst[dp++] = base64[b1 shl 2 and 0x3f].code.toByte()
                    if (doPadding) {
                        dst[dp++] = '='.code.toByte()
                    }
                }
            }
            return dp
        }

        companion object {
            /**
             * This array is a lookup table that translates 6-bit positive integer
             * index values into their "Base64 Alphabet" equivalents as specified
             * in "Table 1: The Base64 Alphabet" of RFC 2045 (and RFC 4648).
             */
            val toBase64 =
                charArrayOf(
                    'A',
                    'B',
                    'C',
                    'D',
                    'E',
                    'F',
                    'G',
                    'H',
                    'I',
                    'J',
                    'K',
                    'L',
                    'M',
                    'N',
                    'O',
                    'P',
                    'Q',
                    'R',
                    'S',
                    'T',
                    'U',
                    'V',
                    'W',
                    'X',
                    'Y',
                    'Z',
                    'a',
                    'b',
                    'c',
                    'd',
                    'e',
                    'f',
                    'g',
                    'h',
                    'i',
                    'j',
                    'k',
                    'l',
                    'm',
                    'n',
                    'o',
                    'p',
                    'q',
                    'r',
                    's',
                    't',
                    'u',
                    'v',
                    'w',
                    'x',
                    'y',
                    'z',
                    '0',
                    '1',
                    '2',
                    '3',
                    '4',
                    '5',
                    '6',
                    '7',
                    '8',
                    '9',
                    '+',
                    '/'
                )

            /**
             * It's the lookup table for "URL and Filename safe Base64" as specified
             * in Table 2 of the RFC 4648, with the '+' and '/' changed to '-' and
             * '_'. This table is used when BASE64_URL is specified.
             */
            val toBase64URL =
                charArrayOf(
                    'A',
                    'B',
                    'C',
                    'D',
                    'E',
                    'F',
                    'G',
                    'H',
                    'I',
                    'J',
                    'K',
                    'L',
                    'M',
                    'N',
                    'O',
                    'P',
                    'Q',
                    'R',
                    'S',
                    'T',
                    'U',
                    'V',
                    'W',
                    'X',
                    'Y',
                    'Z',
                    'a',
                    'b',
                    'c',
                    'd',
                    'e',
                    'f',
                    'g',
                    'h',
                    'i',
                    'j',
                    'k',
                    'l',
                    'm',
                    'n',
                    'o',
                    'p',
                    'q',
                    'r',
                    's',
                    't',
                    'u',
                    'v',
                    'w',
                    'x',
                    'y',
                    'z',
                    '0',
                    '1',
                    '2',
                    '3',
                    '4',
                    '5',
                    '6',
                    '7',
                    '8',
                    '9',
                    '-',
                    '_'
                )
            private const val MIMELINEMAX = 76
            private val CRLF = byteArrayOf('\r'.code.toByte(), '\n'.code.toByte())
            val RFC4648 = Encoder(false, null, -1, true)
            val RFC4648_URLSAFE = Encoder(true, null, -1, true)
            val RFC2045 = Encoder(false, CRLF, MIMELINEMAX, true)
        }
    }
}

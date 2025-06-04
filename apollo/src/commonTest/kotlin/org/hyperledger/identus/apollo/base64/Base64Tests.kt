package org.hyperledger.identus.apollo.base64

import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class Base64Tests {
    // Extension
    @Test
    fun byteArray_asCharArray() {
        assertContentEquals(
            charArrayOf('"', 'F', '	', 'ￄ', '_', '\'', '!', Char.MIN_VALUE, '�', 'E', 'ﾎ', '￘', 'ﾓ', 'ﾪ', '￶', '￨'),
            byteArrayOf(34, 70, 9, -60, 95, 39, 33, 0, -3, 69, -114, -40, -109, -86, -10, -24).asCharArray()
        )
    }

    // Base64Standard
    @Test
    fun byteArray_base64Decoded() {
        assertEquals("Hello, world!", "SGVsbG8sIHdvcmxkIQ==".base64PadDecoded)
        assertContentEquals(
            byteArrayOf(
                -94,
                124,
                -26,
                -112,
                -72,
                -84,
                16,
                11,
                67,
                -45,
                107,
                38,
                -99,
                79,
                62,
                -49,
                83,
                26,
                -85,
                -70,
                -122,
                53,
                67,
                42,
                -94,
                -87,
                61,
                -74,
                66,
                0,
                80,
                -125,
                -17,
                -11,
                -125,
                63,
                109,
                -15,
                56,
                -95,
                -33,
                18,
                110,
                47,
                47,
                -20,
                -72,
                -34,
                53,
                -69,
                49,
                -45,
                54,
                53,
                -21,
                43,
                9,
                -84,
                -125,
                72,
                -61,
                76,
                31,
                -46
            ),
            "onzmkLisEAtD02smnU8+z1Maq7qGNUMqoqk9tkIAUIPv9YM/bfE4od8Sbi8v7LjeNbsx0zY16ysJrINIw0wf0g==".base64PadDecodedBytes
        )
    }

    @Test
    fun byteArray_base64Encoded() {
        assertEquals(
            "xvrp9DBWlei2mG0ov9MN+A==",
            byteArrayOf(-58, -6, -23, -12, 48, 86, -107, -24, -74, -104, 109, 40, -65, -45, 13, -8).base64PadEncoded
        )
        assertEquals(
            "IkYJxF8nIQD9RY7Yk6r26A==",
            byteArrayOf(34, 70, 9, -60, 95, 39, 33, 0, -3, 69, -114, -40, -109, -86, -10, -24).base64PadEncoded
        )
        assertEquals(
            "U0GeVBi2dNcdL2IO0nJo5Q==",
            byteArrayOf(83, 65, -98, 84, 24, -74, 116, -41, 29, 47, 98, 14, -46, 114, 104, -27).base64PadEncoded
        )
    }

    @Test
    fun string_base64Decoded() {
        assertEquals("word", "d29yZA==".base64PadDecoded)
        assertEquals("Word", "V29yZA==".base64PadDecoded)
        assertEquals("Hello", "SGVsbG8=".base64PadDecoded)
        assertEquals("World!", "V29ybGQh".base64PadDecoded)
        assertEquals("Hello, world!", "SGVsbG8sIHdvcmxkIQ==".base64PadDecoded)
        assertEquals(
            Encoding.Standard.alphabet,
            "QUJDREVGR0hJSktMTU5PUFFSU1RVVldYWVphYmNkZWZnaGlqa2xtbm9wcXJzdHV2d3h5ejAxMjM0NTY3ODkrLw==".base64PadDecoded
        )
        assertEquals("abcd", "YWJjZA==".base64PadDecoded)
        assertEquals("saschpe", "c2FzY2hwZQ==".base64PadDecoded)
        assertEquals(
            "1234567890-=!@#\$%^&*()_+qwertyuiop[];'\\,./?><|\":}{P`~",
            "MTIzNDU2Nzg5MC09IUAjJCVeJiooKV8rcXdlcnR5dWlvcFtdOydcLC4vPz48fCI6fXtQYH4=".base64PadDecoded
        )
    }

    @Test
    fun string_base64Encoded() {
        assertEquals("d29yZA==", "word".base64PadEncoded)
        assertEquals("V29yZA==", "Word".base64PadEncoded)
        assertEquals("SGVsbG8=", "Hello".base64PadEncoded)
        assertEquals("V29ybGQh", "World!".base64PadEncoded)
        assertEquals("SGVsbG8sIHdvcmxkIQ==", "Hello, world!".base64PadEncoded)
        assertEquals("SGVsbG8sIHdvcmxkIQ==", "Hello, world!".encodeToByteArray().base64PadEncoded)
        assertEquals(
            "QUJDREVGR0hJSktMTU5PUFFSU1RVVldYWVphYmNkZWZnaGlqa2xtbm9wcXJzdHV2d3h5ejAxMjM0NTY3ODkrLw==",
            Encoding.Standard.alphabet.base64PadEncoded
        )
        assertEquals("YWJjZA==", "abcd".base64PadEncoded)
        assertEquals("c2FzY2hwZQ==", "saschpe".base64PadEncoded)
        assertEquals(
            "MTIzNDU2Nzg5MC09IUAjJCVeJiooKV8rcXdlcnR5dWlvcFtdOydcLC4vPz48fCI6fXtQYH4=",
            "1234567890-=!@#\$%^&*()_+qwertyuiop[];'\\,./?><|\":}{P`~".base64PadEncoded
        )
    }

    @Test
    fun byteArray_base64UrlDecoded() {
        assertEquals("Hello, world!", "SGVsbG8sIHdvcmxkIQ==".base64UrlPadDecoded)
        assertContentEquals(
            byteArrayOf(
                -94,
                124,
                -26,
                -112,
                -72,
                -84,
                16,
                11,
                67,
                -45,
                107,
                38,
                -99,
                79,
                62,
                -49,
                83,
                26,
                -85,
                -70,
                -122,
                53,
                67,
                42,
                -94,
                -87,
                61,
                -74,
                66,
                0,
                80,
                -125,
                -17,
                -11,
                -125,
                63,
                109,
                -15,
                56,
                -95,
                -33,
                18,
                110,
                47,
                47,
                -20,
                -72,
                -34,
                53,
                -69,
                49,
                -45,
                54,
                53,
                -21,
                43,
                9,
                -84,
                -125,
                72,
                -61,
                76,
                31,
                -46
            ),
            "onzmkLisEAtD02smnU8-z1Maq7qGNUMqoqk9tkIAUIPv9YM_bfE4od8Sbi8v7LjeNbsx0zY16ysJrINIw0wf0g==".base64UrlPadDecodedBytes
        )
    }

    @Test
    fun byteArray_base64UrlEncoded() {
        assertEquals(
            "xvrp9DBWlei2mG0ov9MN-A",
            byteArrayOf(-58, -6, -23, -12, 48, 86, -107, -24, -74, -104, 109, 40, -65, -45, 13, -8).base64UrlEncoded
        )
        assertEquals(
            "IkYJxF8nIQD9RY7Yk6r26A",
            byteArrayOf(34, 70, 9, -60, 95, 39, 33, 0, -3, 69, -114, -40, -109, -86, -10, -24).base64UrlEncoded
        )
        assertEquals(
            "U0GeVBi2dNcdL2IO0nJo5Q",
            byteArrayOf(83, 65, -98, 84, 24, -74, 116, -41, 29, 47, 98, 14, -46, 114, 104, -27).base64UrlEncoded
        )
    }

    @Test
    fun string_base64UrlDecoded() {
        assertEquals("word", "d29yZA==".base64UrlDecoded)
        assertEquals("Word", "V29yZA==".base64UrlDecoded)
        assertEquals("Hello", "SGVsbG8=".base64UrlDecoded)
        assertEquals("World!", "V29ybGQh".base64UrlDecoded)
        assertEquals("Hello, world!", "SGVsbG8sIHdvcmxkIQ".base64UrlDecoded)
        assertEquals("Hello, world!", "SGVsbG8sIHdvcmxkIQ==".base64UrlPadDecoded)
        assertEquals(
            Encoding.Standard.alphabet,
            "QUJDREVGR0hJSktMTU5PUFFSU1RVVldYWVphYmNkZWZnaGlqa2xtbm9wcXJzdHV2d3h5ejAxMjM0NTY3ODkrLw==".base64UrlPadDecoded
        )
        assertEquals("Salt", "U2FsdA==".base64UrlPadDecoded)
        assertEquals("Pepper", "UGVwcGVy".base64UrlPadDecoded)
        assertEquals("abcd", "YWJjZA".base64UrlPadDecoded)
        assertEquals(
            "1234567890-=!@#\$%^&*()_+qwertyuiop[];'\\,./?><|\":}{P`~",
            "MTIzNDU2Nzg5MC09IUAjJCVeJiooKV8rcXdlcnR5dWlvcFtdOydcLC4vPz48fCI6fXtQYH4".base64UrlDecoded
        )
        assertEquals("saschpe", "c2FzY2hwZQ".base64UrlDecoded)
    }

    @Test
    fun string_base64UrlEncoded() {
        assertEquals("d29yZA", "word".base64UrlEncoded)
        assertEquals("V29yZA", "Word".base64UrlEncoded)
        assertEquals("SGVsbG8", "Hello".base64UrlEncoded)
        assertEquals("V29ybGQh", "World!".base64UrlEncoded)
        assertEquals("SGVsbG8sIHdvcmxkIQ", "Hello, world!".base64UrlEncoded)
        assertEquals("SGVsbG8sIHdvcmxkIQ", "Hello, world!".encodeToByteArray().base64UrlEncoded)
        assertEquals(
            "QUJDREVGR0hJSktMTU5PUFFSU1RVVldYWVphYmNkZWZnaGlqa2xtbm9wcXJzdHV2d3h5ejAxMjM0NTY3ODkrLw",
            Encoding.Standard.alphabet.base64UrlEncoded
        )
        assertEquals("U2FsdA", "Salt".base64UrlEncoded)
        assertEquals("UGVwcGVy", "Pepper".base64UrlEncoded)
        assertEquals("YWJjZA", "abcd".base64UrlEncoded)
        assertEquals(
            "MTIzNDU2Nzg5MC09IUAjJCVeJiooKV8rcXdlcnR5dWlvcFtdOydcLC4vPz48fCI6fXtQYH4",
            "1234567890-=!@#\$%^&*()_+qwertyuiop[];'\\,./?><|\":}{P`~".base64UrlEncoded
        )
        assertEquals("c2FzY2hwZQ", "saschpe".base64UrlEncoded)
    }

    @Test
    fun testEncodeBase16RFC_4648() {
        assertEquals("", "".base64PadEncoded)
    }

    @Test
    fun testEncodeBase16RFC_4648_1() {
        assertEquals("Zg==", "f".base64PadEncoded)
    }

    @Test
    fun testEncodeBase16RFC_4648_2() {
        assertEquals("Zm8=", "fo".base64PadEncoded)
    }

    @Test
    fun testEncodeBase16RFC_4648_3() {
        assertEquals("Zm9v", "foo".base64PadEncoded)
    }

    @Test
    fun testEncodeBase16RFC_4648_4() {
        assertEquals("Zm9vYg==", "foob".base64PadEncoded)
    }

    @Test
    fun testEncodeBase16RFC_4648_5() {
        assertEquals("Zm9vYmE=", "fooba".base64PadEncoded)
    }

    @Test
    fun testEncodeBase16RFC_4648_6() {
        assertEquals("Zm9vYmFy", "foobar".base64PadEncoded)
    }
}

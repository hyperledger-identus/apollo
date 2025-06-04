package org.hyperledger.identus.apollo.utils

import org.hyperledger.identus.apollo.base64.base64DecodedBytes
import org.hyperledger.identus.apollo.base64.base64PadDecodedBytes
import org.hyperledger.identus.apollo.base64.base64UrlDecodedBytes
import org.hyperledger.identus.apollo.base64.base64UrlEncoded
import org.hyperledger.identus.apollo.secp256k1.Secp256k1Lib
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class Secp256k1LibTests {
    @Test
    fun testCreatePublicKey() {
        val privKeyBase64 = "N/JFgvYaReyRXwassz5FHg33A4I6dczzdXrjdHGksmg="
        val base64ByteArray = privKeyBase64.base64DecodedBytes
        val pubKey = Secp256k1Lib().createPublicKey(base64ByteArray, false)
        assertEquals(
            "BD-l4lrQ6Go-oN5XtdpY6o5dyf2V2v5EbMAvRjVGJpE1gYVURJfxKMpNPnKlLr4MOLNVaYvBNOoy9L50E8jVx8Q",
            pubKey.base64UrlEncoded
        )
    }

    @Test
    fun testDerivePrivateKey() {
        val privKeyBase64 = "96ViMAl0/N1Xm5RJesQxC2NvxhNc4ZkwPyVevZ4akDI="
        val derivedPrivKeyBase64 = "xEDIjzhlf/0o+vL42KupeLuZDiWBqpUHhVuwO8a2BBA="

        val derivedKey =
            Secp256k1Lib().derivePrivateKey(
                privKeyBase64.base64DecodedBytes,
                derivedPrivKeyBase64.base64PadDecodedBytes
            )
        assertEquals(
            "u-Yqv0HafNqAlodCU2_ahWRZ91IvQ438BK6wbJSaUwE",
            derivedKey!!.base64UrlEncoded
        )
    }

    @Test
    fun testSignature() {
        val privKeyBase64 = "N_JFgvYaReyRXwassz5FHg33A4I6dczzdXrjdHGksmg"
        val message = "Test"

        val signature =
            Secp256k1Lib()
                .sign(privKeyBase64.base64UrlDecodedBytes, message.encodeToByteArray())
        assertEquals(
            "MEUCIQCFeGlhJrH-9R70X4JzrurWs52SwuxCnJ8ky6riFwMOrwIgT7zlLo7URMHW5tiMgG73IOw2Dm3XyLl1iqW1-t5NFWQ",
            signature.base64UrlEncoded
        )
    }

    @Test
    fun testVerification() {
        val pubKeyBase64 = "BD-l4lrQ6Go-oN5XtdpY6o5dyf2V2v5EbMAvRjVGJpE1gYVURJfxKMpNPnKlLr4MOLNVaYvBNOoy9L50E8jVx8Q"
        val signatureBase64 =
            "MEUCIQCFeGlhJrH-9R70X4JzrurWs52SwuxCnJ8ky6riFwMOrwIgT7zlLo7URMHW5tiMgG73IOw2Dm3XyLl1iqW1-t5NFWQ"
        val message = "Test"

        assertTrue {
            Secp256k1Lib().verify(
                pubKeyBase64.base64UrlDecodedBytes,
                signatureBase64.base64UrlDecodedBytes,
                message.encodeToByteArray()
            )
        }
    }

    @Test
    fun testVerificationOfNimbusJWTSignature() {
        val pubKeyBase64 = "BIkm9EiFvopehs3FUHFkp+vYYGuyWwhOhaEhAE/XbX6u9FcLWxguRJYRCVcuN7AHDo8wePDUVDI9UrvMSaKXbiw="
        val signatureBase64 =
            "gKPlzHeuO0NryO1f_iHzxKUtyyD6e_woWNoK2QsEBGuhQw-tkJpKVvBzK0F-z7bdG0nj6xWZBmlJ7ctsVexYPg"
        val message = "eyJhbGciOiJFUzI1NksifQ.eyJpc3MiOiJkaWQ6cHJpc206MGYzN2Y2YmRmZWNlMzQzNzJmMzE3YmM5NDQyNzY5YzI0Yzk1ZmNkYjQzZDAzOTNiOTZiOGQ3YWEwODBlZDBiNzpDcmtCQ3JZQkVqb0tCbUYxZEdndE1SQUVTaTRLQ1hObFkzQXlOVFpyTVJJaEF2TWkxYUZZaTdOUlFWQ00zU2s2TjBvQkhIOXlabUhUcGtOT0tyd1QzYWdVRWpzS0IybHpjM1ZsTFRFUUFrb3VDZ2x6WldOd01qVTJhekVTSVFLSkp2UkloYjZLWG9iTnhWQnhaS2ZyMkdCcnNsc0lUb1doSVFCUDEyMS1yaEk3Q2dkdFlYTjBaWEl3RUFGS0xnb0pjMlZqY0RJMU5tc3hFaUVDRXI2QkJMbFVEcjFMcHdJZ1JLcHZZQ1BYUkRfWFh5SFpTbWdVTXMxZlpVMCIsInN1YiI6ImRpZDpwcmlzbTpiYWQyNDgzNDA2NjU5MmIwMDgwYjlmMWYyZjkxZjA1ZmNiZDU0MzQxMDlkZTIwMTM3YmRiOWE0ZWE4N2E4NjQ4OkN0OEJDdHdCRW5RS0gyRjFkR2hsYm5ScFkyRjBhVzl1WVhWMGFHVnVkR2xqWVhScGIyNUxaWGtRQkVKUENnbHpaV053TWpVMmF6RVNJSDNWNzdRb3dXWWZpN1BHdFZTSjBIZVdjcVlwdmxzanNydUN1ZUhITDByZUdpRHl4OWVBN0lQdVgyLUExMkxHZktFczJZTGJoUm1ILUdWbzFNUndZdUYta0JKa0NnOXRZWE4wWlhKdFlYTjBaWEpMWlhrUUFVSlBDZ2x6WldOd01qVTJhekVTSUgzVjc3UW93V1lmaTdQR3RWU0owSGVXY3FZcHZsc2pzcnVDdWVISEwwcmVHaUR5eDllQTdJUHVYMi1BMTJMR2ZLRXMyWUxiaFJtSC1HVm8xTVJ3WXVGLWtBIiwibmJmIjoxNzEzODYyMjk4LCJ2YyI6eyJjcmVkZW50aWFsU3ViamVjdCI6eyJlbWFpbEFkZHJlc3MiOiJjb3Jwb3JhdGVAZG9tYWluLmNvbSIsImRyaXZpbmdDbGFzcyI6MSwiZHJpdmluZ0xpY2Vuc2VJRCI6IkVTLTEyMzQ1Njc4OTAiLCJpZCI6ImRpZDpwcmlzbTpiYWQyNDgzNDA2NjU5MmIwMDgwYjlmMWYyZjkxZjA1ZmNiZDU0MzQxMDlkZTIwMTM3YmRiOWE0ZWE4N2E4NjQ4OkN0OEJDdHdCRW5RS0gyRjFkR2hsYm5ScFkyRjBhVzl1WVhWMGFHVnVkR2xqWVhScGIyNUxaWGtRQkVKUENnbHpaV053TWpVMmF6RVNJSDNWNzdRb3dXWWZpN1BHdFZTSjBIZVdjcVlwdmxzanNydUN1ZUhITDByZUdpRHl4OWVBN0lQdVgyLUExMkxHZktFczJZTGJoUm1ILUdWbzFNUndZdUYta0JKa0NnOXRZWE4wWlhKdFlYTjBaWEpMWlhrUUFVSlBDZ2x6WldOd01qVTJhekVTSUgzVjc3UW93V1lmaTdQR3RWU0owSGVXY3FZcHZsc2pzcnVDdWVISEwwcmVHaUR5eDllQTdJUHVYMi1BMTJMR2ZLRXMyWUxiaFJtSC1HVm8xTVJ3WXVGLWtBIiwiZGF0ZU9mSXNzdWFuY2UiOiIyMDIzLTAxLTAxVDAyOjAyOjAyWiJ9LCJ0eXBlIjpbIlZlcmlmaWFibGVDcmVkZW50aWFsIl0sIkBjb250ZXh0IjpbImh0dHBzOlwvXC93d3cudzMub3JnXC8yMDE4XC9jcmVkZW50aWFsc1wvdjEiXSwiY3JlZGVudGlhbFN0YXR1cyI6eyJzdGF0dXNQdXJwb3NlIjoiUmV2b2NhdGlvbiIsInN0YXR1c0xpc3RJbmRleCI6NCwiaWQiOiJodHRwOlwvXC8xOTIuMTY4LjEuNDQ6ODAwMFwvcHJpc20tYWdlbnRcL2NyZWRlbnRpYWwtc3RhdHVzXC9jMDkxOGViNi1lZGIzLTRjMTUtYWM4OS0yZDk5MTZmMjFmYjUjNCIsInR5cGUiOiJTdGF0dXNMaXN0MjAyMUVudHJ5Iiwic3RhdHVzTGlzdENyZWRlbnRpYWwiOiJodHRwOlwvXC8xOTIuMTY4LjEuNDQ6ODAwMFwvcHJpc20tYWdlbnRcL2NyZWRlbnRpYWwtc3RhdHVzXC9jMDkxOGViNi1lZGIzLTRjMTUtYWM4OS0yZDk5MTZmMjFmYjUifX19"

        assertTrue {
            Secp256k1Lib().verify(
                pubKeyBase64.base64DecodedBytes,
                signatureBase64.base64UrlDecodedBytes,
                message.encodeToByteArray()
            )
        }
    }

    @Test
    fun testCompress() {
        val pubKeyBase64 = "BHza5mV6_Iz6XdyMpxpjUMprZUCN_MpMuQCTFYpxSf8rW7N7DD04troywCgLkg0_ABP-IcxZcE1-qKjwCWYTVO8"

        assertEquals(
            Secp256k1Lib().compressPublicKey(pubKeyBase64.base64UrlDecodedBytes).base64UrlEncoded,
            "A3za5mV6_Iz6XdyMpxpjUMprZUCN_MpMuQCTFYpxSf8r"
        )
    }

    @Test
    fun testUncompress() {
        val pubKeyBase64 = "A3za5mV6_Iz6XdyMpxpjUMprZUCN_MpMuQCTFYpxSf8r"

        assertEquals(
            Secp256k1Lib().uncompressPublicKey(pubKeyBase64.base64UrlDecodedBytes).base64UrlEncoded,
            "BHza5mV6_Iz6XdyMpxpjUMprZUCN_MpMuQCTFYpxSf8rW7N7DD04troywCgLkg0_ABP-IcxZcE1-qKjwCWYTVO8"
        )
    }

    @Test
    fun testVerification2() {
        val pubKeyBase64 = "BD-l4lrQ6Go-oN5XtdpY6o5dyf2V2v5EbMAvRjVGJpE1gYVURJfxKMpNPnKlLr4MOLNVaYvBNOoy9L50E8jVx8Q"
        val signatureBase64 =
            "MEUCIQCFeGlhJrH-9R70X4JzrurWs52SwuxCnJ8ky6riFwMOrwIgT7zlLo7URMHW5tiMgG73IOw2Dm3XyLl1iqW1-t5NFWQ"
        val message = "Test"

        assertTrue {
            Secp256k1Lib().verify(
                pubKeyBase64.base64UrlDecodedBytes,
                signatureBase64.base64UrlDecodedBytes,
                message.encodeToByteArray()
            )
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun testSignatureNormalisationFromJS() {
        val signatureHex =
            "3045022100bca0478b0f400277626355d9f411bf0534afb1236758099c0c8ffdd761664a7202207e629a601f1bbe3e0df10899e62919c2a5c902f773120a2bddeb1cde14bae1fd"
        val publicKeyHex =
            "042e6d481b0b8761414166ea01431ed339d9150a58c2e521cb6cc8638988299b835acfc2cc1c7a7fd9ea54acc8060c37367be8ed7fb70c13203e702ef7a20d8b06"
        val dataHex = "4174616c61507269736d2057616c6c65742053444b"
        val publicKey = publicKeyHex.hexToByteArray()
        val signature = signatureHex.hexToByteArray()
        val data = dataHex.hexToByteArray()
        val valid = Secp256k1Lib().verify(publicKey, signature, data)
        assertEquals(valid, true)
    }

    @Test
    fun testJVMVerification() {
        val pubKeyBase64 = "BD-l4lrQ6Go-oN5XtdpY6o5dyf2V2v5EbMAvRjVGJpE1gYVURJfxKMpNPnKlLr4MOLNVaYvBNOoy9L50E8jVx8Q"
        val signatureBase64 =
            "MEUCIQCFeGlhJrH-9R70X4JzrurWs52SwuxCnJ8ky6riFwMOrwIgT7zlLo7URMHW5tiMgG73IOw2Dm3XyLl1iqW1-t5NFWQ"
        val message = "Test"
        assertTrue {
            Secp256k1Lib().verify(
                pubKeyBase64.base64UrlDecodedBytes,
                signatureBase64.base64UrlDecodedBytes,
                message.encodeToByteArray()
            )
        }
    }

    @Test
    fun testJVMVerificationBitcoin() {
        val ecPublicKeyBase64 =
            "BC7OYUnD57Qxel3-gyGuUIeicvRYhkFMOw9vsz70WMHzt8X8jiX358Jv9KYrMOHpkHE6jpb8CTvGabgIJUPkX_4"
        val messageBase64 =
            "eyJ0eXAiOiJKV1QiLCJhbGciOiJFUzI1NksifQ.eyJub25jZSI6IjQ3YmM5ZmMwLWVhODAtNDlmOC04OTcxLWJjYzY0MmJmZDNjMCIsImlzcyI6ImRpZDpwcmlzbTphZjJlNGJiOWU1MTRmODg5ZTdkNTY2MDZjNmYzZWVhYmNmMDgxZTc0ZTQ4NDMwN2Q3NTQ4Mzg0Y2ZiOTE4ZTdlOkNzY0JDc1FCRW1RS0QyRjFkR2hsYm5ScFkyRjBhVzl1TUJBRVFrOEtDWE5sWTNBeU5UWnJNUklnTHM1aFNjUG50REY2WGY2RElhNVFoNkp5OUZpR1FVdzdEMi16UHZSWXdmTWFJTGZGX0k0bDktZkNiX1NtS3pEaDZaQnhPbzZXX0FrN3htbTRDQ1ZENUZfLUVsd0tCMjFoYzNSbGNqQVFBVUpQQ2dselpXTndNalUyYXpFU0lDN09ZVW5ENTdReGVsMy1neUd1VUllaWN2Ulloa0ZNT3c5dnN6NzBXTUh6R2lDM3hmeU9KZmZud21fMHBpc3c0ZW1RY1RxT2x2d0pPOFpwdUFnbFEtUmZfZyIsInZwIjp7IkBjb250ZXh0IjpbImh0dHBzOlwvXC93d3cudzMub3JnXC8yMDE4XC9wcmVzZW50YXRpb25zXC92MSJdLCJ0eXBlIjpbIlZlcmlmaWFibGVQcmVzZW50YXRpb24iXX0sImF1ZCI6ImRvbWFpbiJ9"
        val rawSignatureBase64 =
            "ZGjNy5vyOaDCPfZRqpjbolcPZXD3WmM_VugjIgVhY2ANARaJ_PnNCnTFFYUgajzml8kIhyIPQsVOchQDQz1RMA"
        val swiftSignatureBase64 =
            "MEQCIGBjYQUiI-hWP2Na93BlD1ei25iqUfY9wqA58pvLzWhkAiAwUT1DAxRyTsVCDyKHCMmX5jxqIIUVxXQKzfn8iRYBDQ"
        val nimbusDerSignatureBase64 =
            "MEQCIGRozcub8jmgwj32UaqY26JXD2Vw91pjP1boIyIFYWNgAiANARaJ_PnNCnTFFYUgajzml8kIhyIPQsVOchQDQz1RMA=="

        val publicKey = ecPublicKeyBase64.base64UrlDecodedBytes
        val message = messageBase64.encodeToByteArray()

        assertTrue {
            Secp256k1Lib().verify(
                publicKey,
                rawSignatureBase64.base64UrlDecodedBytes,
                message
            )
        }

        assertTrue {
            Secp256k1Lib().verify(
                publicKey,
                swiftSignatureBase64.base64UrlDecodedBytes,
                message
            )
        }

        assertTrue {
            Secp256k1Lib().verify(
                publicKey,
                nimbusDerSignatureBase64.base64UrlDecodedBytes,
                message
            )
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun testVerificationPrismCrypto0() {
        val public0 = "025ec3069f260463ab79c6ada107de5ef43da1663eb4092d1718f5d26f57f2884b"
        val data0 = "33121a19313e349bafada4113e364a01d578007a02dc"
        val signature0 =
            "3046022100ce972f4df5ab2d6aa20151bd56d92f9db42a6b6e9bdbd78971ea80828e183683022100a30cef9d2d28bc1710cec2c1966eb1e5ac965d0be4774a60ba38a462d56c7e7c"

        assertTrue {
            Secp256k1Lib().verify(
                public0.hexToByteArray(),
                signature0.hexToByteArray(),
                data0.hexToByteArray()
            )
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun testVerificationPrismCrypto1() {
        val public1 = "02e2c22135cba30f2b4796626819c9fc7029ae3be3a924802078ff204f9fb263a9"
        val data1 = "15931303fc9afa09fd8ac1"
        val signature1 =
            "3046022100da8e9a5724b99de4afe938233530fa1c6c8363eb67f9408d53d4941a0ec79fbb02210086b6bba2687e3d412e6e33455c98250a04fb334ae5bca1f46bc76dba6115f726"

        assertTrue {
            Secp256k1Lib().verify(
                public1.hexToByteArray(),
                signature1.hexToByteArray(),
                data1.hexToByteArray()
            )
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun testVerificationPrismCrypto2() {
        val public2 = "03752cb21c3687c1a5a2d18fbb1557b97e6aa666a5af099bbbe4f629ea6d83b1e1"
        val data2 = "226d3d0df1f3c475684457bf656dd59e"
        val signature2 =
            "3044022019f198ca5d3f43d85d6621a15e560dcc9b79fe4009930d8dce03944f3bd79e2e0220516cbfa1f6917a14990a4c9d5b32eb86fc1cde62538f2e99ab95f6fd40919519"

        assertTrue {
            Secp256k1Lib().verify(
                public2.hexToByteArray(),
                signature2.hexToByteArray(),
                data2.hexToByteArray()
            )
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun testVerificationPrismCrypto3() {
        val public3 = "02ac19f9dd09a00a845b6133c2832da6a5308f0be86ca53ca2425d1876c3f568b2"
        val data3 = "19b6993da7739b086ca04cc4b3"
        val signature3 =
            "304502206bca4b8bd479a15cf23b768ab19577fbd87aca3e60ba0b4db3f6ea67080d0aa9022100f1e9fe9d8d236af74622edbffed5e2e6740531a7601e9e205cdf1769ac3ea7cd"

        assertTrue {
            Secp256k1Lib().verify(
                public3.hexToByteArray(),
                signature3.hexToByteArray(),
                data3.hexToByteArray()
            )
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun testVerificationPrismCrypto4() {
        val public4 = "035a9a596978d0777e2a840333636ee78cbd6861eaf73a724ca051820f312537bf"
        val data4 = "99d5dd82b45eaf61d0187d2a11"
        val signature4 =
            "304402207e1e98b08da0b914c7e5f91c54eb7a765c761cd725f7c118dd1210321b2a88a402204f12d0c5c9cdef8199e40b00840893f343088f216bb45e2d3c81a7cc24df55a4"

        assertTrue {
            Secp256k1Lib().verify(
                public4.hexToByteArray(),
                signature4.hexToByteArray(),
                data4.hexToByteArray()
            )
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun testVerificationPrismCrypto5() {
        val public5 = "024860f24369911e01cc475a88e5455c692c55976b870728c8a8b877b5df04669d"
        val data5 = "b53857e29f52275586"
        val signature5 =
            "3045022045b5fc308afe2cd9ef6f4e67e7f9a3ca9dfb4091fbbe951035cd5f55da6e3235022100b4ab20e19d55b8c235bb4e0ee4020dbbb58fab0185193b698cfe33a291c6a560"

        assertTrue {
            Secp256k1Lib().verify(
                public5.hexToByteArray(),
                signature5.hexToByteArray(),
                data5.hexToByteArray()
            )
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun testVerificationPrismCrypto6() {
        val public6 = "03c41f327bd37ede1c985ead9ef23f4bac37f4d6734ea8e376b8c6bf3147e28f91"
        val data6 = "10aef7c7acce95f49e7799ee0205"
        val signature6 =
            "30440220174473ba0c4cf1a055742bb42da5f1696086c06ac5f1004bbb12cac0ec11b81802200219e9cbeb5378600e269777fb33f4a0328dd8fbfa42352bf3284528cdbb0fcb"

        assertTrue {
            Secp256k1Lib().verify(
                public6.hexToByteArray(),
                signature6.hexToByteArray(),
                data6.hexToByteArray()
            )
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun testVerificationPrismCrypto7() {
        val public7 = "0265520a34189b61a9e02c711372ac685ca5facc154918bf74d1374e173bc6abf6"
        val data7 = "5a39a343131e1a4b0f69f0ec5f"
        val signature7 =
            "3045022100da58ad12cd4d18220eb18775e573560ec05ec0f459728f5d70603aee5f256ce2022017385e5895f341c1a5e5fe1035350c800d101a014dd8c3bdc2d19febdc6835bc"

        assertTrue {
            Secp256k1Lib().verify(
                public7.hexToByteArray(),
                signature7.hexToByteArray(),
                data7.hexToByteArray()
            )
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun testVerificationPrismCrypto8() {
        val public8 = "03bed6a8a0fd4b7af194608b96b8000adc6c0fb98e1b5cf5ca88c0fec71108127d"
        val data8 = "2f6e2230c4dedafe96aa422f9b5e"
        val signature8 =
            "3045022100e9d2f7876078c53db6488ba9a4dbacdba5f30341577c5427a1ef0b0ffcfd4c2b0220072f0705ef235617630b49dce2f75baf3a844ee3d88cdbcd21d89430f9a721c6"

        assertTrue {
            Secp256k1Lib().verify(
                public8.hexToByteArray(),
                signature8.hexToByteArray(),
                data8.hexToByteArray()
            )
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun testVerificationPrismCrypto9() {
        val public9 = "02cdb3a84f0704a842b38b3e70a9d68accd94e712a3e03fc4270e2000dfe1480d3"
        val data9 = "2ab555"
        val signature9 =
            "304502206dc9e71d3fa1612a77a118145fa031191ce913f0722152e73bc2c6ed663fac98022100db27302f229bd5584f66c14c9bade299a976cd740fadbc609fcfab1f1544cfd4"

        assertTrue {
            Secp256k1Lib().verify(
                public9.hexToByteArray(),
                signature9.hexToByteArray(),
                data9.hexToByteArray()
            )
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun testVerificationPrismCrypto10() {
        val public10 = "034eb34ae043352bf1f125244a3d534a5f6b98dbd82ae6146d74e8bc0e3edb9837"
        val data10 = "7bac7995678ce23b85074372c28bbba3dd3e013cce91a497b71e37744d43"
        val signature10 =
            "3045022025c3f5b4dca9c6dd5e1f143758ba3e071f9ce4c902b1df9b950ce422e4e352e7022100c340d8a19d5a4e71f3b35e8ac0366b49e31b884d6790d9df36d0562d925f5df1"

        assertTrue {
            Secp256k1Lib().verify(
                public10.hexToByteArray(),
                signature10.hexToByteArray(),
                data10.hexToByteArray()
            )
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun testVerificationPrismCrypto11() {
        val public11 = "02198d212d5f2738202828ac10ff174ec99f0fb6710dfb759b3afcf2bb211bbf0b"
        val data11 = "9cf3f6f8e6047c352d217b2cdffe7925495379b15c2f48"
        val signature11 =
            "304402202de1c2003449d5e2a3b2e1eaaddcb42cdf7abd630768ff737edcbb0e93a74d0102201df34e3f6cb1be6b7162b21cc294af6d73c339ff04c08c41598407f3e019ea4d"

        assertTrue {
            Secp256k1Lib().verify(
                public11.hexToByteArray(),
                signature11.hexToByteArray(),
                data11.hexToByteArray()
            )
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun testVerificationPrismCrypto12() {
        val public12 = "03fd09989559b8d2f37886c9b68ab1dc09ba7620ed7f40b39ac6c2ab9acfc8b290"
        val data12 = "8e"
        val signature12 =
            "304402203f64b6333f76151efaa17eec156a9eb410942afddbdb1e988154dc26c1e670ae0220494a0e2331aab2d7d9021cfb192da40de464591a0cfed9fa4ee1b89a62c9692a"

        assertTrue {
            Secp256k1Lib().verify(
                public12.hexToByteArray(),
                signature12.hexToByteArray(),
                data12.hexToByteArray()
            )
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun testVerificationPrismCrypto13() {
        val public13 = "02b5de6a254244e63a0031cdd0ec5c547d280fac45a9797490be91917ae6dd5760"
        val data13 = "af629bb4f46ea77424b071e4cd267bc02135c3013d7748842e6d"
        val signature13 =
            "3046022100a066f2c0a20ab561858c8bc5e58cb72e931ccece537716bca10003b8fdd1167e022100c5f4248f52cb39a7378c3c4d615970ca81a18590a2fdb8ba2f30a06a0ed61d61"

        assertTrue {
            Secp256k1Lib().verify(
                public13.hexToByteArray(),
                signature13.hexToByteArray(),
                data13.hexToByteArray()
            )
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun testVerificationPrismCrypto14() {
        val public14 = "03838435445b8dee456bddddedbae7401753b1bcaa99907f02d8d9002289f722fc"
        val data14 = "49d3ba81064512e9"
        val signature14 =
            "3046022100d0391d91d9f1a0269f94f1c38f418789452a9ed02ed29eafc1b23dd510d68b7c022100dca843653ade5a16492f80c9d54862c29fcdf7c58eaef085990f1b0caaca0987"

        assertTrue {
            Secp256k1Lib().verify(
                public14.hexToByteArray(),
                signature14.hexToByteArray(),
                data14.hexToByteArray()
            )
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun testVerificationPrismCrypto15() {
        val public15 = "034ef4bd6214d9f86a7393a671ad5ccb2a9e526af9aeec3c3cc303465edd973b0d"
        val data15 = "e2b1330ff2eaaf82f8364b727e1893b29956787d42e0301ba765efc259d7"
        val signature15 =
            "304402201f857c891047f7704639e5e44117f544a60c4978cab905a1560cfbc03c7f796602206b62b9c1a237d07a0c0fd700f807b67de9d271a49a9eb6bfedb39e0100a728e7"

        assertTrue {
            Secp256k1Lib().verify(
                public15.hexToByteArray(),
                signature15.hexToByteArray(),
                data15.hexToByteArray()
            )
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun testVerificationPrismCrypto16() {
        val public16 = "02de7e848977cf7409d5eeb6863d1cb8544d12de50f821ea844bcfad3a0af354d4"
        val data16 = "1eb1269f2b4d92fa9b704684b5c899b3cd2a"
        val signature16 =
            "3046022100b16bba6e401ff7c0945f864d471b0a42d186c0f992b66a37ec4aef0c02543bf4022100c4352d407b61bbacd9c8513deba0d05736a54e429465f884d9c390f846eed108"

        assertTrue {
            Secp256k1Lib().verify(
                public16.hexToByteArray(),
                signature16.hexToByteArray(),
                data16.hexToByteArray()
            )
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun testVerificationPrismCrypto17() {
        val public17 = "0302059fff5c903632fa9bd3e2a6138b02e0873a128ca03bbfd37efec403a5669f"
        val data17 = "a358696fa77d"
        val signature17 =
            "3044022072f1e5dbbcc1a866a4f9a856734d0739dc71b152cecdff0398854a0478d1faf1022024cd12e3b270dd71f266b26aba5c21e726ad99fc6c591ee53d1ace62a545ccb5"

        assertTrue {
            Secp256k1Lib().verify(
                public17.hexToByteArray(),
                signature17.hexToByteArray(),
                data17.hexToByteArray()
            )
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun testVerificationPrismCrypto18() {
        val public18 = "033f08fc5adeef183bbca59923a583eb91efac59cb169f4e7cb70bcc1bbee06fdc"
        val data18 = "c8"
        val signature18 =
            "3045022100faf186b545801a10c0c3b6860076a3c2c1ac3106b399ff6f926391d5f58db7590220220830b0c2fe368f5fd043e1109e9df11002ed92c9d5c317ddb46df0b508107a"

        assertTrue {
            Secp256k1Lib().verify(
                public18.hexToByteArray(),
                signature18.hexToByteArray(),
                data18.hexToByteArray()
            )
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun testVerificationPrismCrypto19() {
        val public19 = "0262ae6149f128e7b727252c9ca5df84b49770b70d39accadbddfeedb34e0a7a0e"
        val data19 = "8b3788243f3c98e44962bc03e5da3d1caca14708bf6d"
        val signature19 =
            "3045022100abc3048d145b7ee5a5a22348206b51bfc2716cb2c3e76337a217d628dc15656302200c6b85f099870234c110ff76dd40b42a80d6900a4636b843a58f99262f48d54c"

        assertTrue {
            Secp256k1Lib().verify(
                public19.hexToByteArray(),
                signature19.hexToByteArray(),
                data19.hexToByteArray()
            )
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun testVerificationPrismCrypto20() {
        val public20 = "03b722749ca5e7b3face8cbce50fa3096a0bb48b714c23330a619688d3ef2685e5"
        val data20 = "c2e083c32b6b8de993e65e00f438098af54278f6ede84709483c60b46a71"
        val signature20 =
            "30440220516a705c5d42ba5858330dc8911248096ddec0df889cb6b55e0f082d818960a6022030939109a1db738090467e726997f8d90db2718e3be60c431608ebf556712eff"

        assertTrue {
            Secp256k1Lib().verify(
                public20.hexToByteArray(),
                signature20.hexToByteArray(),
                data20.hexToByteArray()
            )
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun testVerificationPrismCrypto21() {
        val public21 = "03d8a2da096adac81bafcfbae8c8b7b38bdd319410899961e561e9cace7caed7eb"
        val data21 = "d662ea17e73481ae85ccbc01704d36f13680"
        val signature21 =
            "3046022100a9af9a342bfa10221c6de02805fcc61217b212760e12de6c459c22ba0fe932a0022100fee33be31c2bf26d3b918f37ccebf8e457acb7882feecb65b59ef7d2bf3d1da2"

        assertTrue {
            Secp256k1Lib().verify(
                public21.hexToByteArray(),
                signature21.hexToByteArray(),
                data21.hexToByteArray()
            )
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun testVerificationPrismCrypto22() {
        val public22 = "0374fc049f4b4b4fe8e17f02742989f3d6555b0c230d566ac69effa44f99d32267"
        val data22 = "3799b0dedc31bded83878b11fdeb10"
        val signature22 =
            "3044022023fc95d7283b96fdedfe7a7c1073f0d5f4a0ce4fb3ca4940c2a6d47ca2236033022033201ce70e10de6a01f4a12d36e92ae62d0b25a7a40da5d32f879c116ba0c2d2"

        assertTrue {
            Secp256k1Lib().verify(
                public22.hexToByteArray(),
                signature22.hexToByteArray(),
                data22.hexToByteArray()
            )
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun testVerificationPrismCrypto23() {
        val public23 = "02af493b8a7940f7f1580c2b5fed5a90bb0c131ca4eee3a453416f46d21ad60bf6"
        val data23 = "3343bd7649655d2cacd889e4fb620b0a386f43925dae"
        val signature23 =
            "3046022100d08b2d1d37c625f1f8022d38ef13ed78694dd0cc3cb19e1017bf8f39f92961a2022100ae60e66603a11487bd8d1c314fd47ac91b292f251e9ba33f825c0dc496924be4"

        assertTrue {
            Secp256k1Lib().verify(
                public23.hexToByteArray(),
                signature23.hexToByteArray(),
                data23.hexToByteArray()
            )
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun testVerificationPrismCrypto24() {
        val public24 = "02327d44e810121b390fbec18ef203acb0975be6b7961b6597be6cccf52e105513"
        val data24 = "4a516c6ae3bd5d5f5811585572a4b07d8e93"
        val signature24 =
            "304502202114844a03bf832640487acdb62bb7730eec150be51447593f7a66224b04ce5f022100cc7f2501c2a75d71f289c73607dd828a89b4f5e11f19589e0fe184b2eff22947"

        assertTrue {
            Secp256k1Lib().verify(
                public24.hexToByteArray(),
                signature24.hexToByteArray(),
                data24.hexToByteArray()
            )
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun testVerificationPrismCrypto25() {
        val public25 = "03407ef29f05e5bc26220ae32d0d7fda2ef94d9a2f001744a0362765626faede2b"
        val data25 = "43eb42d1ab3ac46fc933e21d677c5b228ff3f3fcb6"
        val signature25 =
            "3046022100fb32798e1a023fa2e8bdcc8a58770372a79cc0566c354f45a8520265dd58cf34022100f4eaab9354f7d80f1515531b5a1cb3a3b722f096f24dde0331899a37b77fe85e"

        assertTrue {
            Secp256k1Lib().verify(
                public25.hexToByteArray(),
                signature25.hexToByteArray(),
                data25.hexToByteArray()
            )
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun testVerificationPrismCrypto26() {
        val public26 = "03ef0b5f7b167c770e53c26f9ce0593e41168e307503f418d41a98948a1b88f16b"
        val data26 = "98dfc7b2473392057c6020dcc4b2ef7c68474b5788bb555de693a5"
        val signature26 =
            "3046022100fc4252d4cdbe4e0470f8fc29bb7d32d80713f582b0e1a5d338c8b03de0d7a0da0221008d56beea3b68b9b65d2cb19ecd856d057d0c60dfb732e554d4dd31303e064158"

        assertTrue {
            Secp256k1Lib().verify(
                public26.hexToByteArray(),
                signature26.hexToByteArray(),
                data26.hexToByteArray()
            )
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun testVerificationPrismCrypto27() {
        val public27 = "021ff1182f20929374355b6a35f2a4707bb78a052ef6d134fb9ce063979c72f0e8"
        val data27 = "1159598e30cff4"
        val signature27 =
            "3046022100bdf6db8ea1108b7027f12e33bc02f2caf56afc206c09efa3a94df038088ed786022100d22298a6e2cd64a8f12dcd35382273525725f583b7fe3d05f33003238c7e5e8f"

        assertTrue {
            Secp256k1Lib().verify(
                public27.hexToByteArray(),
                signature27.hexToByteArray(),
                data27.hexToByteArray()
            )
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun testVerificationPrismCrypto28() {
        val public28 = "031776f58bb1626a7f7d17f19c9ac861226e907aba53fe6661a242e69594de85d4"
        val data28 = "f269dc1dd8e5983ad4734d6757a422099e"
        val signature28 =
            "3045022100e6c630074b4913e40db01b6df329400abb1ae905ce392bd2c2517633ce217a33022025c41a1762d96a3572ceb8a262af1081330ff897953de31d6eab18adfea5b9d9"

        assertTrue {
            Secp256k1Lib().verify(
                public28.hexToByteArray(),
                signature28.hexToByteArray(),
                data28.hexToByteArray()
            )
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun testVerificationPrismCrypto29() {
        val public29 = "03df832c689a34534a499ea817a8f28a17b8761eeed3cedf795d240013bef115ce"
        val data29 = "f89d62a52aab9e30609884676821"
        val signature29 =
            "304402207731ab999e07d14431a0e42ef1274607a7a0eb888a883568ef8d0825c933d75702202ae4096abbc2a4fd079ea66d14095051332304ee721d2c2b357de723abe0914d"

        assertTrue {
            Secp256k1Lib().verify(
                public29.hexToByteArray(),
                signature29.hexToByteArray(),
                data29.hexToByteArray()
            )
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun testVerificationPrismCrypto30() {
        val public30 = "020b78dcdbdf7a67ef85131ecee3b89ae3eb285cff1efd153806fd216caa0bc26a"
        val data30 = "c2ce3f"
        val signature30 =
            "3045022056d2de55da1f5c2758d3dd0e9647a34e479ac0e61343e6f912e3669f078f9daa022100f212f759ac98d142ee8493f87d7a4d5785dccf252add8b372be3f32623200f18"

        assertTrue {
            Secp256k1Lib().verify(
                public30.hexToByteArray(),
                signature30.hexToByteArray(),
                data30.hexToByteArray()
            )
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun testVerificationPrismCrypto31() {
        val public31 = "025e4730a5c0921731987e18d504ab0ebe17e0c788407611d4022a0ffc718ce404"
        val data31 = "ddd82c334e27e6c0999589"
        val signature31 =
            "30450221009eb04a98b50fb3419ad7d5a429e23be06905b93834daa1b141b543700d8c012a02203dfbd110655ab5f5776ddd686bc7a1482c0c98ec87ea7c81efc6a4afd4a4c74e"

        assertTrue {
            Secp256k1Lib().verify(
                public31.hexToByteArray(),
                signature31.hexToByteArray(),
                data31.hexToByteArray()
            )
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun testVerificationPrismCrypto32() {
        val public32 = "02809c15e681190bab0e955877e839cb8a0011c2dc696617299c0b63a43c437053"
        val data32 = "10"
        val signature32 =
            "3045022072aefed285b9450262364a64762de0999344e53b67d6763878e050e108e76344022100cc4bb5949fe5a333ccc42a901b2e70ebb80cba68c6de5a8f1108595282e4106a"

        assertTrue {
            Secp256k1Lib().verify(
                public32.hexToByteArray(),
                signature32.hexToByteArray(),
                data32.hexToByteArray()
            )
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun testVerificationPrismCrypto33() {
        val public33 = "020d22e7de2551bc343277246f6cfc73c31dc21ff33ea8b7cfb185b560908a1d47"
        val data33 = "c1ff9b49291dd5cc3e627a7e147ebd7a08e2ac57cb"
        val signature33 =
            "30440220788f775677c063bcc22a3271bdd3a9a423584b87dab4c52f881c2d7df36f89fd02201a5edfd0a0efa06503eba4351f1decc1b5dce15ef4d5869aef76f5e4ea305148"

        assertTrue {
            Secp256k1Lib().verify(
                public33.hexToByteArray(),
                signature33.hexToByteArray(),
                data33.hexToByteArray()
            )
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun testVerificationPrismCrypto34() {
        val public34 = "02a5196e147210027dc644544b45df692b2643e16a6ff5bb3d0a42c0a1a23cdc81"
        val data34 = "22e96f58574f191b754fff1501ad6a71e2f2c4745ab4d98903800bb3d8ab6c"
        val signature34 =
            "3046022100a5e8e79f368d683f952bea8627a3fbe411e110b2fab42bfe507300c163d8813f022100d5f68e36d3cd37b6ba16c493f988e3f33c0e48d60d9459f33c6d60ba84460862"

        assertTrue {
            Secp256k1Lib().verify(
                public34.hexToByteArray(),
                signature34.hexToByteArray(),
                data34.hexToByteArray()
            )
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun testVerificationPrismCrypto35() {
        val public35 = "033fca0e582fca02c7c4afe97ce582592e978ad85fc4f7e75534a6f841c5d9f893"
        val data35 = "6589a9a397d4b88af7422dba8196959595bedd77f1e700c8ba9121"
        val signature35 =
            "30450220044df23de60f24aba40d39c72ed5cf37021408ea0ce316e072c9aaacb39b8c13022100cffb8a2889e60918caa3c065aa04ecf56d5ade73a44f7b845046bb5ffd185b0c"

        assertTrue {
            Secp256k1Lib().verify(
                public35.hexToByteArray(),
                signature35.hexToByteArray(),
                data35.hexToByteArray()
            )
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun testVerificationPrismCrypto36() {
        val public36 = "0214a6c938517c424018f5c0538f14c220bf48e79c4d4bfd36ae73a6f600c60074"
        val data36 = "3646f36c04d0fe01f66a9521728c"
        val signature36 =
            "304402202411573b0656b90bad369ea05b69866b0b7834adff9f1726af4e99ff3f67a6ba0220511eea49cbd6ebd3e57f7d8b30a1174c1ce4355d42955169a8be076efaac3f89"

        assertTrue {
            Secp256k1Lib().verify(
                public36.hexToByteArray(),
                signature36.hexToByteArray(),
                data36.hexToByteArray()
            )
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun testVerificationPrismCrypto37() {
        val public37 = "024df3c641960d555b130cff5db8a7bc51acf4f16434b75462efc2582b3dd4be85"
        val data37 = "c517f53b392c1bc83606376bf492517c4502d99955458b5f652dd81794"
        val signature37 =
            "3045022100b70a2586b76b56c45712eae944e419822b0ece808e884b849613fceaf29e746a02203e3fa765df4eb70e8e625e72f3d35561d96a8e2e07579bc6e7adcd7f4a37fefd"

        assertTrue {
            Secp256k1Lib().verify(
                public37.hexToByteArray(),
                signature37.hexToByteArray(),
                data37.hexToByteArray()
            )
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun testVerificationPrismCrypto38() {
        val public38 = "0242c4aa466b5d222f3d0b0de78156ab2c9082b201d06fee5180bc56231ab33d94"
        val data38 = "1f34f679ad6d144d302af05e79d7205074f8"
        val signature38 =
            "304502201a5a1cb22e8a3b68d4c04417412c75f1f5d48e25f2a85ae7449324f51afdcf79022100ea1b26ed808af0ec4f73286c5d1a4ef6f45ca0ebd78c3bbfc18b2745e65d35cd"

        assertTrue {
            Secp256k1Lib().verify(
                public38.hexToByteArray(),
                signature38.hexToByteArray(),
                data38.hexToByteArray()
            )
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun testVerificationPrismCrypto39() {
        val public39 = "035d7de2b10ca92de612138a8cbd7244d50afaae55003ff6e19b408ac4ccbe5dd3"
        val data39 = "970ffecfff8ddb75"
        val signature39 =
            "3045022016d9c1b9bd23339ee8ba4d5b17a92b9a8522a03f72e59c241584419e9ec5ad170221009b19f711f3f7c0a8c7a9f743079e3a01ef5005eccf57a7b9f77f1a86c31e4924"

        assertTrue {
            Secp256k1Lib().verify(
                public39.hexToByteArray(),
                signature39.hexToByteArray(),
                data39.hexToByteArray()
            )
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun testVerificationPrismCrypto40() {
        val public40 = "02c11800de3ebcf9c542049b105cec5c049b620af3c0223c9441b2804404d48922"
        val data40 = "4d7055505e3c"
        val signature40 =
            "304402204d7b2c809c4f029e2d6b8bc8b90778bb0f42b2d5ab741f9317c047e4ff7e7ad102206b7da13955d3bc034eef4e97f9e97d43e7374be6fb44c0668e5d0c7a943edf71"

        assertTrue {
            Secp256k1Lib().verify(
                public40.hexToByteArray(),
                signature40.hexToByteArray(),
                data40.hexToByteArray()
            )
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun testVerificationPrismCrypto41() {
        val public41 = "0205fe2ab3963918fb728f06c76413e0ac08fe352b7e0c32d6e0861fa79b764ed1"
        val data41 = "32ed02f0235c1422044afa2d6844c5e0235f380aba033b"
        val signature41 =
            "304402201b323994477164a267fecb69ffd6a3ea63ee3c01648bdd639e224ece2ac3f51202206e38a3c8239114dff4dc0a6e1ac0717657a362a410e98984461661c78b435cd0"

        assertTrue {
            Secp256k1Lib().verify(
                public41.hexToByteArray(),
                signature41.hexToByteArray(),
                data41.hexToByteArray()
            )
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun testVerificationPrismCrypto42() {
        val public42 = "034e2ad47b1ab64f27e5caf914c4c789da94cdaf51ab74487d9a6a8d640de614c1"
        val data42 = "11fcf2f5751ba64bd83be54514"
        val signature42 =
            "30440220680ee3489736f3494bd11c944d51757e137b60955fed3934c6b214ef5a7c6b0c02201c79f1da2d0333442d8998d49e12aba9d3072101fe42b0b35de58b07bfa0b796"

        assertTrue {
            Secp256k1Lib().verify(
                public42.hexToByteArray(),
                signature42.hexToByteArray(),
                data42.hexToByteArray()
            )
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun testVerificationPrismCrypto43() {
        val public43 = "03a3d3642f4a218278a5a22b6c59c5c6551e0afc1efbe3c5be2d60f3bb07653390"
        val data43 = "2b8193708c31"
        val signature43 =
            "3045022076de9c7e5234ffa158662186f48aaf308b5cb10b55c0e6172e4f34d0429c9ca70221009fbe8b7d126e1c787828ddc51dfc7b8243dfc5ea877c7339d5212dd50fa84cc3"

        assertTrue {
            Secp256k1Lib().verify(
                public43.hexToByteArray(),
                signature43.hexToByteArray(),
                data43.hexToByteArray()
            )
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun testVerificationPrismCrypto44() {
        val public44 = "02502991121053f932ca83629b90d0e6428300361a3db40e473f624af2db013b25"
        val data44 = "156092baa666ff43615f96782797ce41d1c520"
        val signature44 =
            "3044022039ae7f6ae0f567e337d97575a630803447842f478c2cfec005b6c4b86777703602202ad47c286258141d10ebbbef3547ed465bcb9dd42223c18cfd3351d57555a0d3"

        assertTrue {
            Secp256k1Lib().verify(
                public44.hexToByteArray(),
                signature44.hexToByteArray(),
                data44.hexToByteArray()
            )
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun testVerificationPrismCrypto45() {
        val public45 = "0252f1f61f2ed6c0dc2a2dd8dc477e8c37a35eab6d14c94238e1ac6eb1774ead4f"
        val data45 = "d1214a30fa539b1c49dd385b32"
        val signature45 =
            "3046022100b9c0756e6ccdbbda4f4d9853362c638c88c6e1d1399c057980c1709ed89f644d02210082bba757ecb29d6f3d4703c718389646c4d176eb6fb33e1f6b04399428c00d6d"

        assertTrue {
            Secp256k1Lib().verify(
                public45.hexToByteArray(),
                signature45.hexToByteArray(),
                data45.hexToByteArray()
            )
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun testVerificationPrismCrypto46() {
        val public46 = "03e79fed143dec70d18ce101015d4402fa3fa70bced3892854427a1df646142ade"
        val data46 = "3daecf8a4ce732110da3ec0789f5cfd8c955fd153003e48d"
        val signature46 =
            "304502206d164be29709cd2cef1d0d7d7f216cfd16d48ff1fb0cd25a39d2d8061a2e2a420221009dd261934dc194c865a2fcf22d7e2b7e80b403cd5793d79bbda7f1eefd0009bb"

        assertTrue {
            Secp256k1Lib().verify(
                public46.hexToByteArray(),
                signature46.hexToByteArray(),
                data46.hexToByteArray()
            )
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun testVerificationPrismCrypto47() {
        val public47 = "03d6915116866f308b9c386ef9065a9a3be3d84f860ee6910cda32a070403bf12a"
        val data47 = "05e7f6e57ee6fc59"
        val signature47 =
            "3044022022b387bc6bd817ba8b7678a2f2d7407863f7270d670f34563904fe270b64490602201475444356d3a96969a66d3fb3d22c0b6666d7443f58ec2f57d9bfa8863c097d"

        assertTrue {
            Secp256k1Lib().verify(
                public47.hexToByteArray(),
                signature47.hexToByteArray(),
                data47.hexToByteArray()
            )
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun testVerificationPrismCrypto48() {
        val public48 = "034be48a452a2399542306cef31ed61d69136cf71af5b8aced080c213fceb4c7e3"
        val data48 = "ea9dec5729710760ab767790dff3"
        val signature48 =
            "304502210099a7d8dbf0b610e7a4c4acb5a7bf432fd4cdd79cabb8de369194a4074080604702201f6d3e45d7765be344b622f53495821c14dd5150d3bbff7b4b37241ec49a8821"

        assertTrue {
            Secp256k1Lib().verify(
                public48.hexToByteArray(),
                signature48.hexToByteArray(),
                data48.hexToByteArray()
            )
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun testVerificationPrismCrypto49() {
        val public49 = "024db5e63fb022543e378be3433e8d3300be01a7dc1b13d41e41cf954e6828caa8"
        val data49 = "a70a915cb32710d63aec0c3b2c2f49c219775dc4ec9c"
        val signature49 =
            "3045022016e85fc22a190b455b5226d1c08ad0f215896a7c2b2db0ed8eedf9c35ae13479022100faf8aade3145665e3e51945ab8a24d81974a2aa1af6d344d9925c434290c7107"

        assertTrue {
            Secp256k1Lib().verify(
                public49.hexToByteArray(),
                signature49.hexToByteArray(),
                data49.hexToByteArray()
            )
        }
    }
}

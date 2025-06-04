package org.hyperledger.identus.apollo.utils

import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.allocArrayOf
import kotlinx.cinterop.convert
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.Foundation.NSNumber
import platform.Foundation.NSString
import platform.Foundation.create
import platform.Foundation.numberWithInt
import platform.darwin.NSUInteger
import platform.posix.memcpy

/**
 * Converts a ByteArray to an NSData object.
 *
 * @return The converted NSData object.
 */
@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
fun ByteArray.toNSData(): NSData =
    memScoped {
        NSData.create(
            bytes = allocArrayOf(this@toNSData),
            length = this@toNSData.size.convert<NSUInteger>()
        )
    }

/**
 * Converts an NSData object to a ByteArray.
 *
 * @return The ByteArray representation of the NSData object.
 */
@OptIn(ExperimentalForeignApi::class)
fun NSData.toByteArray(): ByteArray =
    ByteArray(this@toByteArray.length.toInt()).apply {
        if (this@toByteArray.length > 0U) {
            usePinned {
                memcpy(it.addressOf(0), this@toByteArray.bytes, this@toByteArray.length)
            }
        }
    }

/**
 * Converts an integer to an NSNumber object.
 *
 * @return The converted NSNumber object.
 */
@OptIn(ExperimentalForeignApi::class)
fun Int.toNSNumber(): NSNumber =
    memScoped {
        return NSNumber.numberWithInt(this@toNSNumber)
    }

/**
 * Converts an NSNumber to a Kotlin Int.
 *
 * @return The converted Kotlin Int value.
 */
fun NSNumber.toKotlinInt(): Int {
    return this.intValue
}

/**
 * Converts a Kotlin String to an NSString object.
 *
 * @return The converted NSString object.
 */
@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
fun String.toNSString(): NSString =
    memScoped {
        return NSString.create(string = this@toNSString)
    }

/**
 * Converts an NSString to a Kotlin String.
 *
 * @return The converted Kotlin String.
 */
fun NSString.toKotlinString(): String {
    return this.toString()
}

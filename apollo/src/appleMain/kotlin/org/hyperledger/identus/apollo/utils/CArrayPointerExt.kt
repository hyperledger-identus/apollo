package org.hyperledger.identus.apollo.utils

import kotlinx.cinterop.CArrayPointer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.MemScope
import kotlinx.cinterop.UByteVar
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.get
import kotlinx.cinterop.set

/**
 * Converts the elements of the CArrayPointer to a UByteArray of specified length.
 *
 * @param length The length of the UByteArray.
 * @return The converted UByteArray.
 */
@OptIn(ExperimentalUnsignedTypes::class, ExperimentalForeignApi::class)
fun CArrayPointer<UByteVar>.toUByteArray(length: Int): UByteArray =
    UByteArray(length) {
        this[it]
    }

/**
 * Converts a UByteArray to a CArrayPointer.
 *
 * @param memScope The memory scope to allocate the CArrayPointer.
 * @return The CArrayPointer containing the converted UByteArray.
 */
@OptIn(ExperimentalUnsignedTypes::class, ExperimentalForeignApi::class)
fun UByteArray.toCArrayPointer(memScope: MemScope): CArrayPointer<UByteVar> {
    val array = memScope.allocArray<UByteVar>(this.size)
    val arrayPtr = array.getPointer(memScope)
    for (i in this.indices) arrayPtr[i] = this[i]
    return array
}

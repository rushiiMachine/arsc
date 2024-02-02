package dev.rushii.arsc.internal

/**
 * Shift this value left by the [bitCount] number of bits.
 * Based on a copy of [UInt.shl]
 */
@Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")
internal infix fun UShort.shl(bitCount: Int): UShort = UShort((data.toUInt() shl bitCount).toShort())

@file:Suppress("NOTHING_TO_INLINE")

package com.github.diamondminer88.arsc.internal

import java.nio.ByteBuffer

internal inline fun ByteBuffer.readU8(): UByte = get().toUByte()
internal inline fun ByteBuffer.readU16(): UShort = getShort().toUShort()
internal inline fun ByteBuffer.readU32(): UInt = getInt().toUInt()

internal fun ByteBuffer.putNullBytes(amount: Int) {
	for (i in 0..<amount)
		put(0)
}

/**
 * Read null-terminated string with utf16 encoding
 * ## Warning:
 * This function always reads `SIZE * 2` bytes
 */
internal fun ByteBuffer.readStringUtf16(size: Int): String {
	val bytes = ByteArray(size * 2)
		.also { get(it, 0, size * 2) }

	val stringBytesSize = run {
		for (i in 0..(bytes.size - 3)) {
			if (
				bytes[i] == 0.toByte() &&
				bytes[i + 1] == 0.toByte() &&
				bytes[i + 2] == 0.toByte()
			) {
				return@run i + 1
			}
		}

		return@run bytes.size
	}

	val trimmedBytes = bytes
		.take(stringBytesSize)
		.toByteArray()

	return String(trimmedBytes, Charsets.UTF_16LE)
}

internal fun ByteBuffer.putStringUtf16(string: String, outSize: Int) {
	val bytes = string.toByteArray(Charsets.UTF_16LE)
	val fillCount = 128 - bytes.size

	if (fillCount < 0)
		throw IllegalArgumentException("outSize is smaller than utf-16le bytes of string")

	println("putStringUtf16: $bytes")

	put(bytes)
	putNullBytes(fillCount)
}

/**
 * Shift this value left by the [bitCount] number of bits.
 * Based on a copy of [UInt.shl]
 */
@Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")
internal infix fun UShort.shl(bitCount: Int): UShort = UShort((data.toUInt() shl bitCount).toShort())

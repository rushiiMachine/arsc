@file:Suppress("NOTHING_TO_INLINE")

package dev.rushii.arsc.internal

import dev.rushii.arsc.ArscError
import java.nio.ByteBuffer

internal inline fun ByteBuffer.readU8(): UByte = get().toUByte()
internal inline fun ByteBuffer.readU16(): UShort = getShort().toUShort()
internal inline fun ByteBuffer.readU32(): UInt = getInt().toUInt()

/**
 * If not currently aligned to the specified byte boundary, then moves the
 * buffer pointer to the next index that is aligned based on the start of the buffer.
 *
 * @throws ArscError If not enough bytes exist in order to align to the next boundary.
 */
internal fun ByteBuffer.align(alignment: Int) {
	val pos = position()
	val remaining = pos % alignment

	if (remaining > 0) {
		val target = pos + alignment - remaining

		if (target > limit()) {
			throw ArscError(pos, null, "not enough bytes to align to the next $alignment byte boundary")
		}

		position(target)
	}
}

/**
 * If not currently already at a specific byte boundary,
 * then write extra null bytes until the cursor is aligned.
 */
internal fun ByteBuffer.writeAlignment(alignment: Int) {
	val pos = position()
	val remaining = pos % alignment
	val target = alignment - remaining

	if (remaining <= 0) return
	if (pos + target > limit())
		throw ArscError(pos, null, "buffer limit too small to write alignment")

	putNullBytes(target)
}

/**
 * Write a certain amount of null bytes
 */
internal fun ByteBuffer.putNullBytes(amount: Int) {
	for (i in 0..<amount)
		put(0)
}

/**
 * Read null-terminated string with UTF16LE encoding.
 * @param charSize The char count of this string. Note that the amount of bytes read is `charSize * 2`
 */
internal fun ByteBuffer.readStringUtf16(charSize: Int): String {
	val bytes = ByteArray(charSize * 2)
		.also { get(it, 0, charSize * 2) }

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

// TODO: UTF16LE string writer
internal fun ByteBuffer.putStringUtf16(string: String, outSize: Int) {
	val bytes = string.toByteArray(Charsets.UTF_16LE)
	val fillCount = 128 - bytes.size

	if (fillCount < 0)
		throw IllegalArgumentException("outSize is smaller than utf-16le bytes of string")

	println("putStringUtf16: $bytes")

	put(bytes)
	putNullBytes(fillCount)
}

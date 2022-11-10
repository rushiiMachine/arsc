package com.github.diamondminer88.arsc.internal

import java.nio.ByteBuffer

/**
 * Read null-terminated string with utf16 encoding
 * ## Warning:
 * This function always reads `SIZE * 2` bytes
 */
internal fun ByteBuffer.readStringUtf16(size: Int): String {
	val end = position() + size * 2
	val bytes = ByteArray(size).also { get(it, 0, size) }
		.takeWhile { it != (0).toByte() }
		.toByteArray()
	val string = String(bytes, Charsets.UTF_16)

	position(end)
	return string
}

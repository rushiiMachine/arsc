package dev.rushii.arsc

import java.nio.ByteBuffer

fun bytes(vararg bytes: Byte): ByteBuffer {
	return ByteBuffer.wrap(byteArrayOf(*bytes))
}

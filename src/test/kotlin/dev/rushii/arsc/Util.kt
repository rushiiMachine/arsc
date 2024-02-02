package dev.rushii.arsc

import java.nio.ByteBuffer

fun bytes(vararg bytes: Byte): ByteBuffer {
	return ByteBuffer.wrap(byteArrayOf(*bytes))
}

/**
 * Initialize a [ByteBuffer] with a backing array of nulls and the cursor moved to the end.
 * @param count The amount of bytes to skip forward.
 * @param capacity The total size of the buffer.
 */
fun nullBytesEnd(count: Int, capacity: Int = count): ByteBuffer {
	assert(capacity >= count)

	return ByteBuffer
		.wrap(ByteArray(capacity))
		.position(count)
}

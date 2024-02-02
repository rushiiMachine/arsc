package dev.rushii.arsc

import java.nio.ByteBuffer
import kotlin.test.DefaultAsserter.assertTrue

/**
 * Initialize a [ByteBuffer] with the bytes specified by [bytes].
 * @param cursorToEnd If true, then move the cursor position to the end of the buffer.
 */
fun bytes(vararg bytes: Byte, cursorToEnd: Boolean = false): ByteBuffer {
	return ByteBuffer.wrap(bytes)
		.apply { if (cursorToEnd) position(bytes.size) }
}

/**
 * Initialize a [ByteBuffer] with a backing array of nulls and the cursor moved to the end.
 * @param cursorPos The amount of bytes to skip forward.
 * @param capacity The total size of the buffer.
 */
fun fillBytes(cursorPos: Int, capacity: Int = cursorPos, fillValue: Byte = 0): ByteBuffer {
	assert(capacity >= cursorPos)

	return ByteBuffer
		.wrap(ByteArray(capacity) { fillValue })
		.position(cursorPos)
}

fun ByteArray.toHexString(separator: String = "") = joinToString(separator) {
	Integer.toUnsignedString(java.lang.Byte.toUnsignedInt(it), 16).padStart(2, '0')
}

fun assertBuffersEqual(expected: ByteBuffer, actual: ByteBuffer) {
	val expectedArr = expected.array()
	val actualArr = actual.array()

	assertTrue(
		{
			"Expected <$expected (${expectedArr.toHexString(", ")})>, " +
				"actual <$actual (${actualArr.toHexString(", ")})>."
		},
		expected == actual && expectedArr contentEquals actualArr,
	)
}

package dev.rushii.arsc

import dev.rushii.arsc.internal.putNullBytes
import dev.rushii.arsc.internal.writeAlignment
import org.junit.jupiter.api.assertThrows
import kotlin.test.Test

class WriteAlignmentTest {
	@Test
	fun `don't align when empty`() {
		val targetBytes = fillBytes(0, 4, 0x7F)
		val alignedBytes = fillBytes(0, 4, 0x7F)

		assertBuffersEqual(targetBytes, alignedBytes)
	}

	@Test
	fun `don't align when aligned`() {
		val targetBytes = fillBytes(4, 4, 0x7F)
		val alignedBytes = fillBytes(4, 4, 0x7F)
			.apply { writeAlignment(4) }

		assertBuffersEqual(targetBytes, alignedBytes)
	}

	@Test
	fun `fail to align when no more capacity`() {
		val targetBytes = fillBytes(1, 1, 0x7F)
		val alignedBytes = fillBytes(1, 1, 0x7F)

		assertThrows<ArscError> { targetBytes.writeAlignment(4) }
		assertBuffersEqual(targetBytes, alignedBytes)
	}

	@Test
	fun `align when extra byte`() {
		val targetBytes = bytes(0x7F, 0x00, 0x00, 0x00, cursorToEnd = true)
		val bytes = fillBytes(1, 4, 0x7F)
			.apply { writeAlignment(4) }

		assertBuffersEqual(targetBytes, bytes)
	}

	@Test
	fun `align when byte missing`() {
		val targetBytes = bytes(0x7F, 0x7F, 0x7F, 0x00, cursorToEnd = true)
		val bytes = fillBytes(3, 4, 0x7F)
			.apply { writeAlignment(4) }

		assertBuffersEqual(targetBytes, bytes)
	}

	@Test
	fun `align to different value`() {
		val targetBytes = fillBytes(5, 32, 0x7F)
			.apply { putNullBytes(11) }
		val bytes = fillBytes(5, 32, 0x7F)
			.apply { writeAlignment(16) }

		assertBuffersEqual(targetBytes, bytes)
	}
}

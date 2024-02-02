package dev.rushii.arsc

import dev.rushii.arsc.internal.align
import org.junit.jupiter.api.assertThrows
import kotlin.test.Test
import kotlin.test.assertEquals

class PositionAlignmentTest {
	@Test
	fun `don't align when empty`() {
		val bytes = nullBytesEnd(0, 8192)
			.apply { align(4) }

		assertEquals(bytes.position(), 0, "should not align with no bytes")
	}

	@Test
	fun `don't align when aligned`() {
		val bytes = nullBytesEnd(4, 8192)
			.apply { align(4) }

		assertEquals(bytes.position(), 4, "should not align when already aligned")
	}

	@Test
	fun `align when extra 1`() {
		val bytes = nullBytesEnd(1, 8192)
			.apply { align(4) }

		assertEquals(bytes.position(), 4, "should align to next boundary")
	}

	@Test
	fun `align when 1 missing`() {
		val bytes = nullBytesEnd(3, 8192)
			.apply { align(4) }

		assertEquals(bytes.position(), 4, "should align to next boundary")
	}

	@Test
	fun `align to different value`() {
		val bytes = nullBytesEnd(2005, 8192)
			.apply { align(4096) }

		assertEquals(bytes.position(), 4096, "should align to next boundary")
	}

	@Test
	fun `fail to align when no more capacity`() {
		val bytes = nullBytesEnd(1, 1)

		assertThrows<ArscError>("fail to align when no more capacity") {
			bytes.align(4)
		}
	}
}

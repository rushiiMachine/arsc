package dev.rushii.arsc

import dev.rushii.arsc.internal.ArscHeaderType
import org.junit.jupiter.api.assertThrows
import java.nio.ByteBuffer
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Parsing/writing tests for [ArscHeaderType]
 */
class HeaderTypeTest {
	@Test
	fun `string pool test`() {
		val buffer = bytes(0x00, 0x01)
		assertEquals(ArscHeaderType.StringPool, ArscHeaderType.parse(buffer))
	}

	@Test
	fun `table test`() {
		val buffer = bytes(0x00, 0x02)
		assertEquals(ArscHeaderType.Table, ArscHeaderType.parse(buffer))
	}

	@Test
	fun `table package test`() {
		val buffer = bytes(0x02, 0x00)
		assertEquals(ArscHeaderType.TablePackage, ArscHeaderType.parse(buffer))
	}

	@Test
	fun `table type test`() {
		val buffer = bytes(0x02, 0x01)
		assertEquals(ArscHeaderType.TableType, ArscHeaderType.parse(buffer))
	}

	@Test
	fun `table type spec test`() {
		val buffer = bytes(0x02, 0x02)
		assertEquals(ArscHeaderType.TableTypeSpec, ArscHeaderType.parse(buffer))
	}

	@Test
	fun `table library test`() {
		val buffer = bytes(0x02, 0x03)
		assertEquals(ArscHeaderType.TableLibrary, ArscHeaderType.parse(buffer))
	}

	@Test
	fun `invalid parsing test`() {
		val buffer = bytes(0x7F, 0x7F)
		assertThrows<ArscError> { ArscHeaderType.parse(buffer) }
	}

	@Test
	fun `write test`() {
		val targetBytes = bytes(0x02, 0x03, cursorToEnd = true)
		val bytes = ByteBuffer.allocate(2)
			.apply { ArscHeaderType.write(this, ArscHeaderType.TableLibrary) }

		assertBuffersEqual(targetBytes, bytes)
	}
}

package com.github.diamondminer88.arsc

import com.github.diamondminer88.arsc.internal.ArscHeaderType
import org.junit.jupiter.api.assertThrows
import kotlin.test.Test
import kotlin.test.assertEquals

class HeaderTypeTest {
	@Test
	fun `null test`() {
		val buffer = bytes(0x00, 0x00)
		assertEquals(ArscHeaderType.Null, ArscHeaderType.parse(buffer))
	}

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
	fun `invalid test`() {
		val buffer = bytes(127, 127)
		assertThrows<ArscError> { ArscHeaderType.parse(buffer) }
	}
}

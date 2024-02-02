package com.github.diamondminer88.arsc

import com.github.diamondminer88.arsc.internal.ArscHeader
import com.github.diamondminer88.arsc.internal.ArscHeaderType
import org.junit.jupiter.api.assertDoesNotThrow
import kotlin.test.Test
import kotlin.test.assertEquals

class HeaderTest {
	@Test
	fun parses() {
		val bytes = bytes(
			0x00, 0x01,
			0x00, 0x08,
			0x00, 0x00, 0x00, 0x7F,
		)

		val header = assertDoesNotThrow { ArscHeader.parse(bytes) }

		assertEquals(ArscHeaderType.StringPool, header.type)
		assertEquals(8u, header.headerSize)
		assertEquals(127U, header.bodySize)
	}
}

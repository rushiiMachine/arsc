package com.github.diamondminer88.arsc

import org.junit.jupiter.api.assertDoesNotThrow
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals

class ArscTest {
	@Test
	fun `from parsed`() {
		val pkg = ArscPackage(
			id = 0u,
			name = "com.discord",
			types = mutableMapOf(),
		)

		val arsc = Arsc(listOf(pkg))
		assertEquals("com.discord", arsc.packages.firstOrNull()?.name)
	}

	@Test
	fun `from bytes`() {
		val arscFile = javaClass.classLoader
			.getResourceAsStream("discord.arsc")!!
			.readBytes()

		val arsc = assertDoesNotThrow { Arsc(arscFile) }
		assertEquals("com.discord", arsc.packages.firstOrNull()?.name)
		File("C:/Users/Fluff/Desktop/arsc.txt").writeText(arsc.toString())
	}

	@Test
	fun `from file`() {
		val arscFile = javaClass.classLoader
			.getResource("discord.arsc")!!

		val arsc = assertDoesNotThrow { Arsc(File(arscFile.file)) }
		assertEquals("com.discord", arsc.packages.firstOrNull()?.name)
	}
}

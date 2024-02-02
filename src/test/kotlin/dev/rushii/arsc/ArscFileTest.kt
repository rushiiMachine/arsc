package dev.rushii.arsc

import org.junit.jupiter.api.assertDoesNotThrow
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * End to end full parsing/writing tests for [ArscFile]
 */
class ArscFileTest {
	private val arscFile = javaClass.classLoader.getResource("discord.arsc")!!
	private val arscBytes = arscFile.readBytes()

	@Test
	fun `from parsed`() {
		val pkg = ArscPackage(
			id = 0u,
			name = "com.discord",
			types = mutableMapOf(),
		)

		val arsc = ArscFile(listOf(pkg))
		assertEquals("com.discord", arsc.packages.firstOrNull()?.name)
	}

	@Test
	fun `from bytes`() {
		val arsc = assertDoesNotThrow { ArscFile(arscBytes) }
		assertEquals("com.discord", arsc.packages.firstOrNull()?.name)
	}

	@Test
	fun `from file`() {
		val arsc = assertDoesNotThrow { ArscFile(File(arscFile.file)) }
		assertEquals("com.discord", arsc.packages.firstOrNull()?.name)
	}
}

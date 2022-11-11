package com.github.diamondminer88.arsc

import org.junit.jupiter.api.assertDoesNotThrow
import kotlin.test.Test
import kotlin.test.assertTrue

class ArscTest {
	private val discordResources = javaClass.classLoader
		.getResourceAsStream("discord.arsc")!!
		.readBytes()

	@Test
	fun `from bytes`() {
		val arsc = assertDoesNotThrow {
			Arsc(discordResources)
		}

		assertTrue(arsc.packages.isNotEmpty(), "empty packages list")
	}
}

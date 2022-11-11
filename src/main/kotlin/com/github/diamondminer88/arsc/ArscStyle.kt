package com.github.diamondminer88.arsc

import java.nio.ByteBuffer

public data class ArscStyle(
	val spans: List<Span>,
) {
	public data class Span(
		val name: String,
		val start: UInt,
		val end: UInt,
	)

	internal companion object {
		private const val SPAN_END = UInt.MAX_VALUE

		@JvmStatic
		fun parse(bytes: ByteBuffer): ArscStyle {
			val spans = mutableListOf<Span>()

			while (true) {
				val name = bytes.int.toUInt()

				if (name == SPAN_END) {
					break
				}

				val start = bytes.int.toUInt()
				val end = bytes.int.toUInt()

				spans += Span(
					// name = name,
					name = "", // TODO: here
					start = start,
					end = end
				)
			}

			return ArscStyle(spans)
		}
	}
}

package com.github.diamondminer88.arsc

import com.github.diamondminer88.arsc.internal.readU32
import java.nio.ByteBuffer

public data class ArscStyle(
	val spans: List<Span>,
) {
	internal fun size(): Int {
		return spans.size * Span.BYTES_SIZE + 4
	}

	public data class Span(
		val name: String,
		val start: UInt,
		val end: UInt,
	) {
		internal companion object {
			const val BYTES_SIZE = 12
		}
	}

	internal companion object {
		private const val SPAN_END = UInt.MAX_VALUE

		@JvmStatic
		fun parse(bytes: ByteBuffer): ArscStyle {
			val spans = mutableListOf<Span>()

			while (true) {
				val name = bytes.readU32()

				if (name == SPAN_END) {
					break
				}

				val start = bytes.readU32()
				val end = bytes.readU32()

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

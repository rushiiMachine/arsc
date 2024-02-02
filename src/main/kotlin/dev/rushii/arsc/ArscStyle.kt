package dev.rushii.arsc

import dev.rushii.arsc.internal.readU32
import java.nio.ByteBuffer

public data class ArscStyle(
	val spans: List<Span>,
) {
	/** Size of this data structure in bytes. */
	public fun size(): Int {
		return (spans.size * Span.size()) +
			UInt.SIZE_BYTES // SPAN_END
	}

	public data class Span(
		val name: String,
		val start: UInt,
		val end: UInt,
	) {
		public companion object {
			/** Size of the full data structure in bytes. */
			public fun size(): Int = UInt.SIZE_BYTES * 3 // name, start, end
		}
	}

	@ArscInternalApi
	public companion object {
		/**
		 * If a name of a parsed span is this value then it is the terminator
		 * of the spans list and should immediately stop parsing the rest of the span.
		 */
		private const val SPAN_END = UInt.MAX_VALUE

		@JvmStatic
		public fun parse(bytes: ByteBuffer): ArscStyle {
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
					name = "", // TODO: retrieve string from string pool
					start = start,
					end = end
				)
			}

			return ArscStyle(spans)
		}
	}
}

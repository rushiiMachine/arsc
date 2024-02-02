package dev.rushii.arsc.internal

import java.nio.ByteBuffer

/**
 * A header prefixed at the start of the file and before each package and chunk
 * @param type The body type
 * @param headerSize u16 header size
 * @param bodySize u64 body size
 */
@ArscInternalApi
public data class ArscHeader(
	val type: ArscHeaderType,
	val headerSize: UShort,
	val bodySize: UInt,
) {
	public companion object {
		/** Size of this data structure in bytes. */
		@JvmStatic
		public fun size(): Int = ArscHeaderType.size() + UShort.SIZE_BYTES + UInt.SIZE_BYTES

		@JvmStatic
		public fun parse(bytes: ByteBuffer): ArscHeader {
			val type = ArscHeaderType.parse(bytes)
			val headerSize = bytes.readU16()
			val bodySize = bytes.readU32()

			return ArscHeader(
				type = type,
				headerSize = headerSize,
				bodySize = bodySize,
			)
		}

		@JvmStatic
		public fun write(bytes: ByteBuffer, value: ArscHeader) {
			ArscHeaderType.write(bytes, value.type)
			bytes.putShort(value.headerSize.toShort())
			bytes.putInt(value.bodySize.toInt())
		}
	}
}

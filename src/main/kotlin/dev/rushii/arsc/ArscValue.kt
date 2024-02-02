package dev.rushii.arsc

import dev.rushii.arsc.internal.*
import java.nio.ByteBuffer

public sealed interface ArscValue {
	public sealed class Plain : ArscValue {
		public abstract val type: UByte

		@ArscInternalApi
		public companion object {
			private const val TYPE_STRING: UByte = 0x03u

			@JvmStatic
			public fun parse(bytes: ByteBuffer, globalStringPool: ArscStringPool): Plain {
				val size = bytes.readU16() // const u16 = 8
				val zero = bytes.readU8() // const u8 = 0
				val type = bytes.readU8()
				val data = bytes.readU32()

				return if (type == TYPE_STRING) {
					String(
						data = globalStringPool.strings[data.toInt()]
					)
				} else {
					Raw(
						type = type,
						data = data,
					)
				}
			}

			@JvmStatic
			public fun write(bytes: ByteBuffer, value: Plain, writtenGlobalPool: ArscStringPool.WrittenPool) {
				bytes.putShort(8) // size
				bytes.put(0) // zero
				bytes.put(value.type.toByte()) // type

				when (value) {
					is Raw -> {
						bytes.putInt(value.data.toInt())
					}

					is String -> {
						bytes.putInt(writtenGlobalPool.strings[value.data]!!)
					}
				}
			}
		}

		public data class Raw(
			override val type: UByte,
			val data: UInt,
		) : Plain()

		public data class String(
			val data: kotlin.String,
		) : Plain() {
			override val type: UByte = 0x3U
		}
	}

	public data class Bag(
		val parent: UInt,
		val values: Map<UInt, Plain>,
	) : ArscValue
}

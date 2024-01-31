package com.github.diamondminer88.arsc

import com.github.diamondminer88.arsc.internal.ArscStringPool
import java.nio.ByteBuffer

public sealed interface ArscValue {
	public abstract class Plain : ArscValue {
		public abstract val type: UByte

		internal companion object {
			private const val TYPE_STRING: UByte = 0x03u

			@JvmStatic
			fun parse(bytes: ByteBuffer, globalStringPool: ArscStringPool): Plain {
				val size = bytes.short // const u16 = 8
				val zero = bytes.get() // const u8 = 0
				val type = bytes.get().toUByte()
				val data = bytes.int.toUInt()

				return if (type == TYPE_STRING) {
					PlainString(
						data = globalStringPool.strings[data.toInt()]
					)
				} else {
					PlainRaw(
						type = type,
						data = data,
					)
				}
			}

			@JvmStatic
			fun write(bytes: ByteBuffer, value: Plain, writtenGlobalPool: ArscStringPool.WrittenPool) {
				bytes.putShort(8) // size
				bytes.put(0) // zero
				bytes.put(value.type.toByte()) // type

				when (value) {
					is PlainRaw -> {
						bytes.putInt(value.data.toInt())
					}

					is PlainString -> {
						bytes.putInt(writtenGlobalPool.strings[value.data]!!)
					}
				}
			}
		}
	}

	public data class PlainRaw(
		override val type: UByte,
		val data: UInt,
	) : Plain()

	public data class PlainString(
		val data: String,
	) : Plain() {
		override val type: UByte = 0x3U
	}

	public data class Bag(
		val parent: UInt,
		val values: Map<UInt, Plain>,
	) : ArscValue
}

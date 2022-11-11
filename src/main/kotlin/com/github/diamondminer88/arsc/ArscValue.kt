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
				val size = bytes.short.toUShort()
				val zero = bytes.get().toUByte()
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

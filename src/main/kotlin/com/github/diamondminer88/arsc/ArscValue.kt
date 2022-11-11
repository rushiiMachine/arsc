package com.github.diamondminer88.arsc

import com.github.diamondminer88.arsc.internal.ArscStringPool
import java.nio.ByteBuffer

public sealed interface ArscValue {
	public abstract class Plain : ArscValue {
		public abstract val size: UShort
		public abstract val type: UByte

		internal companion object {
			private const val TYPE_STRING = 0x03U

			@JvmStatic
			fun parse(bytes: ByteBuffer, globalStringPool: ArscStringPool): Plain {
				val size = bytes.short.toUShort()
				val zero = bytes.get().toUByte()
				val type = bytes.get().toUByte()
				val data = bytes.int.toUInt()

				println("value zero: $zero")

				return if (type.toUInt() and TYPE_STRING != 0U) {
					PlainString(
						size = size,
						data = globalStringPool.strings[data.toInt()]
					)
				} else {
					PlainRaw(
						size = size,
						type = type,
						data = data,
					)
				}
			}
		}
	}

	public data class PlainRaw(
		override val size: UShort,
		override val type: UByte,
		val data: UInt,
	) : Plain()

	public data class PlainString(
		override val size: UShort,
		val data: String,
	) : Plain() {
		override val type: UByte = 0x3U
	}

	public data class Bag(
		val parent: UInt,
		val values: Map<UInt, Plain>,
	) : ArscValue
}

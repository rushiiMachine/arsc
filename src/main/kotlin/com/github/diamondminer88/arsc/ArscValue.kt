package com.github.diamondminer88.arsc

import com.github.diamondminer88.arsc.internal.ArscStringPool
import java.nio.ByteBuffer

public sealed interface ArscValue {
	public data class Plain(
		val size: UShort,
		val zero: UByte,
		val type: UByte,
		val dataIndex: UInt,
	) : ArscValue {
		public fun isString(): Boolean {
			return type.toUInt() and TYPE_STRING != 0U
		}

		internal companion object {
			const val TYPE_STRING = 0x03U

			@JvmStatic
			fun parse(bytes: ByteBuffer, globalStringPool: ArscStringPool): Plain {
				val size = bytes.short.toUShort()
				val zero = bytes.get().toUByte()
				val type = bytes.get().toUByte()
				val dataIndex = bytes.int.toUInt()

				// TODO: get value from string pool

				return Plain(
					size = size,
					zero = zero,
					type = type,
					dataIndex = dataIndex,
				)
			}
		}
	}

	public data class Bag(
		val parent: UInt,
		val values: Map<UInt, Plain>,
	) : ArscValue
}

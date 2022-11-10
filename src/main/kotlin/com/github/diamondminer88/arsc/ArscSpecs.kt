package com.github.diamondminer88.arsc

import java.nio.ByteBuffer

public data class ArscSpecs(
	internal val typeId: UByte,
	val res0: UByte,
	val res1: UShort,
	val specs: List<Spec>,
) {
	public data class Spec(
		val id: UInt,
		val flags: UInt,
	)

	internal companion object {
		@JvmStatic
		fun parse(bytes: ByteBuffer): ArscSpecs {
			val typeId = bytes.get().toUByte()
			val res0 = bytes.get().toUByte()
			val res1 = bytes.short.toUShort()
			val specCount = bytes.int.toUInt()

			val specs = (0 until specCount.toInt())
				.map { id -> Spec(id.toUInt(), bytes.int.toUInt()) }

			assert(specs.isNotEmpty()) { "specs cannot be empty" }

			return ArscSpecs(
				typeId = typeId,
				res0 = res0,
				res1 = res1,
				specs = specs
			)
		}
	}
}

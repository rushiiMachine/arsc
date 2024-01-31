package com.github.diamondminer88.arsc

import java.nio.ByteBuffer

public data class ArscSpecs(
	internal val typeId: UByte = 0u,
	val specs: MutableMap<UInt, Spec>,
) {
	public fun highestSpecId(): UInt {
		return specs.values
			.maxByOrNull { it.id }
			?.id
			?: 1U
	}

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
				specs = specs.associateBy { it.id }.toMutableMap()
			)
		}
	}
}

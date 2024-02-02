package com.github.diamondminer88.arsc

import com.github.diamondminer88.arsc.internal.*
import java.nio.ByteBuffer

public data class ArscSpecs(
	val typeId: UByte = 0u,
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

	@ArscInternalApi
	public companion object {
		@JvmStatic
		public fun parse(bytes: ByteBuffer): ArscSpecs {
			val typeId = bytes.readU8()
			val res0 = bytes.readU8()
			val res1 = bytes.readU16()
			val specCount = bytes.readU32()

			val specs = (0u..<specCount)
				.map { id -> Spec(id, bytes.readU32()) }

			assert(specs.isNotEmpty()) { "specs cannot be empty" }

			return ArscSpecs(
				typeId = typeId,
				specs = specs.associateBy { it.id }.toMutableMap()
			)
		}
	}
}

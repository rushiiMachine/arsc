package dev.rushii.arsc

import dev.rushii.arsc.internal.*
import java.nio.ByteBuffer

public data class ArscResource(
	val specId: UInt = 0u,
	val flags: UShort,
	val name: String,
	val value: ArscValue,
) {
	@ArscInternalApi
	public companion object {
		private const val FLAG_COMPLEX: UShort = 0x0001u

		@JvmStatic
		public fun parse(
			bytes: ByteBuffer,
			resourceCount: Int,
			globalStringPool: ArscStringPool,
			keyStringPool: ArscStringPool,
		): MutableList<ArscResource> {
			val entries = (0..<resourceCount)
				.map { bytes.readU32() }

			val resources = mutableListOf<ArscResource>()

			entries.forEachIndexed { specIndex, entry ->
				if (entry == UInt.MAX_VALUE)
					return@forEachIndexed

				val size = bytes.readU16()
				val flags = bytes.readU16()
				val nameIndex = bytes.readU32()
				val value = if (flags and FLAG_COMPLEX != 0.toUShort()) {
					val parent = bytes.readU32()
					val count = bytes.readU32()
					val values = (0..<count.toInt()).associate {
						val index = bytes.readU32()
						val value = ArscValue.Plain.parse(bytes, globalStringPool)
						index to value
					}

					ArscValue.Bag(
						parent = parent,
						values = values,
					)
				} else {
					ArscValue.Plain.parse(bytes, globalStringPool)
				}

				resources += ArscResource(
					specId = specIndex.toUInt(),
					flags = flags,
					name = keyStringPool.strings[nameIndex.toInt()],
					value = value,
				)
			}

			return resources
		}
	}
}

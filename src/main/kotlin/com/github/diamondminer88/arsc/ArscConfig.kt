package com.github.diamondminer88.arsc

import java.nio.ByteBuffer

/**
 * Represents a type configuration (mipmap-xxhdpi, mipmap-hdpi, etc...)
 * @param typeId The parsed type id this belongs to
 * @param configId The id of this config
 * @param res0 u8 representation of something
 * @param res1 u16 representation of something
 * @param resources The actual resource data
 */
public data class ArscConfig(
	internal val typeId: UByte,
	internal val configId: ArscTypeConfigId,
	var res0: UByte,
	var res1: UShort,
	var resources: MutableList<ArscResource>,
) {
	internal companion object {
		/**
		 * Read a resource config from the current position in the buffer
		 */
		@JvmStatic
		fun parse(bytes: ByteBuffer): ArscConfig {
			val typeId = bytes.get().toUByte()
			val res0 = bytes.get().toUByte()
			val res1 = bytes.short.toUShort()
			val resourceCount = bytes.int.toUInt()
			val resourcesStart = bytes.int.toUInt()
			val configId = ArscTypeConfigId.parse(bytes)
			val resources = ArscResource.parseMultiple(resourceCount)

			return ArscConfig(
				typeId = typeId,
				configId = configId,
				res0 = res0,
				res1 = res1,
				resources = resources,
			)
		}
	}
}

public data class ArscTypeConfigId(
	public var data: ByteArray,
) {
	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (javaClass != other?.javaClass) return false
		other as ArscTypeConfigId
		if (!data.contentEquals(other.data)) return false
		return true
	}

	override fun hashCode(): Int {
		return data.contentHashCode()
	}

	internal companion object {
		/**
		 * Reads a config id from the current position in the buffer
		 */
		@JvmStatic
		fun parse(bytes: ByteBuffer): ArscTypeConfigId {
			val size = bytes.int.toUInt()
			bytes.position(bytes.position() - 4)

			val idBytes = ByteArray(size.toInt())
				.also { bytes.get(it, 0, size.toInt()) }

			return ArscTypeConfigId(idBytes)
		}
	}
}

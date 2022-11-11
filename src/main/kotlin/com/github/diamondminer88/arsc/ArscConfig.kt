package com.github.diamondminer88.arsc

import com.github.diamondminer88.arsc.internal.ArscStringPool
import java.nio.ByteBuffer

/**
 * Represents a type configuration (mipmap-xxhdpi, mipmap-hdpi, etc...)
 * @param typeId The parsed type id this belongs to
 * @param configId The id of this config
 * @param resources The actual resource data
 */
public data class ArscConfig(
	internal val typeId: UByte = 0u,
	val configId: ConfigId,
	var resources: MutableList<ArscResource>,
) {
	internal companion object {
		/**
		 * Read a resource config from the current position in the buffer
		 */
		@JvmStatic
		fun parse(
			bytes: ByteBuffer,
			globalStringPool: ArscStringPool,
			keyStringPool: ArscStringPool,
		): ArscConfig {
			val typeId = bytes.get().toUByte()
			val res0 = bytes.get().toUByte()
			val res1 = bytes.short.toUShort()
			val resourceCount = bytes.int.toUInt()
			val resourcesStart = bytes.int.toUInt()
			val configId = ConfigId.parse(bytes)
			val resources = ArscResource.parse(bytes, resourceCount.toInt(), globalStringPool, keyStringPool)

			return ArscConfig(
				typeId = typeId,
				configId = configId,
				resources = resources,
			)
		}
	}

	public data class ConfigId(
		public var data: ByteArray,
	) {
		override fun equals(other: Any?): Boolean {
			if (this === other) return true
			if (javaClass != other?.javaClass) return false
			other as ConfigId
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
			fun parse(bytes: ByteBuffer): ConfigId {
				val size = bytes.int.toUInt()
				bytes.position(bytes.position() - 4)

				val idBytes = ByteArray(size.toInt())
					.also { bytes.get(it, 0, size.toInt()) }

				return ConfigId(idBytes)
			}
		}
	}
}

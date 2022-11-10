@file:Suppress("MemberVisibilityCanBePrivate")

package com.github.diamondminer88.arsc

import com.github.diamondminer88.arsc.internal.*
import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * The base parser and/or writer for editing .arsc files
 */
public class Arsc {
	public val packages: List<ArscPackage>

	/**
	 * Creates an Arsc from an existing parsed package in memory
	 */
	public constructor(packages: List<ArscPackage>) {
		this.packages = packages
	}

	/**
	 * Loads an arsc file from a file
	 */
	public constructor(file: File) {
		packages = emptyList()
		Arsc(file.readBytes())
	}

	/**
	 * Loads arsc from a bytearray
	 */
	public constructor(bytes: ByteArray) {
		val buffer = ByteBuffer
			.wrap(bytes)
			.order(ByteOrder.LITTLE_ENDIAN)

		println(ArscHeader.parse(buffer)) // throwaway

		val packageCount = buffer.int
		val globalStringPool = ArscStringPool.parse(buffer)
		val packages = MutableList(packageCount) { ArscPackage.parse(buffer) }

		this.packages = packages
	}

	override fun toString(): String {
		return "Arsc[packages=$packages]"
	}
}

public typealias ArscTypeName = String

/**
 * Represents a type configuration (mipmap-xxhdpi, mipmap-hdpi, etc...)
 * @param typeId The parsed type id this belongs to
 * @param configId The id of this config
 * @param res0 u8 representation of something
 * @param res1 u16 representation of something
 * @param resources The actual resource data
 */
public data class ArscTypeConfig(
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
		fun parse(bytes: ByteBuffer): ArscTypeConfig {
			val typeId = bytes.get().toUByte()
			val res0 = bytes.get().toUByte()
			val res1 = bytes.short.toUShort()
			val resourceCount = bytes.int.toUInt()
			val resourcesStart = bytes.int.toUInt()
			val configId = ArscTypeConfigId.parse(bytes)
			val resources = ArscResource.parseMultiple(resourceCount)

			return ArscTypeConfig(
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

		}
	}
}

public data class ArscType(
	var id: StringPoolSize,
	var name: ArscTypeName,
	var configs: List<ArscTypeConfig>,
	var specs: List<ArscTypeSpec>,
)

public class ArscError(position: Int, value: Any, message: String) : Error("Failed to parse arsc at index $position, value $value: $message")

@file:Suppress("MemberVisibilityCanBePrivate")

package com.github.diamondminer88.arsc

import com.github.diamondminer88.arsc.internal.*
import java.nio.ByteBuffer

/**
 * Representing a package in the arsc.
 * Usually, only one exists per APK, however the format defines that there can be more.
 */
public data class ArscPackage(
	/**
	 * u32 id of the package
	 */
	var id: UInt,

	/**
	 * String name of the package
	 */
	var name: String,

	/**
	 * All the resource types contained within this package
	 */
	var types: MutableMap<ArscTypeName, ArscType>,
) {
	/**
	 * Gets or creates a new resource type based on the name to modify with [block].
	 * Generates an ID automatically.
	 */
	public fun type(name: ArscTypeName, block: ArscType.() -> Unit) {
		val type = types.computeIfAbsent(name) {
			ArscType(
				id = highestTypeId() + 1U,
				name = name,
				configs = mutableListOf(),
				specs = mutableListOf(),
			)
		}

		block(type)
	}

	/**
	 * Gets the highest defined type id or 0
	 */
	public fun highestTypeId(): UInt {
		return types.values
			.maxByOrNull { it.id }
			?.id
			?: 1U
	}

	internal companion object {
		/**
		 * Parse an arsc package at the current position in the buffer
		 */
		@JvmStatic
		fun parse(bytes: ByteBuffer): ArscPackage {
			val header = ArscHeader.parse(bytes)
			assert(header.type == ArscHeaderType.TablePackage) { "Parsed package header contains an invalid type" }

			val packageId = bytes.int.toUInt()
			val packageName = bytes.readStringUtf16(size = 128)

			val typeStringsOffset = bytes.int.toUInt()
			val lastPublicType = bytes.int.toUInt()
			val keyStringOffset = bytes.int.toUInt()
			val lastPublicKey = bytes.int.toUInt()
			val typeIdsOffset = bytes.int.toUInt()

			val typeNames = ArscStringPool.parse(bytes)
			val keyNames = ArscStringPool.parse(bytes)

			val types = (1..typeNames.strings.size).map {
				ArscType(
					id = it,
					name = typeNames.strings[it - 1],
					specs = mutableListOf(),
					configs = mutableListOf(),
				)
			}

			println("typeIds off $typeIdsOffset")
			println("cur pos ${bytes.position()}")

			var startPos = bytes.position()
			while (true) {
				println("loop pos: $startPos")

				val header = try {
					ArscHeader.parse(bytes)
				} catch (t: Throwable) {
					bytes.position(startPos)
					break
				}

				when (header.type) {
					ArscHeaderType.TableTypeSpec -> {
						val specs = ArscTypeSpec.parseChunk(bytes)

						val type = types[specs.typeId]
						assert(type.specs.isEmpty()) { "Duplicate specs chunk defined for type ${type.name}" }

						type.specs = specs
					}

					ArscHeaderType.TableType -> {
						val config = ArscTypeConfig.parse(bytes)
						types[config.typeId.toInt()].configs += config
					}

					else -> throw ArscError(
						startPos - ArscHeader.BYTES_SIZE,
						header.type,
						"Unexpected chunk type in typeIds section of package $packageName"
					)
				}

				startPos = bytes.position()
			}

			return ArscPackage(
				id = packageId,
				name = packageName,
				types = types.associateBy { it.name }.toMutableMap(),
			)
		}
	}
}

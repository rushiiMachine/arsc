@file:Suppress("MemberVisibilityCanBePrivate")

package dev.rushii.arsc

import dev.rushii.arsc.internal.*
import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * The main parser and writer for editing `*.arsc` files like an APK's `resources.arsc`.
 */
public class Arsc {
	public val packages: List<ArscPackage>

	/**
	 * Creates an Arsc from an existing parsed package in memory.
	 */
	public constructor(packages: List<ArscPackage>) {
		this.packages = packages
	}

	/**
	 * Parses an arsc file from a raw byte array.
	 */
	public constructor(bytes: ByteArray) {
		val buffer = ByteBuffer
			.wrap(bytes)
			.asReadOnlyBuffer()
			.order(ByteOrder.LITTLE_ENDIAN)

		val header = ArscHeader.parse(buffer)
		val packageCount = buffer.readU32()
		val globalStringPool = ArscStringPool.parse(buffer)
		val packages = MutableList(packageCount.toInt()) {
			ArscPackage.parse(buffer, globalStringPool)
		}

		this.packages = packages
	}

	/**
	 * Loads an arsc file from a file
	 */
	public constructor(file: File) :
		this(file.readBytes())

	/**
	 * Finalizes this Arsc and writes it to file
	 * @param out The target file to write the modified arsc to
	 */
	public fun write(out: File) {
		out.apply { exists() || createNewFile() }
			.writeBytes(finalize())
	}

	/**
	 * Finalizes this arsc and writes it to a byte array
	 */
	public fun finalize(): ByteArray {
		// global string pool
		val globalStrings = packages.flatMap { pkg ->
			pkg.types.values.flatMap { type ->
				type.configs.flatMap { cfg ->
					cfg.resources.mapNotNull { rsc ->
						(rsc.value as? ArscValue.PlainString)?.data
					}
				}
			}
		}
		val globalStringPool = ArscStringPool(
			strings = globalStrings,
			styles = emptyList(), // TODO: collect styles
			flags = ArscStringPool.UTF_8_FLAG, // TODO: check rust lib how this is decided
		)

		// arsc header
		// val size = 0x000C + globalStringPool.size() + pkgs.sumOf { it.size }
		val size = 0x000C + globalStringPool.size() + 0
		val header = ArscHeader(
			type = ArscHeaderType.TableLibrary, // FIXME: correct arsc header type
			headerSize = 0x000Cu,
			bodySize = size.toUInt(),
		)

		val bytes = ByteBuffer.allocate(size)

		ArscHeader.write(bytes, header)
		val writtenGlobalStringPool = ArscStringPool.write(bytes, globalStringPool)

		// packages
		for (pkg in packages) {
			// ArscPackage.write(bytes, pkg, writtenGlobalStringPool)
		}

		return bytes.array()
	}

	override fun toString(): String {
		return "Arsc[packages=$packages]"
	}
}

public class ArscError(position: Int, value: Any?, message: String) :
	Error("Failed to parse arsc at index $position, value $value: $message")

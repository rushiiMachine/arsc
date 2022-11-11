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
	 * Loads arsc from a bytearray
	 */
	public constructor(bytes: ByteArray) {
		val buffer = ByteBuffer
			.wrap(bytes)
			.order(ByteOrder.LITTLE_ENDIAN)

		val header = ArscHeader.parse(buffer)
		val packageCount = buffer.int.toUInt()
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

	override fun toString(): String {
		return "Arsc[packages=$packages]"
	}
}

public class ArscError(position: Int, value: Any, message: String) :
	Error("Failed to parse arsc at index $position, value $value: $message")

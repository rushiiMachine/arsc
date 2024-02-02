package com.github.diamondminer88.arsc.internal

import com.github.diamondminer88.arsc.ArscError
import java.nio.ByteBuffer

/**
 * A parsed chunk type
 * @param value Internal u16 representation.
 */
internal enum class ArscHeaderType(val value: UShort) {
	StringPool(0x0001u),
	Table(0x0002u),
	TablePackage(0x0200u),
	TableType(0x0201u),
	TableTypeSpec(0x0202u),
	TableLibrary(0x0203u);

	companion object {
		/** Size of this data structure in bytes. */
		@JvmStatic
		fun size(): Int = UShort.SIZE_BYTES

		@JvmStatic
		fun parse(bytes: ByteBuffer): ArscHeaderType {
			return when (val value = bytes.readU16()) {
				StringPool.value -> StringPool
				Table.value -> Table
				TablePackage.value -> TablePackage
				TableType.value -> TableType
				TableTypeSpec.value -> TableTypeSpec
				TableLibrary.value -> TableLibrary
				else -> throw ArscError(bytes.position() - size(), value, "Invalid header type 0x${value.toString(16)}")
			}
		}

		@JvmStatic
		fun write(bytes: ByteBuffer, value: ArscHeaderType) {
			bytes.putShort(value.value.toShort())
		}
	}
}

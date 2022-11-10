package com.github.diamondminer88.arsc.internal

import com.github.diamondminer88.arsc.ArscError
import java.nio.ByteBuffer

/**
 * The parsed chunk type
 * @param value u16 representation
 */
internal enum class ArscHeaderType(val value: Short) {
	Null(0x0000),
	StringPool(0x0001),
	Table(0x0002),
	TablePackage(0x0200),
	TableType(0x0201),
	TableTypeSpec(0x0202),
	TableLibrary(0x0203);

	companion object {
		const val SIZE_BYTES = 2

		/**
		 * Parse a chunk type from the current position in the buffer
		 */
		@JvmStatic
		fun parse(bytes: ByteBuffer): ArscHeaderType {
			return when (val value = bytes.short) {
				Null.value -> Null
				StringPool.value -> StringPool
				Table.value -> Table
				TablePackage.value -> TablePackage
				TableType.value -> TableType
				TableTypeSpec.value -> TableTypeSpec
				TableLibrary.value -> TableLibrary
				else -> throw ArscError(bytes.position() - SIZE_BYTES, value, "Invalid header type 0x${value.toString(16)}")
			}
		}
	}
}

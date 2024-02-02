package dev.rushii.arsc.internal

import dev.rushii.arsc.ArscError
import java.nio.ByteBuffer

/**
 * A parsed chunk type
 * @param value The raw value this enum value represents.
 */
@ArscInternalApi
public enum class ArscHeaderType(public val value: UShort) {
	StringPool(0x0001u),
	Table(0x0002u),
	TablePackage(0x0200u),
	TableType(0x0201u),
	TableTypeSpec(0x0202u),
	TableLibrary(0x0203u);

	public companion object {
		/** Size of this data structure in bytes. */
		@JvmStatic
		public fun size(): Int = UShort.SIZE_BYTES

		@JvmStatic
		public fun parse(bytes: ByteBuffer): ArscHeaderType {
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
		public fun write(bytes: ByteBuffer, value: ArscHeaderType) {
			bytes.putShort(value.value.toShort())
		}
	}
}

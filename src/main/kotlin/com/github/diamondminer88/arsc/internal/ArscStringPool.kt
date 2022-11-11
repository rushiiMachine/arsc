package com.github.diamondminer88.arsc.internal

import com.github.diamondminer88.arsc.ArscStyle
import java.nio.ByteBuffer

internal data class ArscStringPool(
	val strings: List<String>,
	val styles: List<ArscStyle>,
) {
	companion object {
		private val UTF_8_FLAG = 0x00000100.toUInt()

		@JvmStatic
		fun parse(bytes: ByteBuffer): ArscStringPool {
			val startPos = bytes.position()

			val header = ArscHeader.parse(bytes)
			assert(header.type == ArscHeaderType.StringPool) { "Invalid header type ${header.type} when parsing string pool" }

			val stringsCount = bytes.int.toUInt()
			val stylesCount = bytes.int.toUInt()
			val flags = bytes.int.toUInt()
			val stringsOffset = bytes.int.toUInt()
			val stylesOffset = bytes.int.toUInt()

			val offsets = Array(stringsCount.toInt()) { bytes.int.toUInt() }
			val styleOffsets = Array(stylesCount.toInt()) { bytes.int.toUInt() }

			val useUTF8 = flags and UTF_8_FLAG != 0U
			val strings = List(stringsCount.toInt()) {
				if (useUTF8) {
					readUtf8String(bytes)
				} else {
					readUtf16String(bytes)
				}
			}

			val styles = List(stylesCount.toInt()) { ArscStyle.parse(bytes) }

			if (bytes.position() % 4 > 0) {
				bytes.position(bytes.position() + 4 - (bytes.position() % 4))
			}

			return ArscStringPool(
				strings = strings,
				styles = styles,
			)
		}

		private fun readUtf8Length(bytes: ByteBuffer): UShort {
			val length = bytes.get().toUByte()

			return if (length.toInt() and 0x80 != 0) {
				val length2 = bytes.get().toUByte()
				(((length and 0x7FU).toUInt() shl 8) or length2.toUInt()).toUShort()
			} else {
				length.toUShort()
			}
		}

		private fun readUtf8String(bytes: ByteBuffer): String {
			val charCount = readUtf8Length(bytes)
			val byteCount = readUtf8Length(bytes)

			val string = ByteArray(byteCount.toInt())
				.also { bytes.get(it, 0, byteCount.toInt()) }
				.let { String(it, Charsets.UTF_8) }

			val nullTerminator = bytes.get()
			assert(nullTerminator.toInt() == 0x00) { "invalid utf8 string terminator" }

			return string
		}

		private fun readUtf16Length(bytes: ByteBuffer): UInt {
			val length = bytes.short.toUShort()

			return if (length > 0x7FFFu) {
				((length and 0x7FFFu).toUInt() shl 8) or bytes.short.toUInt()
			} else {
				length.toUInt()
			}
		}

		private fun readUtf16String(bytes: ByteBuffer): String {
			val byteCount = readUtf16Length(bytes)

			val string = ByteArray(byteCount.toInt() * 2)
				.also { bytes.get(it, 0, byteCount.toInt() * 2) }
				.let { String(it, Charsets.UTF_16LE) }

			val nullTerminator = bytes.short
			assert(nullTerminator.toInt() == 0x0000) { "invalid utf16 string terminator" }

			return string
		}
	}
}

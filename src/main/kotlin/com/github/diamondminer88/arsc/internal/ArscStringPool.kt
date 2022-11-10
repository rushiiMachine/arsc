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

			val stringCount = bytes.int.toUInt()
			val styleCount = bytes.int.toUInt()
			val flags = bytes.int.toUInt()
			val stringsOffset = bytes.int.toUInt()
			val stylesOffset = bytes.int.toUInt()

			val offsets = Array(stringCount.toInt()) { bytes.int.toUInt() }
			val styleOffsets = Array(stringCount.toInt()) { bytes.int.toUInt() }

			assert(stringsOffset + startPos.toUInt() == bytes.position().toUInt()) { "Invalid stringsOffsets or incorrectly parsed data" }

			val useUTF8 = flags and UTF_8_FLAG == UTF_8_FLAG
			val strings = List(stringCount.toInt()) {
				if (useUTF8) {
					readUtf8String(bytes)
				} else {
					readUtf16String(bytes)
				}
			}

			bytes.position(startPos + stylesOffset.toInt())
			val styles = List(styleCount.toInt()) { ArscStyle.parse(bytes) }

			bytes.position(startPos + header.size.toInt())

			return ArscStringPool(
				strings = strings,
				styles = styles,
			)
		}

		private fun readUtf8Length(bytes: ByteBuffer): UShort {
			val length = bytes.get().toUByte()

			return if (length.toInt() and 0x80 != 0) {
				(((length.toUInt() and 0x7FU) shl 8) or bytes.get().toUInt()).toUShort()
			} else {
				length.toUShort()
			}
		}

		fun readUtf8String(bytes: ByteBuffer): String {
			val charCount = readUtf8Length(bytes)
			val byteCount = readUtf8Length(bytes)

			val stringBytes = ByteArray(byteCount.toInt())
				.also { bytes.get(it, 0, byteCount.toInt()) }

			return String(stringBytes, Charsets.UTF_8)
		}

		private fun readUtf16Length(bytes: ByteBuffer): UInt {
			val length = bytes.short.toUShort()

			return if (length > 0x7FFFu) {
				((length and 0x7FFFu).toUInt() shl 8) or bytes.short.toUInt()
			} else {
				length.toUInt()
			}
		}

		fun readUtf16String(bytes: ByteBuffer): String {
			val charCount = readUtf16Length(bytes)
			val byteCount = readUtf16Length(bytes)

			bytes.position(bytes.position() + 2) // skip null terminator
			val stringBytes = ByteArray(byteCount.toInt())
				.also { bytes.get(it, 0, byteCount.toInt()) }

			return String(stringBytes, Charsets.UTF_16LE)
		}
	}
}

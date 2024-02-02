package com.github.diamondminer88.arsc.internal

import com.github.diamondminer88.arsc.ArscError
import com.github.diamondminer88.arsc.ArscStyle
import java.nio.ByteBuffer
import kotlin.experimental.and

internal data class ArscStringPool(
	val strings: List<String>,
	val styles: List<ArscStyle>,
	val flags: UInt,
) {
	fun size(): Int {
		var size = 0
		size += ArscHeader.size() // header
		size += 5 * 4 // stringsCount, stylesCount, flags, stringsOffset, stylesOffset
		size += strings.size * 4 // stringsOffsets
		size += styles.size * 4 // stylesOffsets
		size += strings.sumOf { // strings
			if (flags and UTF_8_FLAG != 0u) {
				val charsSize = it.chars().count().toInt()
				val charsLengthSize = if (charsSize <= 0x7F) 1 else 2
				val bytesLengthSize = if (it.length <= 0x7F) 1 else 2
				charsLengthSize + bytesLengthSize + charsSize + 1 // null term
			} else {
				val charsSize = it.chars().count().toInt()
				val charsLengthSize = if (charsSize <= 0x7FFF) 2 else 4
				charsSize * 2 + charsLengthSize + 2 // null term
			}
		}
		// TODO: strings padding
		size += styles.sumOf { it.size() } + 8 // styles
		return size
	}

	data class WrittenPool(
		// mapped to item -> id/index
		val strings: Map<String, Int>,
		val styles: Map<ArscStyle, Int>,
	)

	companion object {
		val UTF_8_FLAG = 0x00000100.toUInt()

		@JvmStatic
		fun parse(bytes: ByteBuffer): ArscStringPool {
			val startPos = bytes.position()

			val header = ArscHeader.parse(bytes)
			assert(header.type == ArscHeaderType.StringPool) { "Invalid header type ${header.type} when parsing string pool" }

			val stringsCount = bytes.readU32()
			val stylesCount = bytes.readU32()
			val flags = bytes.readU32()
			val stringsOffset = bytes.readU32()
			val stylesOffset = bytes.readU32()

			val offsets = Array(stringsCount.toInt()) { bytes.readU32() }
			val styleOffsets = Array(stylesCount.toInt()) { bytes.readU32() }

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
				flags = flags,
			)
		}

		private fun readUtf8Length(bytes: ByteBuffer): UShort {
			val length = bytes.readU8().toUShort()

			return if (length and 0x80u > 0u) {
				val length2 = bytes.readU8().toUShort()

				((length and 0x7Fu) shl 8) or length2
			} else {
				length
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

			val nullTerminator = bytes.readU16()
			if (nullTerminator.toInt() != 0) {
				throw ArscError(
					bytes.position() - UShort.SIZE_BYTES,
					nullTerminator,
					"Invalid UTF16LE string NULL terminator"
				)
			}

			return string
		}

		@JvmStatic
		fun write(bytes: ByteBuffer, pool: ArscStringPool): WrittenPool {
			val startPos = bytes.position()

			bytes.putNullBytes(ArscHeader.size()) // blank header for now
			bytes.putInt(pool.strings.size) // strings count
			bytes.putInt(pool.styles.size) // styles count
			bytes.putInt(pool.flags.toInt()) // pool flags
			bytes.putInt(7 * 4 + pool.strings.size * 4 + pool.styles.size * 4) // strings offset

			val stylesOffsetPos = bytes.position()
			bytes.putInt(0) // tmp styles offset

			// string offsets
			for (i in 0..<pool.strings.size) {
				bytes.putInt(i * 4)
			}

			// style offsets
			for ((i, style) in pool.styles.iterator().withIndex()) {
				bytes.putInt(i * (style.spans.size * ArscStyle.Span.BYTES_SIZE + 4))
			}

			// strings
			val useUtf8 = pool.flags and UTF_8_FLAG != 0U
			for (string in pool.strings) {
				if (useUtf8) {
					putUtf8String(bytes, string)
				} else {
					putUtf16String(bytes, string)
				}
			}

			// alignment
			if (bytes.position() % 4 > 0) {
				bytes.putNullBytes(4 - (bytes.position() % 4))
			}

			// styles
			if (pool.styles.isNotEmpty()) {
				for (style in pool.styles) {
					// TODO: write styles
				}
				bytes.putInt(Int.MAX_VALUE)
				bytes.putInt(Int.MAX_VALUE)
			}

			// write header
			val endPos = bytes.position()
			val header = ArscHeader(
				type = ArscHeaderType.StringPool,
				headerSize = 0x001Cu, // const
				bodySize = (endPos - startPos).toUInt()
			)
			bytes.position(startPos)
			ArscHeader.write(bytes, header)
			bytes.position(endPos)

			return WrittenPool(
				strings = pool.strings.mapIndexed { i, s -> s to i }.toMap(),
				styles = pool.styles.mapIndexed { i, s -> s to i }.toMap()
			)
		}

		private fun putUtf8Length(bytes: ByteBuffer, length: UShort) {
			if (length > 0x7Fu) {
				bytes.put(((length.toInt() ushr 8) or 0x80).toByte())
			}
			bytes.put(length.toByte() and 0xFF.toByte())
		}

		private fun putUtf8String(bytes: ByteBuffer, string: String) {
			val stringBytes = string.toByteArray(Charsets.UTF_8)
			val charCount = string.chars().count().toInt()
			val byteCount = string.length

			if (charCount != byteCount)
				println("count $charCount $bytes $string")

			putUtf8Length(bytes, charCount.toUShort())
			putUtf8Length(bytes, byteCount.toUShort())
			bytes.put(stringBytes)
			bytes.put(0)
		}

		private fun putUtf16Length(bytes: ByteBuffer, length: UInt) {
			if (length > 0x7FFFu) {
				val leading2 = (length.toInt() ushr 16) or 0x8000
				bytes.put(leading2.toByte())
				bytes.put((leading2 ushr 8).toByte())
			}
			bytes.put(length.toByte())
			bytes.put((length.toInt() ushr 8).toByte())
		}

		private fun putUtf16String(bytes: ByteBuffer, string: String) {
			val stringBytes = string.toByteArray(Charsets.UTF_16LE)

			putUtf16Length(bytes, stringBytes.size.toUInt())
			bytes.put(stringBytes)
			bytes.putShort(0x0000)
		}
	}
}

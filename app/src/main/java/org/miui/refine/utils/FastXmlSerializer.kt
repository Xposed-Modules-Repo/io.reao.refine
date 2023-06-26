package org.miui.refine.utils

/*
 * Copyright (C) 2006 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.IOException
import java.io.OutputStream
import java.io.UnsupportedEncodingException
import java.io.Writer
import java.nio.ByteBuffer
import java.nio.CharBuffer
import java.nio.charset.Charset
import java.nio.charset.CharsetEncoder
import java.nio.charset.IllegalCharsetNameException
import java.nio.charset.UnsupportedCharsetException
import org.xmlpull.v1.XmlSerializer

/**
 * This is a quick and dirty implementation of XmlSerializer that isn't horribly
 * painfully slow like the normal one. It only does what is needed for the
 * specific XML files being written with it.
 */
class FastXmlSerializer : XmlSerializer {

    private val mText = CharArray(BUFFER_LEN)
    private var mPos: Int = 0

    private var mWriter: Writer? = null

    private var mOutputStream: OutputStream? = null
    private var mCharset: CharsetEncoder? = null
    private val mBytes = ByteBuffer.allocate(BUFFER_LEN)

    private var mInTag: Boolean = false

    @Throws(IOException::class)
    private fun append(c: Char) {
        var pos = mPos
        if (pos >= BUFFER_LEN - 1) {
            flush()
            pos = mPos
        }
        mText[pos] = c
        mPos = pos + 1
    }

    @Throws(IOException::class)
    private fun append(str: String, int: Int = 0, length: Int = str.length) {
        var i = int
        if (length > BUFFER_LEN) {
            val end = i + length
            while (i < end) {
                val next = i + BUFFER_LEN
                append(str, i, if (next < end) BUFFER_LEN else end - i)
                i = next
            }
            return
        }
        var pos = mPos
        if (pos + length > BUFFER_LEN) {
            flush()
            pos = mPos
        }
        str.toCharArray(mText, pos, i, i + length)
        mPos = pos + length
    }

    @Throws(IOException::class)
    private fun append(buf: CharArray, int: Int, length: Int) {
        var i = int
        if (length > BUFFER_LEN) {
            val end = i + length
            while (i < end) {
                val next = i + BUFFER_LEN
                append(buf, i, if (next < end) BUFFER_LEN else end - i)
                i = next
            }
            return
        }
        var pos = mPos
        if (pos + length > BUFFER_LEN) {
            flush()
            pos = mPos
        }
        System.arraycopy(buf, i, mText, pos, length)
        mPos = pos + length
    }

    @Throws(IOException::class)
    private fun escapeAndAppendString(string: String) {
        val n = string.length
        val ne = ESCAPE_TABLE.size.toChar()
        val escapes = ESCAPE_TABLE
        var lastPos = 0
        var pos = 0
        while (pos < n) {
            val c = string[pos]
            if (c >= ne) {
                pos++
                continue
            }
            val escape = escapes[c.code]
            if (escape == null) {
                pos++
                continue
            }
            if (lastPos < pos) {
                append(string, lastPos, pos - lastPos)
            }
            lastPos = pos + 1
            append(escape)
            pos++
        }
        if (lastPos < pos) {
            append(string, lastPos, pos - lastPos)
        }
    }

    @Throws(IOException::class)
    private fun escapeAndAppendString(buf: CharArray, start: Int, len: Int) {
        val ne = ESCAPE_TABLE.size.toChar()
        val escapes = ESCAPE_TABLE
        val end = start + len
        var lastPos = start
        var pos: Int = start
        while (pos < end) {
            val c = buf[pos]
            if (c >= ne) {
                pos++
                continue
            }
            val escape = escapes[c.code]
            if (escape == null) {
                pos++
                continue
            }
            if (lastPos < pos) {
                append(buf, lastPos, pos - lastPos)
            }
            lastPos = pos + 1
            append(escape)
            pos++
        }
        if (lastPos < pos) {
            append(buf, lastPos, pos - lastPos)
        }
    }

    @Throws(IOException::class, IllegalArgumentException::class, IllegalStateException::class)
    override fun attribute(namespace: String?, name: String, value: String): XmlSerializer {
        append(' ')
        if (namespace != null) {
            append(namespace)
            append(':')
        }
        append(name)
        append("=\"")

        escapeAndAppendString(value)
        append('"')
        return this
    }

    @Throws(IOException::class, IllegalArgumentException::class, IllegalStateException::class)
    override fun cdsect(text: String) {
        throw UnsupportedOperationException()
    }

    @Throws(IOException::class, IllegalArgumentException::class, IllegalStateException::class)
    override fun comment(text: String) {
        throw UnsupportedOperationException()
    }

    @Throws(IOException::class, IllegalArgumentException::class, IllegalStateException::class)
    override fun docdecl(text: String) {
        throw UnsupportedOperationException()
    }

    @Throws(IOException::class, IllegalArgumentException::class, IllegalStateException::class)
    override fun endDocument() {
        flush()
    }

    @Throws(IOException::class, IllegalArgumentException::class, IllegalStateException::class)
    override fun endTag(namespace: String?, name: String): XmlSerializer {
        if (mInTag) {
            append(" />\n")
        } else {
            append("</")
            if (namespace != null) {
                append(namespace)
                append(':')
            }
            append(name)
            append(">\n")
        }
        mInTag = false
        return this
    }

    @Throws(IOException::class, IllegalArgumentException::class, IllegalStateException::class)
    override fun entityRef(text: String) {
        throw UnsupportedOperationException()
    }

    @Throws(IOException::class)
    private fun flushBytes() {
        val position = mBytes.position()

        if (position > 0) {
            mBytes.flip()
            mOutputStream!!.write(mBytes.array(), 0, position)
            mBytes.clear()
        }
    }

    @Throws(IOException::class)
    override fun flush() {
        // Log.i("PackageManager", "flush mPos=" + mPos);
        if (mPos > 0) {
            if (mOutputStream != null) {
                val charBuffer = CharBuffer.wrap(mText, 0, mPos)
                var result = mCharset!!.encode(charBuffer, mBytes, true)
                while (true) {
                    if (result.isError) {
                        throw IOException(result.toString())
                    } else if (result.isOverflow) {
                        flushBytes()
                        result = mCharset!!.encode(charBuffer, mBytes, true)
                        continue
                    }
                    break
                }
                flushBytes()
                mOutputStream!!.flush()
            } else {
                mWriter!!.write(mText, 0, mPos)
                mWriter!!.flush()
            }
            mPos = 0
        }
    }

    override fun getDepth(): Int {
        throw UnsupportedOperationException()
    }

    override fun getFeature(name: String): Boolean {
        throw UnsupportedOperationException()
    }

    override fun getName(): String {
        throw UnsupportedOperationException()
    }

    override fun getNamespace(): String {
        throw UnsupportedOperationException()
    }

    @Throws(IllegalArgumentException::class)
    override fun getPrefix(namespace: String, generatePrefix: Boolean): String {
        throw UnsupportedOperationException()
    }

    override fun getProperty(name: String): Any {
        throw UnsupportedOperationException()
    }

    @Throws(IOException::class, IllegalArgumentException::class, IllegalStateException::class)
    override fun ignorableWhitespace(text: String) {
        throw UnsupportedOperationException()
    }

    @Throws(IOException::class, IllegalArgumentException::class, IllegalStateException::class)
    override fun processingInstruction(text: String) {
        throw UnsupportedOperationException()
    }

    @Throws(IllegalArgumentException::class, IllegalStateException::class)
    override fun setFeature(name: String, state: Boolean) {
        if (name == "http://xmlpull.org/v1/doc/features.html#indent-output") {
            return
        }
        throw UnsupportedOperationException()
    }

    @Throws(IOException::class, IllegalArgumentException::class, IllegalStateException::class)
    override fun setOutput(os: OutputStream?, encoding: String?) {
        if (os == null) {
            throw IllegalArgumentException()
        }
        try {
            mCharset = Charset.forName(encoding).newEncoder()
        } catch (e: IllegalCharsetNameException) {
            throw UnsupportedEncodingException(encoding).initCause(e)
        } catch (e: UnsupportedCharsetException) {
            throw UnsupportedEncodingException(encoding).initCause(e)
        }

        mOutputStream = os
    }

    @Throws(IOException::class, IllegalArgumentException::class, IllegalStateException::class)
    override fun setOutput(writer: Writer) {
        mWriter = writer
    }

    @Throws(IOException::class, IllegalArgumentException::class, IllegalStateException::class)
    override fun setPrefix(prefix: String, namespace: String) {
        throw UnsupportedOperationException()
    }

    @Throws(IllegalArgumentException::class, IllegalStateException::class)
    override fun setProperty(name: String, value: Any) {
        throw UnsupportedOperationException()
    }

    @Throws(IOException::class, IllegalArgumentException::class, IllegalStateException::class)
    override fun startDocument(encoding: String?, standalone: Boolean?) {
        val startText = "<?xml version='1.0' encoding='utf-8' standalone='"
        append(startText + (if (standalone!!) "yes" else "no") + "' ?>\n")
    }

    @Throws(IOException::class, IllegalArgumentException::class, IllegalStateException::class)
    override fun startTag(namespace: String?, name: String): XmlSerializer {
        if (mInTag) {
            append(">\n")
        }
        append('<')
        if (namespace != null) {
            append(namespace)
            append(':')
        }
        append(name)
        mInTag = true
        return this
    }

    @Throws(IOException::class, IllegalArgumentException::class, IllegalStateException::class)
    override fun text(buf: CharArray, start: Int, len: Int): XmlSerializer {
        if (mInTag) {
            append(">")
            mInTag = false
        }
        escapeAndAppendString(buf, start, len)
        return this
    }

    @Throws(IOException::class, IllegalArgumentException::class, IllegalStateException::class)
    override fun text(text: String): XmlSerializer {
        if (mInTag) {
            append(">")
            mInTag = false
        }
        escapeAndAppendString(text)
        return this
    }

    companion object {
        private val ESCAPE_TABLE = arrayOf(
            null, null, null, null, null, null, null, null, // 0-7
            null, null, null, null, null, null, null, null, // 8-15
            null, null, null, null, null, null, null, null, // 16-23
            null, null, null, null, null, null, null, null, // 24-31
            null, null, "&quot;", null, null, null, "&amp;", null, // 32-39
            null, null, null, null, null, null, null, null, // 40-47
            null, null, null, null, null, null, null, null, // 48-55
            null, null, null, null, "&lt;", null, "&gt;", null // 56-63
        )

        private const val BUFFER_LEN = 8192
    }
}
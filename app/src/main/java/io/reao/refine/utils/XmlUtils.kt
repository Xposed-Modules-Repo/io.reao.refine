package io.reao.refine.utils

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

import android.util.Xml
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParser.END_DOCUMENT
import org.xmlpull.v1.XmlPullParser.END_TAG
import org.xmlpull.v1.XmlPullParser.START_TAG
import org.xmlpull.v1.XmlPullParser.TEXT
import org.xmlpull.v1.XmlPullParserException
import org.xmlpull.v1.XmlSerializer

@Suppress("unused")
object XmlUtils {

    @Throws(XmlPullParserException::class, IOException::class)
    fun skipCurrentTag(parser: XmlPullParser) {
        val outerDepth = parser.depth
        var type: Int
        while (parser.next().also {
                type = it
            } != END_DOCUMENT && (type != END_TAG || parser.depth > outerDepth)
        ) {
            // Empty Body
        }
    }

    fun convertValueToList(value: CharSequence?, options: Array<String>, defaultValue: Int): Int {
        value?.let {
            options.forEachIndexed { index, _ ->
                if (value == options[index]) return index
            }
        }
        return defaultValue
    }

    fun convertValueToBoolean(value: CharSequence?, defaultValue: Boolean): Boolean {
        var result = false

        if (null == value) return defaultValue

        if (value == "1" || value == "true" || value == "TRUE") result = true

        return result
    }

    fun convertValueToInt(charSeq: CharSequence?, defaultValue: Int): Int {
        if (null == charSeq) return defaultValue

        val nm = charSeq.toString()

        // XXX This code is copied from Integer.decode() so we don't
        // have to instantiate an Integer!
        // var value: Int
        var sign = 1
        var index = 0
        val len = nm.length
        var base = 10
        if ('-' == nm[0]) {
            sign = -1
            index++
        }
        if ('0' == nm[index]) {
            // Quick check for a zero by itself
            if (index == len - 1) return 0
            val c = nm[index + 1]
            if ('x' == c || 'X' == c) {
                index += 2
                base = 16
            } else {
                index++
                base = 8
            }
        } else if ('#' == nm[index]) {
            index++
            base = 16
        }
        return nm.substring(index).toInt(base) * sign
    }

    fun convertValueToUnsignedInt(value: String?, defaultValue: Int): Int {
        return if (null == value) defaultValue else parseUnsignedIntAttribute(value)
    }

    @Suppress("KotlinConstantConditions")
    private fun parseUnsignedIntAttribute(charSeq: CharSequence): Int {
        val value = charSeq.toString()
        // var bits: Long
        var index = 0
        val len = value.length
        var base = 10
        if ('0' == value[index]) {
            // Quick check for zero by itself
            if (index == len - 1) return 0
            val c = value[index + 1]
            if ('x' == c || 'X' == c) { // check for hex
                index += 2
                base = 16
            } else { // check for octal
                index++
                base = 8
            }
        } else if ('#' == value[index]) {
            index++
            base = 16
        }
        return value.substring(index).toLong(base).toInt()
    }

    /**
     * Flatten a Map into an output stream as XML. The map can later be read
     * back with readMapXml().
     *
     * @param value The map to be flattened.
     * @param out Where to write the XML data.
     * @see .writeMapXml
     * @see .writeListXml
     *
     * @see .writeValueXml
     *
     * @see .readMapXml
     */
    @Throws(XmlPullParserException::class, IOException::class)
    fun writeMapXml(value: Map<*, *>?, out: OutputStream?) {
        val serializer: XmlSerializer = FastXmlSerializer()
        serializer.setOutput(out, "utf-8")
        serializer.startDocument(null, true)
        serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true)
        writeMapXml(value, null, serializer)
        serializer.endDocument()
    }

    /**
     * Flatten a List into an output stream as XML. The list can later be read
     * back with readListXml().
     *
     * @param value The list to be flattened.
     * @param out Where to write the XML data.
     * @see .writeListXml
     * @see .writeMapXml
     *
     * @see .writeValueXml
     *
     * @see .readListXml
     */
    @Throws(XmlPullParserException::class, IOException::class)
    fun writeListXml(value: List<*>?, out: OutputStream?) {
        val serializer = Xml.newSerializer()
        serializer.setOutput(out, "utf-8")
        serializer.startDocument(null, true)
        serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true)
        writeListXml(value, null, serializer)
        serializer.endDocument()
    }

    /**
     * Flatten a Map into an XmlSerializer. The map can later be read back with
     * readThisMapXml().
     *
     * @param value  The map to be flattened.
     * @param name Name attribute to include with this list's tag, or null for
     * none.
     * @param out  XmlSerializer to write the map into.
     * @see .writeMapXml
     * @see .writeListXml
     *
     * @see .writeValueXml
     *
     * @see .readMapXml
     */
    @Throws(XmlPullParserException::class, IOException::class)
    fun writeMapXml(value: Map<*, *>?, name: String?, out: XmlSerializer) {
        if (value == null) {
            out.startTag(null, "null")
            out.endTag(null, "null")
            return
        }
        val s: Set<*> = value.entries
        val i = s.iterator()
        out.startTag(null, "map")
        if (name != null) {
            out.attribute(null, "name", name)
        }
        while (i.hasNext()) {
            val (key, value) = i.next() as Map.Entry<*, *>
            writeValueXml(value, key as String?, out)
        }
        out.endTag(null, "map")
    }

    /**
     * Flatten a List into an XmlSerializer. The list can later be read back
     * with readThisListXml().
     *
     * @param value  The list to be flattened.
     * @param name Name attribute to include with this list's tag, or null for
     * none.
     * @param out  XmlSerializer to write the list into.
     * @see .writeListXml
     * @see .writeMapXml
     *
     * @see .writeValueXml
     *
     * @see .readListXml
     */
    @Throws(XmlPullParserException::class, IOException::class)
    fun writeListXml(value: List<*>?, name: String?, out: XmlSerializer) {
        if (value == null) {
            out.startTag(null, "null")
            out.endTag(null, "null")
            return
        }
        out.startTag(null, "list")
        if (name != null) {
            out.attribute(null, "name", name)
        }
        val n = value.size
        var i = 0
        while (i < n) {
            writeValueXml(value[i], null, out)
            i++
        }
        out.endTag(null, "list")
    }

    @Throws(XmlPullParserException::class, IOException::class)
    fun writeSetXml(value: Set<*>?, name: String?, out: XmlSerializer) {
        if (value == null) {
            out.startTag(null, "null")
            out.endTag(null, "null")
            return
        }
        out.startTag(null, "set")
        if (name != null) {
            out.attribute(null, "name", name)
        }
        for (v in value) {
            writeValueXml(v, null, out)
        }
        out.endTag(null, "set")
    }

    /**
     * Flatten a byte[] into an XmlSerializer. The list can later be read back
     * with readThisByteArrayXml().
     *
     * @param value  The byte array to be flattened.
     * @param name Name attribute to include with this array's tag, or null for
     * none.
     * @param out  XmlSerializer to write the array into.
     * @see .writeMapXml
     *
     * @see .writeValueXml
     */
    @Throws(XmlPullParserException::class, IOException::class)
    fun writeByteArrayXml(value: ByteArray?, name: String?, out: XmlSerializer) {
        if (value == null) {
            out.startTag(null, "null")
            out.endTag(null, "null")
            return
        }
        out.startTag(null, "byte-array")
        if (name != null) {
            out.attribute(null, "name", name)
        }
        val n = value.size
        out.attribute(null, "num", n.toString())
        val sb = StringBuilder(value.size * 2)
        for (i in 0 until n) {
            val b = value[i].toInt()
            var h = b shr 4
            sb.append(if (h >= 10) 'a'.code + h - 10 else '0'.code + h)
            h = b and 0xff
            sb.append(if (h >= 10) 'a'.code + h - 10 else '0'.code + h)
        }
        out.text(sb.toString())
        out.endTag(null, "byte-array")
    }

    /**
     * Flatten an int[] into an XmlSerializer. The list can later be read back
     * with readThisIntArrayXml().
     *
     * @param value  The int array to be flattened.
     * @param name Name attribute to include with this array's tag, or null for
     * none.
     * @param out  XmlSerializer to write the array into.
     * @see .writeMapXml
     *
     * @see .writeValueXml
     *
     * @see .readThisIntArrayXml
     */
    @Throws(XmlPullParserException::class, IOException::class)
    fun writeIntArrayXml(value: IntArray?, name: String?, out: XmlSerializer) {
        if (value == null) {
            out.startTag(null, "null")
            out.endTag(null, "null")
            return
        }
        out.startTag(null, "int-array")
        if (name != null) {
            out.attribute(null, "name", name)
        }
        val n = value.size
        out.attribute(null, "num", n.toString())
        for (i in 0 until n) {
            out.startTag(null, "item")
            out.attribute(null, "value", value[i].toString())
            out.endTag(null, "item")
        }
        out.endTag(null, "int-array")
    }

    /**
     * Flatten an object's value into an XmlSerializer. The value can later be
     * read back with readThisValueXml().
     *
     *
     * Currently supported value types are: null, String, Integer, Long, Float,
     * Double Boolean, Map, List.
     *
     * @param v    The object to be flattened.
     * @param name Name attribute to include with this value's tag, or null for
     * none.
     * @param out  XmlSerializer to write the object into.
     * @see .writeMapXml
     *
     * @see .writeListXml
     *
     * @see .readValueXml
     */
    @Throws(XmlPullParserException::class, IOException::class)
    fun writeValueXml(v: Any?, name: String?, out: XmlSerializer) {
        val typeStr: String = when (v) {
            null -> {
                out.startTag(null, "null")
                if (name != null) {
                    out.attribute(null, "name", name)
                }
                out.endTag(null, "null")
                return
            }

            is String -> {
                out.startTag(null, "string")
                if (name != null) {
                    out.attribute(null, "name", name)
                }
                out.text(v.toString())
                out.endTag(null, "string")
                return
            }

            is Int -> "int"
            is Long -> "long"
            is Float -> "float"
            is Double -> "double"
            is Boolean -> "boolean"
            is ByteArray -> {
                writeByteArrayXml(v as ByteArray?, name, out)
                return
            }

            is IntArray -> {
                writeIntArrayXml(v as IntArray?, name, out)
                return
            }

            is Map<*, *> -> {
                writeMapXml(v as Map<*, *>?, name, out)
                return
            }

            is List<*> -> {
                writeListXml(v as List<*>?, name, out)
                return
            }

            is Set<*> -> {
                writeSetXml(v as Set<*>?, name, out)
                return
            }

            is CharSequence -> {
                // XXX This is to allow us to at least write something if
                // we encounter styled text... but it means we will drop all
                // of the styling information. :(
                out.startTag(null, "string")
                if (name != null) {
                    out.attribute(null, "name", name)
                }
                out.text(v.toString())
                out.endTag(null, "string")
                return
            }

            else -> {
                throw RuntimeException("writeValueXml: unable to write value $v")
            }
        }
        out.startTag(null, typeStr)
        if (name != null) {
            out.attribute(null, "name", name)
        }
        out.attribute(null, "value", v.toString())
        out.endTag(null, typeStr)
    }

    /**
     * Read a HashMap from an InputStream containing XML. The stream can
     * previously have been written by writeMapXml().
     *
     * @param in The InputStream from which to read.
     * @return HashMap The resulting map.
     * @see .readListXml
     *
     * @see .readValueXml
     *
     * @see .readThisMapXml .see .writeMapXml
     */
    @Suppress("UNCHECKED_CAST")
    @Throws(XmlPullParserException::class, IOException::class)
    fun readMapXml(`in`: InputStream?): MutableMap<String, Any>? {
        val parser = Xml.newPullParser()
        parser.setInput(`in`, null)
        return readValueXml(parser, arrayOfNulls(1)) as MutableMap<String, Any>?
    }

    /**
     * Read an ArrayList from an InputStream containing XML. The stream can
     * previously have been written by writeListXml().
     *
     * @param in The InputStream from which to read.
     * @return ArrayList The resulting list.
     * @see .readMapXml
     *
     * @see .readValueXml
     *
     * @see .readThisListXml
     *
     * @see .writeListXml
     */
    @Throws(XmlPullParserException::class, IOException::class)
    fun readListXml(`in`: InputStream?): ArrayList<*>? {
        val parser = Xml.newPullParser()
        parser.setInput(`in`, null)
        return readValueXml(parser, arrayOfNulls(1)) as ArrayList<*>?
    }

    /**
     * Read a HashSet from an InputStream containing XML. The stream can
     * previously have been written by writeSetXml().
     *
     * @param in The InputStream from which to read.
     * @return HashSet The resulting set.
     * @throws XmlPullParserException
     * @throws java.io.IOException
     * @see .readValueXml
     *
     * @see .readThisSetXml
     *
     * @see .writeSetXml
     */
    @Throws(XmlPullParserException::class, IOException::class)
    fun readSetXml(`in`: InputStream?): HashSet<*>? {
        val parser = Xml.newPullParser()
        parser.setInput(`in`, null)
        return readValueXml(parser, arrayOfNulls(1)) as HashSet<*>?
    }

    /**
     * Read a HashMap object from an XmlPullParser. The XML data could
     * previously have been generated by writeMapXml(). The XmlPullParser must
     * be positioned *after* the tag that begins the map.
     *
     * @param parser The XmlPullParser from which to read the map data.
     * @param endTag Name of the tag that will end the map, usually "map".
     * @param name   An array of one string, used to return the name attribute of
     * the map's tag.
     * @return HashMap The newly generated map.
     * @see .readMapXml
     */
    @Throws(XmlPullParserException::class, IOException::class)
    fun readThisMapXml(
        parser: XmlPullParser,
        endTag: String,
        name: Array<String?>
    ): HashMap<Any?, Any?> {
        val map: HashMap<Any?, Any?> = HashMap()
        var eventType = parser.eventType

        do {
            if (eventType == START_TAG) {
                val value = readThisValueXml(parser, name)
                if (name[0] != null) {
                    // System.out.println("Adding to map: " + name + " -> " +
                    // val);
                    map[name[0]] = value
                } else {
                    throw XmlPullParserException("Map value without name attribute: " + parser.name)
                }
            } else if (eventType == END_TAG) {
                if (parser.name == endTag) {
                    return map
                }
                throw XmlPullParserException("Expected " + endTag + " end tag at: " + parser.name)
            }

            eventType = parser.next()
        } while (eventType != END_DOCUMENT)

        throw XmlPullParserException("Document ended before $endTag end tag")
    }

    /**
     * Read an ArrayList object from an XmlPullParser. The XML data could
     * previously have been generated by writeListXml(). The XmlPullParser must
     * be positioned *after* the tag that begins the list.
     *
     * @param parser The XmlPullParser from which to read the list data.
     * @param endTag Name of the tag that will end the list, usually "list".
     * @param name   An array of one string, used to return the name attribute of
     * the list's tag.
     * @return HashMap The newly generated list.
     * @see .readListXml
     */
    @Throws(XmlPullParserException::class, IOException::class)
    fun readThisListXml(parser: XmlPullParser, endTag: String, name: Array<String?>): ArrayList<*> {
        val list: ArrayList<Any?> = ArrayList()
        var eventType = parser.eventType
        do {
            if (eventType == START_TAG) {
                val value = readThisValueXml(parser, name)
                list.add(value)
                // System.out.println("Adding to list: " + val);
            } else if (eventType == END_TAG) {
                if (parser.name == endTag) {
                    return list
                }
                throw XmlPullParserException("Expected " + endTag + " end tag at: " + parser.name)
            }

            eventType = parser.next()
        } while (eventType != END_DOCUMENT)

        throw XmlPullParserException("Document ended before $endTag end tag")
    }

    /**
     * Read a HashSet object from an XmlPullParser. The XML data could
     * previously have been generated by writeSetXml(). The XmlPullParser must
     * be positioned *after* the tag that begins the set.
     *
     * @param parser The XmlPullParser from which to read the set data.
     * @param endTag Name of the tag that will end the set, usually "set".
     * @param name   An array of one string, used to return the name attribute of
     * the set's tag.
     * @return HashSet The newly generated set.
     * @throws XmlPullParserException
     * @throws java.io.IOException
     * @see .readSetXml
     */
    @Throws(XmlPullParserException::class, IOException::class)
    fun readThisSetXml(parser: XmlPullParser, endTag: String, name: Array<String?>): HashSet<*> {
        val set: HashSet<Any?> = HashSet()
        var eventType = parser.eventType

        do {
            if (eventType == START_TAG) {
                val value = readThisValueXml(parser, name)
                set.add(value)
                // System.out.println("Adding to set: " + val);
            } else if (eventType == END_TAG) {
                if (parser.name == endTag) {
                    return set
                }
                throw XmlPullParserException("Expected " + endTag + " end tag at: " + parser.name)
            }
            eventType = parser.next()
        } while (eventType != END_DOCUMENT)

        throw XmlPullParserException("Document ended before $endTag end tag")
    }

    /**
     * Read an int[] object from an XmlPullParser. The XML data could previously
     * have been generated by writeIntArrayXml(). The XmlPullParser must be
     * positioned *after* the tag that begins the list.
     *
     * @param parser The XmlPullParser from which to read the list data.
     * @param endTag Name of the tag that will end the list, usually "list".
     * @param name   An array of one string, used to return the name attribute of
     * the list's tag.
     * @return Returns a newly generated int[].
     * @see .readListXml
     */
    @Suppress("UNUSED_PARAMETER")
    @Throws(XmlPullParserException::class, IOException::class)
    fun readThisIntArrayXml(
        parser: XmlPullParser,
        endTag: String,
        name: Array<String?>?
    ): IntArray {
        val num: Int = try {
            parser.getAttributeValue(null, "num").toInt()
        } catch (e: NullPointerException) {
            throw XmlPullParserException("Need num attribute in byte-array")
        } catch (e: NumberFormatException) {
            throw XmlPullParserException("Not a number in num attribute in byte-array")
        }

        val array = IntArray(num)
        var i = 0
        var eventType = parser.eventType
        do {
            if (eventType == START_TAG) {
                if (parser.name == "item") {
                    try {
                        array[i] = parser.getAttributeValue(null, "value").toInt()
                    } catch (e: NullPointerException) {
                        throw XmlPullParserException("Need value attribute in item")
                    } catch (e: NumberFormatException) {
                        throw XmlPullParserException("Not a number in value attribute in item")
                    }
                } else {
                    throw XmlPullParserException("Expected item tag at: " + parser.name)
                }
            } else if (eventType == END_TAG) {
                when (parser.name) {
                    endTag -> return array
                    "item" -> i++
                    else ->
                        throw XmlPullParserException(
                            "Expected " + endTag + " end tag at: " + parser.name
                        )
                }
            }
            eventType = parser.next()
        } while (eventType != END_DOCUMENT)

        throw XmlPullParserException("Document ended before $endTag end tag")
    }

    /**
     * Read a flattened object from an XmlPullParser. The XML data could
     * previously have been written with writeMapXml(), writeListXml(), or
     * writeValueXml(). The XmlPullParser must be positioned *at* the tag
     * that defines the value.
     *
     * @param parser The XmlPullParser from which to read the object.
     * @param name   An array of one string, used to return the name attribute of
     * the value's tag.
     * @return Object The newly generated value object.
     * @see .readMapXml
     *
     * @see .readListXml
     *
     * @see .writeValueXml
     */
    @Throws(XmlPullParserException::class, IOException::class)
    fun readValueXml(parser: XmlPullParser, name: Array<String?>): Any? {
        var eventType = parser.eventType
        do {
            when (eventType) {
                START_TAG ->
                    return readThisValueXml(parser, name)

                END_TAG ->
                    throw XmlPullParserException("Unexpected end tag at: " + parser.name)

                TEXT ->
                    throw XmlPullParserException("Unexpected text: " + parser.text)

                else -> eventType = parser.next()
            }
        } while (eventType != END_DOCUMENT)

        throw XmlPullParserException("Unexpected end of document")
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun readThisValueXml(parser: XmlPullParser, name: Array<String?>): Any? {
        val valueName = parser.getAttributeValue(null, "name")
        val tagName = parser.name

        // System.out.println("Reading this value tag: " + tagName + ", name=" + valueName);
        val res: Any?
        when (tagName) {
            "null" -> res = null
            "string" -> {
                var value: String? = ""
                var eventType: Int
                while (parser.next().also { eventType = it } != END_DOCUMENT) {
                    when (eventType) {
                        END_TAG -> {
                            if (parser.name == "string") {
                                name[0] = valueName
                                // System.out.println("Returning value for " + valueName + ": " + value);
                                return value
                            }
                            throw XmlPullParserException(
                                "Unexpected end tag in <string>: " + parser.name
                            )
                        }

                        TEXT -> {
                            value += parser.text
                        }

                        START_TAG -> {
                            throw XmlPullParserException(
                                "Unexpected start tag in <string>: " + parser.name
                            )
                        }
                    }
                }
                throw XmlPullParserException("Unexpected end of document in <string>")
            }

            "int" ->
                res = parser.getAttributeValue(null, "value").toInt()

            "long" ->
                res = java.lang.Long.valueOf(parser.getAttributeValue(null, "value"))

            "float" ->
                res = parser.getAttributeValue(null, "value")

            "double" ->
                res = parser.getAttributeValue(null, "value")

            "boolean" ->
                res = java.lang.Boolean.valueOf(parser.getAttributeValue(null, "value"))

            "int-array" -> {
                parser.next()
                res = readThisIntArrayXml(parser, "int-array", name)
                name[0] = valueName
                // System.out.println("Returning value for " + valueName + ": " + res);
                return res
            }

            "map" -> {
                parser.next()
                res = readThisMapXml(parser, "map", name)
                name[0] = valueName
                // System.out.println("Returning value for " + valueName + ": " + res);
                return res
            }

            "list" -> {
                parser.next()
                res = readThisListXml(parser, "list", name)
                name[0] = valueName
                // System.out.println("Returning value for " + valueName + ": " + res);
                return res
            }

            "set" -> {
                parser.next()
                res = readThisSetXml(parser, "set", name)
                name[0] = valueName
                // System.out.println("Returning value for " + valueName + ": " + res);
                return res
            }

            else -> {
                throw XmlPullParserException("Unknown tag: $tagName")
            }
        }

        // Skip through to end tag.
        var eventType: Int
        while (parser.next().also { eventType = it } != END_DOCUMENT) {
            when (eventType) {
                END_TAG -> {
                    if (parser.name == tagName) {
                        name[0] = valueName
                        // System.out.println("Returning value for " + valueName +
                        // ": " + res);
                        return res
                    }
                    throw XmlPullParserException(
                        "Unexpected end tag in <" + tagName + ">: " + parser.name
                    )
                }

                TEXT ->
                    throw XmlPullParserException(
                        "Unexpected text in <" + tagName + ">: " + parser.name
                    )

                START_TAG ->
                    throw XmlPullParserException(
                        "Unexpected start tag in <" + tagName + ">: " + parser.name
                    )
            }
        }

        throw XmlPullParserException("Unexpected end of document in <$tagName>")
    }

    @Throws(XmlPullParserException::class, IOException::class)
    fun beginDocument(parser: XmlPullParser, firstElementName: String) {
        var type: Int

        while (parser.next().also { type = it } != START_TAG && type != END_DOCUMENT) {
            // Empty Body
        }

        if (type != START_TAG) {
            throw XmlPullParserException("No start tag found")
        }

        if (parser.name != firstElementName) {
            throw XmlPullParserException(
                "Unexpected start tag: found " + parser.name + ", expected " + firstElementName
            )
        }
    }

    @Throws(XmlPullParserException::class, IOException::class)
    fun nextElement(parser: XmlPullParser) {
        var type: Int
        while (parser.next().also { type = it } != START_TAG && type != END_DOCUMENT) {
            // Empty Body
        }
    }
}
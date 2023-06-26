package org.miui.refine.model

import com.topjohnwu.superuser.Shell
import com.topjohnwu.superuser.io.SuFile
import com.topjohnwu.superuser.io.SuFileInputStream
import com.topjohnwu.superuser.io.SuFileOutputStream
import org.miui.refine.utils.XmlUtils.readMapXml
import org.miui.refine.utils.XmlUtils.writeMapXml
import org.xmlpull.v1.XmlPullParserException
import timber.log.Timber
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader


class RemoteSharePreferences(packageName: String, path: String) {

    private var path: String? = null
    private var packageName: String? = null
    private var itemList: MutableMap<String, Any>? = null

    init {
        this.path = path
        this.packageName = packageName
        val dataFile = SuFile(path)
        val reader = InputStreamReader(SuFileInputStream.open(dataFile))

        Timber.d("dataFile: ${dataFile.exists()} isRoot: ${Shell.isAppGrantedRoot()}")

        if (!dataFile.exists()) {
            Timber.d("dataFile not exists")
            throw RuntimeException("dataFile not exists")
        }

        Timber.d(reader.readText())

        fromXml(SuFileInputStream.open(dataFile))
    }

    private fun fromXml(input: InputStream) {

        try {
            val input: InputStream = ByteArrayInputStream(input.readBytes())
            val map: Map<String, Any>? = readMapXml(input)
            input.close()
            if (map != null) {
                itemList = map as MutableMap<String, Any>
            }
        } catch (ignored: XmlPullParserException) {
            Timber.d(ignored)
        } catch (ignored: IOException) {
            Timber.d(ignored)
        }
    }

    private fun toXml(): String {
        val out = ByteArrayOutputStream()
        try {
            writeMapXml(itemList, out)
        } catch (ignored: XmlPullParserException) {
        } catch (ignored: IOException) {
        }
        return out.toString()
    }

    fun getBoolean(key: String, default: Boolean): Boolean {
        return itemList!!.get(key) as Boolean? ?: default
    }

    fun putBoolean(key: String, value: Boolean) {
        Timber.d("putBoolean $key $value")

        itemList!![key] = value

        commit()
    }

    fun commit() {
        val xml = toXml()
        Timber.d("save" + xml)

        val dataFile = SuFile(path)

        val op = SuFileOutputStream.open(dataFile)
        op.write(xml.toByteArray())
        op.close()

        Shell.cmd("am force-stop ${packageName!!}").exec()
    }

}
package org.miui.refine.utils.extensions

import android.content.pm.ApplicationInfo
import org.lsposed.hiddenapibypass.HiddenApiBypass
import java.lang.reflect.Field


class ApplicationInfoHidden() {
    companion object {
        const val PRIVATE_FLAG_HIDDEN = 1 shl 0
        const val PRIVATE_FLAG_PRIVILEGED = 1 shl 3
    }

}


fun ApplicationInfo.getPrivateFlags(): Int {
    var allInstanceFields = HiddenApiBypass.getInstanceFields(ApplicationInfo::class.java)

    val field = allInstanceFields
        .stream()
        .filter { item ->
            (item as Field).name.equals("privateFlags")
        }
        .findFirst().get() as Field

    field.isAccessible = true
    val value = field.get(this) as Int

    return value
}

fun ApplicationInfo.isSystemApp(): Boolean {
    return HiddenApiBypass.invoke(ApplicationInfo::class.java, this, "isSystemApp") as Boolean
}
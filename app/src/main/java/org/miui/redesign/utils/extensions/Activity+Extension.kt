package org.miui.redesign.utils.extensions

import android.app.Activity
import org.lsposed.hiddenapibypass.HiddenApiBypass

fun Activity.getLaunchedFromPackage(): String {
    return HiddenApiBypass.invoke(Activity::class.java, this, "getLaunchedFromPackage") as String
}

fun Activity.getLaunchedFromUid(): Int {
    return HiddenApiBypass.invoke(Activity::class.java, this, "getLaunchedFromUid") as Int
}
package org.miui.redesign.utils

import android.util.Log


object Log {

    private const val TAG = "MIUI-Redesign"

    fun d(tag: String, msg: String) {
        Log.d(TAG,"$tag: $msg")
    }

}
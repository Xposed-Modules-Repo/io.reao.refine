package org.miui.refine.utils

import android.util.Log


object Log {

    private const val TAG = "MIUI-refine"

    fun d(tag: String, msg: String) {
        Log.d(TAG,"$tag: $msg")
    }

}
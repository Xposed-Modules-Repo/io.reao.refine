package org.miui.refine.utils

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager

object LauncherUtil {

    private val LAUNCHER_ACTIVITY = "org.miui.refine.MainActivity"

    fun showLauncherStatus(context: Context, isShow: Boolean) {
        val pm: PackageManager = context.packageManager
        val show = if (isShow) {
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED
        } else {
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED
        }

        pm.setComponentEnabledSetting(
            ComponentName(context, LAUNCHER_ACTIVITY),
            show,
            PackageManager.DONT_KILL_APP
        )
    }

    fun getLauncherStatus(context: Context): Boolean {
        val pm = context.packageManager
        val state = pm.getComponentEnabledSetting(
            ComponentName(context, LAUNCHER_ACTIVITY)
        )


        return state != PackageManager.COMPONENT_ENABLED_STATE_DISABLED

    }


}
package org.miui.redesign.packageinstaller.model

import android.content.pm.PermissionInfo
import android.graphics.drawable.Drawable

data class AppInfo(
    val label: CharSequence?,
    val icon: Drawable?,

    val packageName: String,
    val versionName: String,
    val versionCode: Long,

    val permissions: Array<PermissionInfo>
)
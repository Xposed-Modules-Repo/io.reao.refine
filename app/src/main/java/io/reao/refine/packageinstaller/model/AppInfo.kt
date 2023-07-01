package io.reao.refine.packageinstaller.model

import android.content.pm.PermissionInfo
import android.graphics.drawable.Drawable

data class AppInfo(
    val label: CharSequence?,
    val icon: Drawable?,

    val packageName: String,
    val versionName: String,
    val versionCode: Long,

    val minSdkVersion: Int,
    val targetSdkVersion: Int,
    val compileSdkVersion: Int,

    val size: Double?,

    val permissions: Array<PermissionInfo>,
)

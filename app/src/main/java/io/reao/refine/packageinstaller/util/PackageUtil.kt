package io.reao.refine.packageinstaller.util

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.PackageManager.NameNotFoundException
import android.content.res.AssetManager
import android.content.res.Resources
import android.graphics.drawable.Drawable
import org.lsposed.hiddenapibypass.HiddenApiBypass
import io.reao.refine.packageinstaller.model.AppInfo
import io.reao.refine.packageinstaller.model.AppSnippet
import io.reao.refine.utils.Log
import java.io.File

object PackageUtil {

    fun getPackageInfo(context: Context, sourceFile: File, flags: Int): PackageInfo? {
        return try {
            context.packageManager.getPackageArchiveInfo(sourceFile.absolutePath, flags)
        } catch (e: Exception) {
            Log.d("PackageUtil", "getPackageInfo error: ${e.message}")
            null
        }
    }

    fun getAppSnippet(
        context: Context,
        applicationInfo: ApplicationInfo,
        sourceFile: File
    ): AppSnippet? {
        Log.d("PackageUtil", "getAppSnippet applicationInfo: $applicationInfo")
        Log.d("PackageUtil", "getAppSnippet sourceFile: $sourceFile")

        val pRes = context.resources
        val assmgr: AssetManager =
            HiddenApiBypass.newInstance(Class.forName("android.content.res.AssetManager")) as AssetManager

        HiddenApiBypass.invoke(
            AssetManager::class.java,
            assmgr,
            "addAssetPath",
            sourceFile.absolutePath
        )


        val res = Resources(assmgr, pRes.displayMetrics, pRes.configuration)

        var label: CharSequence? = null
        if (applicationInfo.labelRes != 0) {
            try {
                label = res.getText(applicationInfo.labelRes)
            } catch (e: Exception) {
                Log.d("PackageUtil", "getAppSnippet Label error: ${e.message}")
            }
        }

        if (label == null) {
            label =
                if (applicationInfo.nonLocalizedLabel != null) applicationInfo.nonLocalizedLabel else applicationInfo.packageName
        }

        Log.d("PackageUtil", "getAppSnippet label: $label")

        var icon: Drawable? = null

        try {
            if (applicationInfo.icon != 0) {
                icon = res.getDrawable(applicationInfo.icon)
            }
        } catch (e: Exception) {
            Log.d("PackageUtil", "getAppSnippet icon error: ${e.message}")

        }

        return AppSnippet(
            label = label,
            icon = icon
        )
    }

    fun getInstalledAppInfo(pm: PackageManager, packageName: String): AppInfo? {

        val packageInfo = try {
            pm.getPackageInfo(packageName, PackageManager.GET_PERMISSIONS)
        } catch (e: NameNotFoundException) {
            return null
        }
        val applicationInfo = packageInfo.applicationInfo

        val versionName = packageInfo.versionName
        val versionCode = packageInfo.longVersionCode

        val minSdkVersion = applicationInfo.minSdkVersion
        val targetSdkVersion = applicationInfo.targetSdkVersion
        val compileSdkVersion = applicationInfo.compileSdkVersion

        return  AppInfo(
            label = applicationInfo.loadLabel(pm),
            icon = applicationInfo.loadIcon(pm),

            packageName = packageName,
            versionName = versionName,
            versionCode = versionCode,

            minSdkVersion = minSdkVersion,
            targetSdkVersion = targetSdkVersion,
            compileSdkVersion = compileSdkVersion,

            size = null,

            permissions = packageInfo.permissions
        )
    }

}
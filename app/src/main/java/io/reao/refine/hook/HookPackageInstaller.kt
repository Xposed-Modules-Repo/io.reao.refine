package io.reao.refine.hook

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XSharedPreferences
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import io.reao.refine.BuildConfig


object HookPackageInstaller {

    fun hook(lpparam: XC_LoadPackage.LoadPackageParam) {
        val modulePref = XSharedPreferences(BuildConfig.APPLICATION_ID, "model_pref")

        // check if the replace packageInstaller is enabled
        if (modulePref.file.canRead() && modulePref.getBoolean(
                "REPLACE_PACKAGE_INSTALLER", true
            )
        ) {
            XposedHelpers.findAndHookMethod(
                "com.miui.packageInstaller.InstallStart",
                lpparam.classLoader,
                "onCreate",
                Bundle::class.java,
                hookOnCreate()
            )
        }
    }

    private fun hookOnCreate(): XC_MethodReplacement {

        return object : XC_MethodReplacement() {

            override fun replaceHookedMethod(param: MethodHookParam): Any? {

                XposedBridge.log("com.miui.packageInstaller.InstallStart : onCreate")

                var activity = param.thisObject as Activity

                val intent = activity.intent

                val newIntent = Intent("android.intent.action.VIEW")
                newIntent.setClassName(
                    "io.reao.refine", "io.reao.refine.packageinstaller.PackageInstallerActivity"
                )
                newIntent.setDataAndType(intent.data, intent.type)
                newIntent.flags =
                    Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                activity.startActivity(newIntent)

                // bypass the SuperNotCalledException https://github.com/rovo89/XposedBridge/issues/201
                XposedHelpers.findField(
                    Activity::class.java, "mCalled"
                ).isAccessible = true
                XposedHelpers.setBooleanField(activity, "mCalled", true)

                activity.setResult(Activity.RESULT_OK);
                activity.finish()

                return null
            }

        }
    }

}
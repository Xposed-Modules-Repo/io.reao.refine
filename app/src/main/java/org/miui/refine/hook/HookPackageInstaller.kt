package org.miui.refine.hook

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.os.Bundle
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import org.miui.refine.utils.Log


object HookPackageInstaller {

    fun hook(lpparam: XC_LoadPackage.LoadPackageParam) {
        XposedHelpers.findAndHookMethod(
            "com.miui.packageInstaller.InstallStart",
            lpparam.classLoader,
            "onCreate",
            Bundle::class.java,
            hookOnCreate()
        )

        XposedHelpers.findAndHookMethod(
            "com.android.packageinstaller.InstallerApplication",
            lpparam.classLoader,
            "onCreate",
            hookInstallerApplicationOnCreate()
        )
    }

    private fun hookOnCreate(): XC_MethodReplacement {
        return object : XC_MethodReplacement() {
            override fun replaceHookedMethod(param: MethodHookParam): Any? {
                var activity = param.thisObject as Activity

                val intent = activity.intent

                val newIntent = Intent("android.intent.action.VIEW")
                newIntent.setClassName(
                    "org.miui.refine",
                    "org.miui.refine.packageinstaller.PackageInstallerActivity"
                )
                newIntent.setDataAndType(intent.data, intent.type)
                newIntent.flags =
                    Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                activity.startActivity(newIntent)

                // bypass the SuperNotCalledException https://github.com/rovo89/XposedBridge/issues/201
                XposedHelpers.findField(
                    Activity::class.java,
                    "mCalled"
                ).isAccessible = true
                XposedHelpers.setBooleanField(activity, "mCalled", true)

                activity.setResult(Activity.RESULT_OK);
                activity.finish()

                return null
            }
        }
    }

    private fun hookInstallerApplicationOnCreate(): XC_MethodReplacement {
        return object : XC_MethodReplacement() {
            override fun replaceHookedMethod(param: MethodHookParam): Any? {
                Log.d("HookPackageInstaller", "hookInstallerApplicationOnCreate)")
                var app = param.thisObject as Application
                return null
            }
        }
    }

}
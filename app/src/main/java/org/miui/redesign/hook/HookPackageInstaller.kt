package org.miui.redesign.hook

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.os.Bundle
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import org.miui.redesign.utils.Log
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader


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


                installPkg()

                val newIntent = Intent("android.intent.action.VIEW")
                newIntent.setClassName(
                    "org.miui.redesign",
                    "org.miui.redesign.packageinstaller.InstallPackageActivity"
                )
                newIntent.setDataAndType(intent.data, intent.type)
                newIntent.flags =
                    Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                activity.startActivity(newIntent)

                // bypass the SuperNotCalledException https://github.com/rovo89/XposedBridge/issues/201
                XposedHelpers.findField(
                    Activity::
                    class.java, "mCalled"
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


    fun installPkg() {
        Log.d("installPkg", "Start")
        try {
            // 执行 'ls' 命令
            //val process = Runtime.getRuntime().exec("pm install /data/local/tmp/cool.apk")
            val process = Runtime.getRuntime().exec("id")

            // 获取命令的输出
            val reader = BufferedReader(
                InputStreamReader(process.inputStream)
            )

            // 读取输出
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                Log.d("installPkg", line!!)
            }

            // 确保子进程已经结束
            process.waitFor()
        } catch (e: Exception) {
            Log.d("installPkg", "Error" + e.message)
            e.printStackTrace()
        }

        Log.d("installPkg", "End")

    }

}
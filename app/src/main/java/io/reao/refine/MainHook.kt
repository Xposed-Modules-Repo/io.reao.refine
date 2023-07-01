package io.reao.refine

import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.callbacks.XC_LoadPackage
import io.reao.refine.hook.HookPackageInstaller
import io.reao.refine.hook.HookSelfModule
import io.reao.refine.BuildConfig

private const val PACKAGE_MIUI_PACKAGEINSTALLER = "com.miui.packageinstaller"

class MainHook : IXposedHookLoadPackage {
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {

        XposedBridge.log("Refine is loaded in ${lpparam.packageName}")

        when (lpparam.packageName) {
            PACKAGE_MIUI_PACKAGEINSTALLER -> {
                HookPackageInstaller.hook(lpparam)
            }

            BuildConfig.APPLICATION_ID -> {
                HookSelfModule.hook(lpparam)
            }

            else -> {
                return
            }
        }
    }
}
package org.miui.refine

import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.callbacks.XC_LoadPackage
import org.miui.refine.hook.HookPackageInstaller
import org.miui.refine.hook.HookSelfModule

private const val PACKAGE_MIUI_PACKAGEINSTALLER = "com.miui.packageinstaller"
private const val PACKAGE_REFINE = "org.miui.refine"

class MainHook : IXposedHookLoadPackage {
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {

        XposedBridge.log("Refine is loaded in ${lpparam.packageName}")

        when (lpparam.packageName) {
            PACKAGE_MIUI_PACKAGEINSTALLER -> {
                HookPackageInstaller.hook(lpparam)
            }

            PACKAGE_REFINE -> {
                HookSelfModule.hook(lpparam)
            }

            else -> {
                return
            }
        }
    }
}
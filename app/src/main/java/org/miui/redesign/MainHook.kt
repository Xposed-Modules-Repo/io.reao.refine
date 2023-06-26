package org.miui.redesign

import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.callbacks.XC_LoadPackage
import org.miui.redesign.hook.HookPackageInstaller

private const val PACKAGE_MIUI_PACKAGEINSTALLER = "com.miui.packageinstaller"
class MainHook : IXposedHookLoadPackage {
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {

        when (lpparam.packageName) {
            PACKAGE_MIUI_PACKAGEINSTALLER -> {
                HookPackageInstaller.hook(lpparam)
            }
            else -> {
                return
            }
        }

    }
}
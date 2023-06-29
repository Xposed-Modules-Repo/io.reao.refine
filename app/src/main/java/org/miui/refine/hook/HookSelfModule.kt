package org.miui.refine.hook

import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

object HookSelfModule {
    fun hook(lpparam: XC_LoadPackage.LoadPackageParam) {

        XposedHelpers.findAndHookMethod(
            "org.miui.refine.utils.ModuleUtil",
            lpparam.classLoader,
            "isModuleActive",
            hookCheckModuleStatus()
        )
    }

    private fun hookCheckModuleStatus(): XC_MethodHook {
        return object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {
                super.beforeHookedMethod(param)

                param.result = true
            }
        }
    }
}
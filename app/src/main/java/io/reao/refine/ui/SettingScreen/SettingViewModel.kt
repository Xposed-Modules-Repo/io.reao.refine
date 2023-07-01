package io.reao.refine.ui.SettingScreen

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.topjohnwu.superuser.Shell
import io.reao.refine.model.RemoteSharePreferences
import io.reao.refine.utils.LauncherUtil
import io.reao.refine.utils.ModuleUtil
import timber.log.Timber

class SettingViewModel : ViewModel() {

    var isGrantedRoot = mutableStateOf(false)
    var isXposedActive = mutableStateOf(false)

    var isSetEnable = mutableStateOf(false)

    var replacePackageInstaller = mutableStateOf(true)

    var enableUSBinstall = mutableStateOf(false)
    var verifyUSBinstall = mutableStateOf(false)
    var hideLauncherStatus = mutableStateOf(false)

    var CenterSharePreferences: RemoteSharePreferences? = null
    var modelPref: SharedPreferences? = null

    @SuppressLint("SdCardPath")
    fun loadStatus(mViewModel: SettingViewModel, context: Context) {

        Shell.cmd("su").exec()

        mViewModel.isGrantedRoot.value = Shell.isAppGrantedRoot() == true
        mViewModel.isXposedActive.value = ModuleUtil.isModuleActive()

        if (!mViewModel.isXposedActive.value || !mViewModel.isGrantedRoot.value) {
            mViewModel.isSetEnable.value = false
            return
        } else {
            mViewModel.isSetEnable.value = true
        }

        mViewModel.modelPref = try {
            context.getSharedPreferences(
                "model_pref", Context.MODE_WORLD_READABLE
            )
        } catch (e: SecurityException) {
            Timber.d("module's not loading")
            null
        }

        mViewModel.replacePackageInstaller.value =
            mViewModel.modelPref!!.getBoolean("REPLACE_PACKAGE_INSTALLER", true)

        Timber.d("isGrantedRoot  : ${Shell.isAppGrantedRoot()} ${Shell.getShell().isRoot} ${mViewModel.isXposedActive.value}}")

        mViewModel.CenterSharePreferences = RemoteSharePreferences(
            "com.miui.securitycenter",
            "/data/data/com.miui.securitycenter/shared_prefs/remote_provider_preferences.xml"
        )
        val adb_install =
            mViewModel.CenterSharePreferences!!.getBoolean("security_adb_install_enable", false)

        Timber.d("security_adb_install_enable  : $adb_install")

        mViewModel.enableUSBinstall.value = adb_install

        val install = mViewModel.CenterSharePreferences!!.getBoolean(
            "permcenter_install_intercept_enabled", true
        )

        mViewModel.verifyUSBinstall.value = !install
        mViewModel.hideLauncherStatus.value = !LauncherUtil.getLauncherStatus(context = context)


    }

    fun changeReplaceInstaller(newState: Boolean) {
        val editor = modelPref!!.edit()
        editor.putBoolean("REPLACE_PACKAGE_INSTALLER", newState)
        editor.apply()
        replacePackageInstaller.value = newState
    }

}
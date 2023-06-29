package org.miui.refine.ui.SettingScreen

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import org.miui.refine.model.RemoteSharePreferences

class SettingViewModel : ViewModel() {

    var isGrantedRoot = mutableStateOf(false)
    var isXposedActive = mutableStateOf(false)

    var replacePackageInstaller = mutableStateOf(true)

    var enableUSBinstall = mutableStateOf(false)
    var verifyUSBinstall = mutableStateOf(false)
    var hideLauncherStatus = mutableStateOf(false)

    var CenterSharePreferences: RemoteSharePreferences? = null
}
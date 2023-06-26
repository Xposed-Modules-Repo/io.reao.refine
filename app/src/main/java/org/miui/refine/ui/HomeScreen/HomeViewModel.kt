package org.miui.refine.ui.HomeScreen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import org.miui.refine.model.RemoteSharePreferences

class HomeViewModel : ViewModel() {

    var isGrantedRoot = mutableStateOf(true)
    var isXposedActive = mutableStateOf(true)

    var replacePackageInstaller = mutableStateOf(true)

    var enableUSBinstall = mutableStateOf(true)
    var verifyUSBinstall = mutableStateOf(true)

    var CenterSharePreferences: RemoteSharePreferences? = null
}
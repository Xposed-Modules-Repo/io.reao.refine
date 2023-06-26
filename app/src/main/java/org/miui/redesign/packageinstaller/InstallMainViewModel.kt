package org.miui.redesign.packageinstaller

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import org.miui.redesign.packageinstaller.model.AppInfo

class InstallMainViewModel : ViewModel() {

    var loading = mutableStateOf(true)
    var needRequestPermission = mutableStateOf(false)
    var installResult = mutableStateOf(false)

    var appInfo = mutableStateOf(
        AppInfo(
            null,
            null,
            "",
            "",
            0,
            emptyArray()
        )
    )

}
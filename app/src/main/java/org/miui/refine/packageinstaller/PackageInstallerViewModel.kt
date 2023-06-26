package org.miui.refine.packageinstaller

import android.content.pm.PackageInfo
import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import org.miui.refine.packageinstaller.model.AppInfo
import java.io.File

class PackageInstallerViewModel : ViewModel() {

    var loading = mutableStateOf(true)
    var installing = mutableStateOf(false)

    var needRequestPermission = mutableStateOf(false)
    var installResult = mutableStateOf(false)

    var mRealPackageUri: Uri? = null
    var mCachePackageFile: File? = null

    var newAppInfo = mutableStateOf(
        AppInfo(
            null,
            null,
            "",
            "",
            0,
            0.0,
            emptyArray()
        )
    )

    var oldAppInfo: PackageInfo? = null

}
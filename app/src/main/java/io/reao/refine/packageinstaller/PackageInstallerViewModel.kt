package io.reao.refine.packageinstaller

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import io.reao.refine.packageinstaller.model.AppInfo
import io.reao.refine.packageinstaller.util.InstallUtil
import io.reao.refine.packageinstaller.util.PackageUtil
import io.reao.refine.utils.Log
import java.io.File

class PackageInstallerViewModel : ViewModel() {

    var loading = mutableStateOf(true)
    var installing = mutableStateOf(false)

    var needRequestPermission = mutableStateOf(false)
    var installResult = mutableStateOf(false)

    var mRealPackageUri: Uri? = null
    var mCachePackageFile: File? = null

    var normalChangelog = mutableListOf<String>()
    var warnChangelog = mutableListOf<String>()

    var toastContent = mutableStateOf("")

    var newAppInfo = mutableStateOf(
        AppInfo(
            null,
            null,
            "",
            "",
            0,
            0,
            0,
            0,
            0.0,
            emptyArray(),
        )
    )

    var installedAppInfo = mutableStateOf(
        AppInfo(
            null,
            null,
            "",
            "",
            0,
            0,
            0,
            0,
            0.0,
            emptyArray(),
        )
    )

    fun prepare(context: Context, intent: Intent) {

        viewModelScope.launch {
            if (intent.scheme == ContentResolver.SCHEME_CONTENT) {

                mRealPackageUri = intent.data!!

                mCachePackageFile = InstallUtil.copyPackage2Cache(
                    mContext = context, mRealPackageUri = mRealPackageUri!!
                )

                if (mCachePackageFile != null) {
                    InstallUtil.processPackageUri(
                        mContext = context, packageUri = Uri.fromFile(mCachePackageFile!!)
                    ).also {
                        if (it != null) {
                            newAppInfo.value = it
                            loading.value = false
                        }
                        Log.d("InstallStart", "appSnippet: $it")
                    }

                    PackageUtil.getInstalledAppInfo(
                        pm = context.packageManager, packageName = newAppInfo.value.packageName
                    ).also {
                        if (it != null) {
                            installedAppInfo.value = it
                            loadDiff()
                        }
                    }
                }

            } else {
                toastContent.value = "Unsupported Uri Scheme"
            }
        }
    }


    private fun loadDiff() {
        normalChangelog = mutableListOf()
        warnChangelog = mutableListOf()

        newAppInfo.value = newAppInfo.value.copy(
            versionName = installedAppInfo.value.versionName + "->" + newAppInfo.value.versionName,
        )

        if (installedAppInfo.value.versionCode <= newAppInfo.value.versionCode) {
            normalChangelog.add("versionCode: ${installedAppInfo.value.versionCode} -> ${newAppInfo.value.versionCode}")
        } else {
            warnChangelog.add("versionCode: ${installedAppInfo.value.versionCode} -> ${newAppInfo.value.versionCode}")
        }

        if (installedAppInfo.value.targetSdkVersion != newAppInfo.value.targetSdkVersion) {
            normalChangelog.add(
                "targetSDK: ${installedAppInfo.value.targetSdkVersion} -> ${newAppInfo.value.targetSdkVersion}"
            )
        }

        if (installedAppInfo.value.minSdkVersion != newAppInfo.value.minSdkVersion) {
            normalChangelog.add(
                "minSdkVersion: ${installedAppInfo.value.minSdkVersion} -> ${newAppInfo.value.minSdkVersion}"
            )
        }

        if (installedAppInfo.value.compileSdkVersion != newAppInfo.value.compileSdkVersion) {
            normalChangelog.add(
                "compileSdkVersion: ${installedAppInfo.value.compileSdkVersion} -> ${newAppInfo.value.compileSdkVersion}"
            )
        }
    }
}
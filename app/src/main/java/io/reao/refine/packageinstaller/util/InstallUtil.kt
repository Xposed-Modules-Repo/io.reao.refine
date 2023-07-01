package io.reao.refine.packageinstaller.util

import android.content.ContentResolver
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import com.topjohnwu.superuser.Shell
import io.reao.refine.packageinstaller.PackageInstallerActivity
import io.reao.refine.packageinstaller.model.AppInfo
import io.reao.refine.utils.Log
import java.io.File
import java.io.FileOutputStream
import kotlin.math.roundToInt

object InstallUtil {

    private val TAG: String = InstallUtil::class.java.simpleName

    fun copyPackage2Cache(mContext: Context, mRealPackageUri: Uri): File? {

        val mStagedFile = File.createTempFile("staged", ".apk", mContext.cacheDir)

        try {
            val inputStream =
                mContext.contentResolver.openInputStream(mRealPackageUri) ?: return null

            val outStream = FileOutputStream(mStagedFile)


            val buffer = ByteArray(1024)

            var bytesRead: Int;

            while (inputStream.read(buffer).also { bytesRead = it } >= 0) {
                outStream.write(buffer, 0, bytesRead)
            }

            inputStream.close()
            outStream.close()

            Log.d("InstallStart", " Staged apk from content URI ")

            val mCachePackageUri = Uri.fromFile(mStagedFile)

            return mStagedFile
        } catch (e: Exception) {
            Log.d("InstallStart", " Error staging apk from content URI ${e.message}")
            return null
        }
    }

    fun processPackageUri(mContext: Context, packageUri: Uri): AppInfo? {
        Log.d("InstallStart", "processPackageUri packageUri: $packageUri")

        when (packageUri.scheme) {
            PackageInstallerActivity.SCHEME_PACKAGE -> {
                // TODO
            }

            ContentResolver.SCHEME_FILE -> {
                val sourceFile = File(packageUri.path!!)

                val mPkgInfo =
                    PackageUtil.getPackageInfo(
                        context = mContext,
                        sourceFile,
                        PackageManager.GET_PERMISSIONS
                    )

                // Check for parse errors
                if (mPkgInfo == null) {
                    Log.d(TAG, "Parse error when parsing manifest. Discontinuing installation")
                    return null
                }

                val appSnippet =
                    PackageUtil.getAppSnippet(
                        context = mContext,
                        mPkgInfo.applicationInfo,
                        sourceFile
                    )

                val appInfo = AppInfo(
                    icon = appSnippet!!.icon,
                    label = appSnippet.label,
                    packageName = mPkgInfo.packageName,

                    versionName = mPkgInfo.versionName,
                    versionCode = mPkgInfo.longVersionCode,

                    minSdkVersion = mPkgInfo.applicationInfo.minSdkVersion,
                    targetSdkVersion = mPkgInfo.applicationInfo.targetSdkVersion,
                    compileSdkVersion = mPkgInfo.applicationInfo.compileSdkVersion,

                    size = ((sourceFile.length() / 1000f / 1000f) * 100).roundToInt()
                        .toDouble() / 100,
                    permissions = mPkgInfo.permissions
                )

                Log.d(TAG, "new package mPkgInfo $mPkgInfo");

                return appInfo
            }

            else -> {
                Log.d(TAG, "processPackageUri packageUri.scheme not support")
            }

        }
        return null
    }

    fun installPackage(
        mRealPackageUri: File,
        requestPermissionCallBack: () -> Unit, resultCallBack: (Boolean) -> Unit
    ) {

        Log.d(TAG, "installPackage: mRealPackageUri $mRealPackageUri")
        // check permission
        if (Shell.isAppGrantedRoot() != true) {
            Log.d(TAG, "installPackage: no root permission")
            requestPermissionCallBack()
            return
        }

        // https://issuetracker.google.com/issues/80270303

        val filePath = mRealPackageUri.path

        val length = File(filePath).length()

        val installCmd = "cat $filePath | pm install -d -r -S $length"
        Log.d(TAG, "installPackage: installCmd $installCmd")
        Shell.cmd(installCmd).submit { result: Shell.Result ->
            Log.d(
                TAG,
                "installPackage result: ${result.isSuccess} , output: ${result.getOut()} ,Error: ${result.err}"
            )
            resultCallBack(result.isSuccess)
        }
    }


}
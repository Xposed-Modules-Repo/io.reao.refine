package org.miui.refine.utils.extensions

import android.annotation.SuppressLint
import android.content.res.AssetManager
import org.lsposed.hiddenapibypass.HiddenApiBypass


object AssetManagerHidden {
    @SuppressLint("PrivateApi")
    fun create(): AssetManager {
        val instance =
            HiddenApiBypass.newInstance(Class.forName("android.content.res.AssetManager")) as AssetManager
        return instance
    }
}

fun AssetManager.addAssetPath(path: String): Int {
    //HiddenApiBypass.invoke(ApplicationInfo.class, new ApplicationInfo(), "usesNonSdkApi"/*, args*/)
    return HiddenApiBypass.invoke(AssetManager::class.java, this,"addAssetPath", path) as Int
}
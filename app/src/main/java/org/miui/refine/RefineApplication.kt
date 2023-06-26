package org.miui.refine

import android.app.Application
import com.topjohnwu.superuser.Shell
import timber.log.Timber

class RefineApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        Timber.d("RefineApplication is initialized.")
        Shell.setDefaultBuilder(Shell.Builder.create().setFlags(Shell.FLAG_MOUNT_MASTER));
    }
}
package org.miui.refine

import android.app.Application
import com.topjohnwu.superuser.Shell
import timber.log.Timber

class RefineApplication : Application() {

    init {
        Shell.enableVerboseLogging = true
        Shell.setDefaultBuilder(
            Shell.Builder.create().setFlags(Shell.FLAG_REDIRECT_STDERR)
                .setFlags(Shell.FLAG_MOUNT_MASTER)
        )
    }

    override fun onCreate() {
        super.onCreate()
        Timber.d("RefineApplication is initialized.")
    }
}
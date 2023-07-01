package io.reao.refine.initializer

import android.content.Context
import androidx.startup.Initializer
import timber.log.Timber

/**
 * for startup
 */
class TimberInitializer : Initializer<Unit> {
    override fun create(context: Context) {
        Timber.plant(Timber.DebugTree())
        Timber.d("TimberInitializer is initialized.")
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()
}
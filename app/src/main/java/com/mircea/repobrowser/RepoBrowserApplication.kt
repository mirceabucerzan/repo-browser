package com.mircea.repobrowser

import android.app.Application
import com.mircea.repobrowser.logging.TimberReleaseTree
import timber.log.Timber

/**
 * [Application] subclass responsible for global initialization.
 */
class RepoBrowserApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // init the Timber logging library
        Timber.plant(if (BuildConfig.DEBUG) Timber.DebugTree() else TimberReleaseTree())
    }

}
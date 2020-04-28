package com.mircea.repobrowser

import android.app.Application
import com.mircea.repobrowser.di.DaggerApplicationComponent
import com.mircea.repobrowser.logging.TimberReleaseTree
import com.mircea.repobrowser.networking.HttpClientManager
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import timber.log.Timber

/**
 * [Application] subclass that uses Dagger for DI.
 */
class RepoBrowserApplication : DaggerApplication() {

    override fun onCreate() {
        super.onCreate()

        // init the Timber logging library
        Timber.plant(if (BuildConfig.DEBUG) Timber.DebugTree() else TimberReleaseTree())

        // init HttpClient
        HttpClientManager.init(this)
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerApplicationComponent.factory().create(applicationContext)
    }
}
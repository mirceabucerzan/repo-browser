package com.mircea.repobrowser.di

import android.content.Context
import com.mircea.repobrowser.RepoBrowserApplication
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

/**
 * The application's main [Component], created by [RepoBrowserApplication].
 */
@Singleton
@Component(
    modules = [
        AndroidSupportInjectionModule::class,
        ApplicationModule::class
    ]
)
interface ApplicationComponent : AndroidInjector<RepoBrowserApplication> {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance appContext: Context): ApplicationComponent
    }
}
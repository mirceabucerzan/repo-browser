package com.mircea.repobrowser.di

import com.mircea.repobrowser.presentation.details.RepoDetailsFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * [Module] for the repository details screen.
 */
@Module
abstract class RepoDetailsModule {

    @ContributesAndroidInjector
    abstract fun contributeRepoDetailsFragment(): RepoDetailsFragment
}
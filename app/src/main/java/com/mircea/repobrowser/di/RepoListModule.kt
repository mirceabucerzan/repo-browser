package com.mircea.repobrowser.di

import com.mircea.repobrowser.presentation.list.RepoListFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * [Module] for the repository list screen.
 */
@Module
abstract class RepoListModule {

    @ContributesAndroidInjector
    abstract fun contributeRepoListFragment(): RepoListFragment
}
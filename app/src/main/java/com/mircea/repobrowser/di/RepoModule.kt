package com.mircea.repobrowser.di

import androidx.lifecycle.ViewModel
import com.mircea.repobrowser.presentation.RepoBrowserViewModel
import com.mircea.repobrowser.presentation.details.RepoDetailsFragment
import com.mircea.repobrowser.presentation.list.RepoListFragment
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module(
    includes = [ViewModelModule::class,
        RepoListModule::class,
        RepoDetailsModule::class]
)
abstract class RepoModule {

    @Binds
    @IntoMap
    @ViewModelKey(RepoBrowserViewModel::class)
    abstract fun bindRepoBrowserViewModel(viewModel: RepoBrowserViewModel): ViewModel
}

/**
 * [Module] for the repository list screen.
 */
@Module
abstract class RepoListModule {

    @ContributesAndroidInjector
    abstract fun contributeRepoListFragment(): RepoListFragment
}

/**
 * [Module] for the repository details screen.
 */
@Module
abstract class RepoDetailsModule {

    @ContributesAndroidInjector
    abstract fun contributeRepoDetailsFragment(): RepoDetailsFragment
}
package com.mircea.repobrowser.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.mircea.repobrowser.data.DefaultGitHubRepository
import com.mircea.repobrowser.data.GitHubApi
import com.mircea.repobrowser.networking.provideRetrofitApi
import timber.log.Timber

class RepoBrowserActivity : AppCompatActivity() {

    private lateinit var viewModel: RepoBrowserViewModel

    private val repoListObserver = Observer<UiResource<List<RepoItem>>> {
        when (it) {
            // TODO Implement UI states
            UiResource.Loading -> Timber.d("loading")
            is UiResource.Success -> Timber.d("success: #${it.data.size} repos fetched")
            is UiResource.Error -> Timber.d(it.javaClass.simpleName)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val repo = DefaultGitHubRepository(provideRetrofitApi(GitHubApi::class.java))
        val factory = RepoBrowserViewModelFactory(repo)
        viewModel = ViewModelProviders.of(this, factory).get(RepoBrowserViewModel::class.java)
    }

    override fun onStart() {
        super.onStart()
        viewModel.getSquareRepositories().observe(this, repoListObserver)
    }

}
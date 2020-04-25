package com.mircea.repobrowser.presentation.details

import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.mircea.repobrowser.R
import com.mircea.repobrowser.activityToolbarTitle
import com.mircea.repobrowser.data.DefaultGitHubRepository
import com.mircea.repobrowser.data.GitHubApi
import com.mircea.repobrowser.networking.provideRetrofitApi
import com.mircea.repobrowser.presentation.RepoBrowserViewModel
import com.mircea.repobrowser.presentation.RepoBrowserViewModelFactory
import com.mircea.repobrowser.presentation.RepoItem
import com.mircea.repobrowser.presentation.UiResource
import timber.log.Timber

/**
 * Fragment that displays the html web page of a GitHub
 * repository, identified by the id passed in as argument.
 */
class RepoDetailsFragment : Fragment(R.layout.fragment_repo_details) {

    private val args: RepoDetailsFragmentArgs by navArgs()
    private lateinit var factory: RepoBrowserViewModelFactory
    private val viewModel: RepoBrowserViewModel by viewModels({ requireActivity() }, { factory })
    private val repoDetailsObserver = Observer<UiResource<RepoItem>> {
        when (it) {
            is UiResource.Success -> {
                activityToolbarTitle = it.data.name
                webView.loadUrl(it.data.htmlUrl)
            }
            UiResource.Error.NoNetworkConnection -> {
                // For now, the generic error displayed by the WebView is enough
                Timber.d("NoNetworkConnection")
            }
            UiResource.Error.Unknown -> {
                // This wouldn't happen, since no API calls are made currently
                Timber.d("Error.Unknown")
            }
        }
    }
    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // The issue of creating a new repo will be solved when implementing proper DI (Dagger)
        val repo = DefaultGitHubRepository(provideRetrofitApi(GitHubApi::class.java))
        factory = RepoBrowserViewModelFactory(repo)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        webView = view.findViewById(R.id.web_view)
        // set the client in order to allow the WebView to handle page navigation
        webView.webViewClient = WebViewClient()
        viewModel.getSquareRepository(args.repoId).observe(viewLifecycleOwner, repoDetailsObserver)
    }

}
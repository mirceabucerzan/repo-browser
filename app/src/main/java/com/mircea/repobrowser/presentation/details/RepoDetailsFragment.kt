package com.mircea.repobrowser.presentation.details

import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.mircea.repobrowser.R
import com.mircea.repobrowser.activityToolbarTitle
import com.mircea.repobrowser.presentation.RepoBrowserViewModel
import com.mircea.repobrowser.presentation.RepoItem
import com.mircea.repobrowser.presentation.UiResource
import dagger.android.support.DaggerFragment
import timber.log.Timber
import javax.inject.Inject

/**
 * Fragment that displays the html web page of a GitHub
 * repository, identified by the id passed in as argument.
 */
class RepoDetailsFragment : DaggerFragment(R.layout.fragment_repo_details) {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    private val args: RepoDetailsFragmentArgs by navArgs()
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        webView = view.findViewById(R.id.web_view)
        // set the client in order to allow the WebView to handle page navigation
        webView.webViewClient = WebViewClient()
        viewModel.getSquareRepository(args.repoId).observe(viewLifecycleOwner, repoDetailsObserver)
    }

}
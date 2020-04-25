package com.mircea.repobrowser.presentation.list

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mircea.repobrowser.R
import com.mircea.repobrowser.activityToolbarTitle
import com.mircea.repobrowser.data.DefaultGitHubRepository
import com.mircea.repobrowser.data.GitHubApi
import com.mircea.repobrowser.networking.provideRetrofitApi
import com.mircea.repobrowser.presentation.*
import timber.log.Timber

/**
 * Fragment which displays a vertically scrollable list of Square's GitHub repositories.
 * Each list item displays the repo's name, description and owner avatar image.
 */
class RepoListFragment : Fragment(R.layout.fragment_repo_list),
    RepoListAdapter.ItemSelectedListener {

    private lateinit var viewModel: RepoBrowserViewModel
    private lateinit var repoListAdapter: RepoListAdapter
    private lateinit var repoList: RecyclerView
    private lateinit var loadingIndicator: ProgressBar
    private lateinit var errorView: TextView

    private val repoListObserver = Observer<UiResource<List<RepoItem>>> {
        when (it) {
            UiResource.Loading -> {
                // display loading state
                Timber.d("loading")
                loadingIndicator.visibility = View.VISIBLE
                repoList.visibility = View.GONE
                errorView.visibility = View.GONE
            }
            is UiResource.Success -> {
                // update repo list
                Timber.d("success: #${it.data.size} repos fetched")
                loadingIndicator.visibility = View.GONE
                errorView.visibility = View.GONE
                repoList.visibility = View.VISIBLE
                repoListAdapter.items = it.data
                repoListAdapter.notifyDataSetChanged()
            }
            is UiResource.Error -> {
                // display error view
                Timber.d(it.javaClass.simpleName)
                loadingIndicator.visibility = View.GONE
                repoList.visibility = View.GONE
                errorView.visibility = View.VISIBLE
                errorView.text = if (it is UiResource.Error.NoNetworkConnection) {
                    getString(R.string.error_no_network)
                } else {
                    getString(R.string.error_generic)
                }
            }
        }
    }

    private val openRepoDetailsObserver = UniqueEventObserver<Long> {
        findNavController().navigate(
            RepoListFragmentDirections.actionRepoListToRepoDetails(it)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // init ViewModel
        val repo = DefaultGitHubRepository(provideRetrofitApi(GitHubApi::class.java))
        val factory = RepoBrowserViewModelFactory(repo)
        viewModel =
            ViewModelProviders.of(requireActivity(), factory).get(RepoBrowserViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadingIndicator = view.findViewById(R.id.loading_indicator)
        errorView = view.findViewById(R.id.error_text)
        repoList = view.findViewById(R.id.repo_list)
        repoList.setHasFixedSize(true)
        repoList.layoutManager = LinearLayoutManager(requireContext())
        repoListAdapter = RepoListAdapter(this)
        repoList.adapter = repoListAdapter

        // observe data
        viewModel.getSquareRepositories().observe(viewLifecycleOwner, repoListObserver)
        // observe open web page events
        viewModel.getOpenRepoDetailsEvent().observe(viewLifecycleOwner, openRepoDetailsObserver)
    }

    override fun onStart() {
        super.onStart()
        activityToolbarTitle = getString(R.string.activity_repo_browser_title)
    }

    override fun onItemSelected(itemId: Long) {
        viewModel.itemSelected(itemId)
    }
}
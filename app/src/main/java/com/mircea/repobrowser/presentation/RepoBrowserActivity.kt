package com.mircea.repobrowser.presentation

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mircea.repobrowser.R
import com.mircea.repobrowser.data.DefaultGitHubRepository
import com.mircea.repobrowser.data.GitHubApi
import com.mircea.repobrowser.networking.provideRetrofitApi
import timber.log.Timber

/**
 * Activity which displays a vertically scrollable list of Square's GitHub repositories.
 * Each list item displays the repo's name, description and owner avatar image.
 */
class RepoBrowserActivity : AppCompatActivity(), RepoListAdapter.ItemSelectedListener {

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

    private val openWebPageEventObserver: UniqueEventObserver<String> = UniqueEventObserver { url ->
        val webPageUri = Uri.parse(url)
        val intent = Intent(Intent.ACTION_VIEW, webPageUri)
        // check if the Intent can be resolved, and start the activity
        if (intent.resolveActivity(packageManager) != null) {
            Timber.d("Opening web page $webPageUri")
            startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // set up views
        setContentView(R.layout.activity_repo_browser)
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.title = getString(R.string.activity_repo_browser_title)

        loadingIndicator = findViewById(R.id.loading_indicator)
        errorView = findViewById(R.id.error_text)
        repoList = findViewById(R.id.repo_list)
        repoList.setHasFixedSize(true)
        repoList.layoutManager = LinearLayoutManager(this)
        repoListAdapter = RepoListAdapter(this)
        repoList.adapter = repoListAdapter

        // init ViewModel
        val repo = DefaultGitHubRepository(provideRetrofitApi(GitHubApi::class.java))
        val factory = RepoBrowserViewModelFactory(repo)
        viewModel = ViewModelProviders.of(this, factory).get(RepoBrowserViewModel::class.java)
    }

    override fun onStart() {
        super.onStart()
        // observe data
        viewModel.getSquareRepositories().observe(this, repoListObserver)
        // observe open web page events
        viewModel.getOpenWebPageEvent().observe(this, openWebPageEventObserver)
    }

    override fun onItemSelected(itemId: Long) {
        viewModel.itemSelected(itemId)
    }
}
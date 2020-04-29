package com.mircea.repobrowser.presentation

import android.util.LongSparseArray
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mircea.repobrowser.data.GitHubRepository
import com.mircea.repobrowser.data.RepoDto
import com.mircea.repobrowser.networking.NoNetworkException
import com.mircea.repobrowser.networking.Result
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * [ViewModel] holding observable GitHub repository data for Square org.
 */
class RepoBrowserViewModel @Inject constructor(
    private val repository: GitHubRepository
) : ViewModel() {

    // The raw list of fetched repos
    private var repoDtoList: List<RepoDto>? = null

    // Observable list of repos, for the repos list UI
    private lateinit var uiRepoItemList: MutableLiveData<UiResource<List<RepoItem>>>

    // In memory cache of observable RepoItems, for the repo details UI
    private val uiRepoItemMap = LongSparseArray<MutableLiveData<UiResource<RepoItem>>>()

    // Navigation event
    private val openRepoDetailsEvent = MutableLiveData<UniqueEvent<Long>>()

    /**
     * Returns an observable [UiResource] for displaying GitHub repository data for Square org.
     */
    fun getSquareRepositories(): LiveData<UiResource<List<RepoItem>>> {
        if (!this::uiRepoItemList.isInitialized) {
            // fetch only once, no refresh
            uiRepoItemList = MutableLiveData()
            uiRepoItemList.value = UiResource.Loading
            viewModelScope.launch {
                val result = repository.getRepos(GitHubRepository.SQUARE_ORG_NAME)

                uiRepoItemList.value = when (result) {
                    is Result.Success -> {
                        repoDtoList = result.data
                        UiResource.Success(result.data.map { it.toRepoItem() })
                    }

                    is Result.Error -> {
                        repoDtoList = null
                        if (result.exception is NoNetworkException) {
                            UiResource.Error.NoNetworkConnection
                        } else {
                            UiResource.Error.Unknown
                        }
                    }
                }
            }
        }
        return uiRepoItemList
    }

    fun getSquareRepository(id: Long): LiveData<UiResource<RepoItem>> {
        // check cache first
        var uiRepoItem = uiRepoItemMap[id]
        if (uiRepoItem == null) {
            uiRepoItem = MutableLiveData()
            uiRepoItemMap.put(id, uiRepoItem)
            // must have been fetched previously, retrieve and wrap in LiveData
            repoDtoList?.find { it.id == id }?.let { repoDto ->
                uiRepoItem.value = UiResource.Success(repoDto.toRepoItem())
            } ?: run {
                // very unlikely, since we fetch all of Square's repos, but we
                // could fetch the individual repo from the github API, by id
                uiRepoItem.value = UiResource.Error.Unknown
            }
        }
        return uiRepoItem
    }

    fun getOpenRepoDetailsEvent(): LiveData<UniqueEvent<Long>> = openRepoDetailsEvent

    fun itemSelected(itemId: Long) {
        repoDtoList?.find { it.id == itemId }?.id?.let {
            openRepoDetailsEvent.value = UniqueEvent(it)
        }
    }
}

/**
 * Extension function which maps the domain (network) model to the UI model.
 */
fun RepoDto.toRepoItem() = RepoItem(
    id ?: -1L,
    name.orEmpty(),
    description.orEmpty(),
    htmlUrl.orEmpty(),
    owner?.avatarUrl.orEmpty()
)
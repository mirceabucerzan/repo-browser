package com.mircea.repobrowser.presentation

import androidx.lifecycle.*
import com.mircea.repobrowser.data.GitHubRepository
import com.mircea.repobrowser.data.RepoDto
import com.mircea.repobrowser.networking.NoNetworkException
import com.mircea.repobrowser.networking.Result
import kotlinx.coroutines.launch

/**
 * [ViewModel] holding observable GitHub repository data for Square org.
 */
class RepoBrowserViewModel(private val repository: GitHubRepository) : ViewModel() {

    private lateinit var squareRepositories: MutableLiveData<UiResource<List<RepoItem>>>

    /**
     * Returns an observable [UiResource] for displaying GitHub repository data for Square org.
     */
    fun getSquareRepositories(): LiveData<UiResource<List<RepoItem>>> {
        if (!this::squareRepositories.isInitialized) {
            squareRepositories = MutableLiveData()
            squareRepositories.value = UiResource.Loading
            viewModelScope.launch {
                val result = repository.getRepos(GitHubRepository.SQUARE_ORG_NAME)
                squareRepositories.value = when (result) {
                    is Result.Success -> UiResource.Success(result.data.map { it.toRepoItem() })
                    is Result.Error -> if (result.exception is NoNetworkException) {
                        UiResource.Error.NoNetworkConnection
                    } else {
                        UiResource.Error.Unknown
                    }
                }
            }
        }
        return squareRepositories
    }

}

/**
 * Factory which creates a [RepoBrowserViewModel].
 */
internal class RepoBrowserViewModelFactory(
    private val repository: GitHubRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RepoBrowserViewModel::class.java)) {
            return RepoBrowserViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class $modelClass")
    }
}

/**
 * Extension function which maps the domain (network) model to the UI model.
 */
private fun RepoDto.toRepoItem() = RepoItem(
    name.orEmpty(),
    description.orEmpty(),
    owner?.avatarUrl.orEmpty(),
    owner?.htmlUrl.orEmpty()
)
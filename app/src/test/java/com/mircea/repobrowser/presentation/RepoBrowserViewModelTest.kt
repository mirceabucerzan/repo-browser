package com.mircea.repobrowser.presentation

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.mircea.repobrowser.TestCoroutineRule
import com.mircea.repobrowser.data.GitHubRepository
import com.mircea.repobrowser.data.OwnerDto
import com.mircea.repobrowser.data.RepoDto
import com.mircea.repobrowser.networking.NoNetworkException
import com.mircea.repobrowser.networking.Result
import com.mircea.repobrowser.observeForTesting
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import java.io.IOException

@ExperimentalCoroutinesApi
class RepoBrowserViewModelTest {

    // Run arch components tasks synchronously
    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    // Sets the main coroutines dispatcher to a TestCoroutineScope for unit testing
    @ExperimentalCoroutinesApi
    @get:Rule
    var testCoroutineRule = TestCoroutineRule()

    private lateinit var fakeRepository: GitHubRepository
    private val viewModel by lazy { RepoBrowserViewModel(fakeRepository) }

    @Test
    fun getSquareRepositoriesLoading() = runBlockingTest {
        // given
        fakeRepository = object : GitHubRepository {
            override suspend fun getRepos(organizationName: String): Result<List<RepoDto>> {
                delay(1)
                return Result.Success(emptyList())
            }
        }

        // when
        testCoroutineRule.pauseDispatcher()
        val repositoriesLiveData = viewModel.getSquareRepositories()

        // then
        repositoriesLiveData.observeForTesting {
            assertEquals(
                "UIResource is not Loading",
                UiResource.Loading,
                repositoriesLiveData.value
            )

            testCoroutineRule.resumeDispatcher()

            assertNotEquals(
                "UIResource is Loading",
                UiResource.Loading,
                repositoriesLiveData.value
            )
        }
    }

    @Test
    fun getSquareRepositoriesSuccess() = runBlockingTest {
        // given
        val fakeRepoDto = RepoDto(
            1,
            "Retrofit",
            "Type safe HTTP client for Android",
            "url",
            OwnerDto("avatarUrl")
        )
        fakeRepository = object : GitHubRepository {
            override suspend fun getRepos(organizationName: String): Result<List<RepoDto>> {
                return Result.Success(listOf(fakeRepoDto))
            }
        }

        // when
        val repositoriesLiveData = viewModel.getSquareRepositories()

        // then
        repositoriesLiveData.observeForTesting {
            assertTrue(
                "UIResource is not Success",
                repositoriesLiveData.value is UiResource.Success
            )
            assertTrue(
                "RepoItem list is not size 1",
                (repositoriesLiveData.value as? UiResource.Success)?.data?.size == 1
            )
            assertEquals(
                "RepoItem has invalid contents",
                fakeRepoDto.toRepoItem(),
                (repositoriesLiveData.value as? UiResource.Success)?.data?.get(0)
            )
        }
    }

    @Test
    fun getSquareRepositoriesErrorNoNetwork() = runBlockingTest {
        // given
        fakeRepository = object : GitHubRepository {
            override suspend fun getRepos(organizationName: String): Result<List<RepoDto>> {
                return Result.Error(NoNetworkException())
            }
        }

        // when
        val repositoriesLiveData = viewModel.getSquareRepositories()

        // then
        repositoriesLiveData.observeForTesting {
            assertTrue(
                "UIResource is not Error.NoNetworkConnection",
                repositoriesLiveData.value is UiResource.Error.NoNetworkConnection
            )
        }
    }

    @Test
    fun getSquareRepositoriesErrorGeneric() = runBlockingTest {
        // given
        fakeRepository = object : GitHubRepository {
            override suspend fun getRepos(organizationName: String): Result<List<RepoDto>> {
                return Result.Error(IOException())
            }
        }

        // when
        val repositoriesLiveData = viewModel.getSquareRepositories()

        // then
        repositoriesLiveData.observeForTesting {
            assertTrue(
                "UIResource is not Error.Unknown",
                repositoriesLiveData.value is UiResource.Error.Unknown
            )
        }
    }

    @Test
    fun openWebPageEventTriggered() = runBlockingTest {
        // given
        val fakeRepoDto = RepoDto(
            1,
            "Retrofit",
            "Type safe HTTP client for Android",
            "www.example.com",
            OwnerDto("avatarUrl")
        )
        fakeRepository = object : GitHubRepository {
            override suspend fun getRepos(organizationName: String): Result<List<RepoDto>> {
                return Result.Success(listOf(fakeRepoDto))
            }
        }
        viewModel.getSquareRepositories()

        // when
        viewModel.itemSelected(fakeRepoDto.id ?: 1)
        val openEventLiveData = viewModel.getOpenWebPageEvent()

        // then
        openEventLiveData.observeForTesting {
            assertTrue(
                "Url is invalid",
                openEventLiveData.value?.getContent() == fakeRepoDto.htmlUrl
            )
        }
    }
}
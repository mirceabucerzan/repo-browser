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
    fun openRepoDetailsEventTriggered() = runBlockingTest {
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
        val openEventLiveData = viewModel.getOpenRepoDetailsEvent()

        // then
        openEventLiveData.observeForTesting {
            assertTrue(
                "Repo id is invalid",
                openEventLiveData.value?.getContent() == fakeRepoDto.id
            )
        }
    }

    @Test
    fun getSquareRepositorySuccess() = runBlockingTest {
        // given
        val id = 123L
        val fakeRepoDto = RepoDto(
            id,
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

        // when
        viewModel.getSquareRepositories()   // fetch first, so cache is populated
        val liveRepo = viewModel.getSquareRepository(id)

        // then
        liveRepo.observeForTesting {
            assertTrue("UI Resource is not Success", liveRepo.value is UiResource.Success<RepoItem>)
            val data = (liveRepo.value as UiResource.Success<RepoItem>).data
            assertEquals("Invalid repo id", fakeRepoDto.id, data.id)
            assertEquals("Invalid repo name", fakeRepoDto.name, data.name)
            assertEquals("Invalid repo description", fakeRepoDto.description, data.description)
            assertEquals("Invalid repo htmlUrl", fakeRepoDto.htmlUrl, data.htmlUrl)
            assertEquals("Invalid repo imageUrl", fakeRepoDto.owner?.avatarUrl, data.imageUrl)
        }
    }

    @Test
    fun getSquareRepositoryErrorUnknownId() = runBlockingTest {
        // given
        fakeRepository = object : GitHubRepository {
            override suspend fun getRepos(organizationName: String): Result<List<RepoDto>> {
                return Result.Success(listOf(RepoDto(789L, "", "", "", null)))
            }
        }

        // when
        viewModel.getSquareRepositories()   // fetch first, so cache is populated
        val liveRepo = viewModel.getSquareRepository(123L)

        // then
        liveRepo.observeForTesting {
            assertTrue(
                "UI Resource is not Error.Unknown",
                liveRepo.value is UiResource.Error.Unknown
            )
        }
    }

    @Test
    fun getSquareRepositorySameIdReturnsSameInstance() = runBlockingTest {
        // given
        val id = 123L
        fakeRepository = object : GitHubRepository {
            override suspend fun getRepos(organizationName: String): Result<List<RepoDto>> {
                return Result.Success(listOf(RepoDto(id, "", "", "", null)))
            }
        }

        // when
        viewModel.getSquareRepositories()   // fetch first, so cache is populated
        val liveRepo1 = viewModel.getSquareRepository(id)
        val liveRepo2 = viewModel.getSquareRepository(id)

        // then
        assertTrue("Different instance returned when using same id", liveRepo1 === liveRepo2)
    }
}
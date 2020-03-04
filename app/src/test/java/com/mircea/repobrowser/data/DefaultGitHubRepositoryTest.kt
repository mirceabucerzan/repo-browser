package com.mircea.repobrowser.data

import com.mircea.repobrowser.networking.NoNetworkException
import com.mircea.repobrowser.networking.Result
import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class DefaultGitHubRepositoryTest {

    private lateinit var fakeGitHubApi: GitHubApi
    private val repository by lazy { DefaultGitHubRepository(fakeGitHubApi) }

    @Test
    fun getReposSuccess() = runBlocking {
        // given
        val fakeRepoDto = RepoDto(
            1,
            "Retrofit",
            "Type safe HTTP client for Android",
            "url",
            OwnerDto("avatarUrl")
        )
        fakeGitHubApi = object : GitHubApi {
            override suspend fun getRepos(organizationName: String): Response<List<RepoDto>> {
                return Response.success(listOf(fakeRepoDto))
            }
        }

        // when
        val result = repository.getRepos(GitHubRepository.SQUARE_ORG_NAME) as? Result.Success

        // then
        assertTrue("Result is not Success", result != null)
        assertTrue("Repo list is not size 1", result?.data?.size == 1)
        assertEquals("RepoDto has invalid data", fakeRepoDto, result?.data?.get(0))
    }

    @Test
    fun getReposErrorGeneric() = runBlocking {
        // given
        fakeGitHubApi = object : GitHubApi {
            override suspend fun getRepos(organizationName: String): Response<List<RepoDto>> {
                return Response.error(404, "Not found".toResponseBody())
            }
        }

        // when
        val result = repository.getRepos(GitHubRepository.SQUARE_ORG_NAME) as? Result.Error

        // then
        assertTrue("Result is not Error", result != null)
        assertTrue("Exception is not IOException", result?.exception is IOException)
    }

    @Test
    fun getReposErrorHttpException() = runBlocking {
        // given
        fakeGitHubApi = object : GitHubApi {
            override suspend fun getRepos(organizationName: String): Response<List<RepoDto>> {
                throw HttpException(Response.error<Unit>(404, "Not found".toResponseBody()))
            }
        }

        // when
        val result = repository.getRepos(GitHubRepository.SQUARE_ORG_NAME) as? Result.Error

        // then
        assertTrue("Result is not Error", result != null)
        assertTrue("Exception is not HttpException", result?.exception is HttpException)
    }

    @Test
    fun getReposErrorNoNetworkException() = runBlocking {
        // given
        fakeGitHubApi = object : GitHubApi {
            override suspend fun getRepos(organizationName: String): Response<List<RepoDto>> {
                throw NoNetworkException()
            }
        }

        // when
        val result = repository.getRepos(GitHubRepository.SQUARE_ORG_NAME) as? Result.Error

        // then
        assertTrue("Result is not Error", result != null)
        assertTrue("Exception is not NoNetworkException", result?.exception is NoNetworkException)
    }

    @Test
    fun getReposErrorThrowable() = runBlocking {
        // given
        fakeGitHubApi = object : GitHubApi {
            override suspend fun getRepos(organizationName: String): Response<List<RepoDto>> {
                throw Throwable()
            }
        }

        // when
        val result = repository.getRepos(GitHubRepository.SQUARE_ORG_NAME) as? Result.Error

        // then
        assertTrue("Result is not Error", result != null)
        assertTrue("Exception is not IOException", result?.exception is IOException)
    }
}
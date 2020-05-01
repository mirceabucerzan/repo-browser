package com.mircea.repobrowser.data

import com.mircea.repobrowser.networking.Result
import com.mircea.repobrowser.networking.callApi
import javax.inject.Inject

class DefaultGitHubRepository @Inject constructor(
    private val gitHubApi: GitHubApi
) : GitHubRepository {

    override suspend fun getRepos(organizationName: String): Result<List<RepoDto>> {
        return callApi { gitHubApi.getRepos(organizationName) }
    }
}
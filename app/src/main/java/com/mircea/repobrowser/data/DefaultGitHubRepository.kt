package com.mircea.repobrowser.data

import com.mircea.repobrowser.networking.Result
import com.mircea.repobrowser.networking.callApi

class DefaultGitHubRepository(private val gitHubApi: GitHubApi) : GitHubRepository {

    override suspend fun getRepos(organizationName: String): Result<List<RepoDto>> {
        return callApi { gitHubApi.getRepos(organizationName) }
    }
}
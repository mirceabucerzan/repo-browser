package com.mircea.repobrowser.data

import com.mircea.repobrowser.networking.Result

/**
 * Repository which manages Github data. All methods are non-blocking.
 */
interface GitHubRepository {
    companion object {
        const val SQUARE_ORG_NAME = "square"
    }

    /**
     * Returns a list of Github [RepoDto] for an organization identified by [organizationName].
     */
    suspend fun getRepos(organizationName: String): Result<List<RepoDto>>
}
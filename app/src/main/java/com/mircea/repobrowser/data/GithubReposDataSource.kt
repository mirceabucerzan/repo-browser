package com.mircea.repobrowser.data

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Provides access to repositories data, stored on GitHub servers.
 */
interface GithubReposDataSource {
    companion object {
        const val SQUARE_ORG_NAME = "square"
    }

    @GET("/orgs/{name}/repos")
    suspend fun getRepos(@Path("name") organizationName: String): Response<List<RepoDto>>
}
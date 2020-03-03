package com.mircea.repobrowser.data

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Provides access to repositories data, stored on GitHub servers.
 */
interface GitHubApi {

    @GET("/orgs/{name}/repos")
    suspend fun getRepos(@Path("name") organizationName: String): Response<List<RepoDto>>
}
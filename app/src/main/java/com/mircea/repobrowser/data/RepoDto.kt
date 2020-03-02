package com.mircea.repobrowser.data

import com.google.gson.annotations.SerializedName

/**
 * Model class for a GitHub repository.
 */
data class RepoDto(
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String,
    @SerializedName("owner") val owner: OwnerDto
)

data class OwnerDto(
    @SerializedName("avatar_url") val avatarUrl: String,
    @SerializedName("html_url") val htmlUrl: String
)
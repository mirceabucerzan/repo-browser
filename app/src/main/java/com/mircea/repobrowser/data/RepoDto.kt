package com.mircea.repobrowser.data

import com.google.gson.annotations.SerializedName

/**
 * Model class for a GitHub repository.
 */
data class RepoDto(
    @SerializedName("id") val id: Long? = null,
    @SerializedName("name") val name: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("html_url") val htmlUrl: String,
    @SerializedName("owner") val owner: OwnerDto?
)

data class OwnerDto(
    @SerializedName("avatar_url") val avatarUrl: String?
)
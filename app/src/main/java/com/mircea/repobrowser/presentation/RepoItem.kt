package com.mircea.repobrowser.presentation

/**
 * Ui model for a GitHub repository.
 */
data class RepoItem(
    val id: Long,
    val name: String,
    val description: String,
    val htmlUrl: String,
    val imageUrl: String
)
package com.mircea.repobrowser.networking

/**
 * Model for a networking operation's result.
 */
sealed class Result<out T : Any> {
    data class Success<out T : Any>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()
}
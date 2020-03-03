package com.mircea.repobrowser.presentation

/**
 * Wraps data consumed by the UI. Useful for situations where a data fetch
 * operation can be in either of loading, successful or error states.
 */
sealed class UiResource<out T : Any> {

    object Loading : UiResource<Nothing>()

    data class Success<out T : Any>(val data: T) : UiResource<T>()

    sealed class Error : UiResource<Nothing>() {

        object NoNetworkConnection : Error()

        object Unknown : Error()
    }
}
package com.mircea.repobrowser

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

/**
 * Observes the [LiveData] until the [block] function is done executing.
 */
fun <T> LiveData<T>.observeForTesting(block: () -> Unit) {
    val observer = Observer<T> { }
    try {
        observeForever(observer)
        block()
    } finally {
        removeObserver(observer)
    }
}
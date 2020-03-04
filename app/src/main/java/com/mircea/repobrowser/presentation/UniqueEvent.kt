package com.mircea.repobrowser.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

/**
 * Wraps data exposed via a [LiveData], which represents an event and makes sure it's consumed only once.
 */
class UniqueEvent<out T>(private val content: T) {

    private var handled: Boolean = false

    /**
     * Returns the content only once, preventing its use again.
     */
    fun getContent(): T? {
        return if (handled) {
            null
        } else {
            handled = true
            content
        }
    }

}

/**
 * An [Observer] for [UniqueEvent]s which checks if it's contents have already been handled.
 *
 * @param onUniqueEvent Function called only if the [UniqueEvent]'s contents have not been handled.
 */
class UniqueEventObserver<T>(private val onUniqueEvent: (T) -> Unit) : Observer<UniqueEvent<T>> {

    override fun onChanged(uniqueEvent: UniqueEvent<T>?) {
        uniqueEvent?.getContent()?.let { content ->
            onUniqueEvent(content)
        }
    }

}
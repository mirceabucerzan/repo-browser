package com.mircea.repobrowser.logging

import timber.log.Timber

/**
 * [Timber.Tree] used for logging in production.
 */
class TimberReleaseTree : Timber.Tree() {
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        //TODO Set up logging in production (analytics?)
    }
}
package com.mircea.repobrowser

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

/**
 * [Fragment] extensions.
 */

var Fragment.activityToolbarTitle: CharSequence?
    get() {
        return (requireActivity() as AppCompatActivity).supportActionBar?.title
    }
    set(value) {
        (requireActivity() as AppCompatActivity).supportActionBar?.title = value
    }
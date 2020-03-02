package com.mircea.repobrowser.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import timber.log.Timber

class RepoBrowserActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("onCreate()")
    }

}
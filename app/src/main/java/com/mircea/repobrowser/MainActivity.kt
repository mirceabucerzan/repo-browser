package com.mircea.repobrowser

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // set up views
        setContentView(R.layout.activity_repo_browser)
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.title = getString(R.string.activity_repo_browser_title)
    }
}
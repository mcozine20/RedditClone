package com.example.redditclone.activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.example.redditclone.network.Util
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*

class SplashActivity : AppCompatActivity() {

    private lateinit var afterSlug : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // coroutine that blocks the main thread
        runBlocking<Unit> {
            val redditHandler = Util()
            afterSlug = redditHandler.addPostsToDB("", this@SplashActivity)
            Log.d("AFTER_SLUG", "$afterSlug")

            val scrollActivityIntent = Intent(this@SplashActivity, ScrollingActivity::class.java)
            scrollActivityIntent.putExtra(ScrollingActivity.KEY_AFTER_SLUG, afterSlug)
            startActivity(scrollActivityIntent)

            finish()
        }
    }
}
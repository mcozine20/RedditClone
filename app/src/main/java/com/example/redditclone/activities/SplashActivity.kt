package com.example.redditclone.activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.example.redditclone.network.Util

class SplashActivity : AppCompatActivity() {

    private lateinit var afterSlug : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val redditHandler = Util()
        afterSlug = redditHandler.addPostsToDB("", this@SplashActivity)

        val scrollActivityIntent = Intent(this@SplashActivity, ScrollingActivity::class.java)
        scrollActivityIntent.putExtra(ScrollingActivity.KEY_AFTER_SLUG, afterSlug)
        startActivity(scrollActivityIntent)
        finish()

    }

}
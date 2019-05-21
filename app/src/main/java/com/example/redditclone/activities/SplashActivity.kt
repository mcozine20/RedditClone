package com.example.redditclone.activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.bumptech.glide.Glide
import com.example.redditclone.data.AppDatabase
import com.example.redditclone.data.Post
import com.example.redditclone.data.RedditResponse
import com.example.redditclone.network.RedditAPI
import com.example.redditclone.network.Util
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

class SplashActivity : AppCompatActivity() {

    private val HOST_URL = "https://reddit.com/"
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
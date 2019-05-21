package com.example.redditclone.activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.redditclone.data.AppDatabase
import com.example.redditclone.data.Post
import com.example.redditclone.data.RedditResponse
import com.example.redditclone.network.RedditAPI
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
        startActivity(Intent(this@SplashActivity, ScrollingActivity::class.java))

        val interceptor : HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
            this.level = HttpLoggingInterceptor.Level.BODY
        }

        val client : OkHttpClient = OkHttpClient.Builder().apply {
            this.addInterceptor(interceptor)
        }.build()

        val retrofit = Retrofit.Builder()
            .baseUrl(HOST_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        val redditAPI = retrofit.create(RedditAPI::class.java)

        // Call the API and retrieve the other information here
        val weatherCall = redditAPI.getPosts(afterSlug = "")

        weatherCall.enqueue(object : Callback<RedditResponse> {

            override fun onFailure(call: Call<RedditResponse>, t: Throwable) {

            }

            override fun onResponse(call: Call<RedditResponse>, response: Response<RedditResponse>) {
                val body = response.body()
                val posts = body?.data?.children
                afterSlug = body?.data?.after!!

                val imgPosts = posts?.filter { it.data?.post_hint == "image" }

                Thread {
                    if (imgPosts != null) {
                        imgPosts!!.forEach {
                            val newPost = Post(
                                postId = null,
                                postTitle = it?.data?.title!!,
                                postContentHint = it?.data?.post_hint!!,
                                postText = it?.data?.selftext!!,
                                postImageUrl = it?.data?.url!!)
                            val newId = AppDatabase.getInstance(this@SplashActivity).postDao().insertPost(newPost)
                            newPost.postId = newId
                        }
                    }

                }.start()

            }
        })

        finish()
    }

}
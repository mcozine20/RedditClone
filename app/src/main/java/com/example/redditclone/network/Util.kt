package com.example.redditclone.network


//Not used yet.

import android.content.Context
import android.util.Log
import com.example.redditclone.activities.SplashActivity
import com.example.redditclone.data.AppDatabase
import com.example.redditclone.data.Post
import com.example.redditclone.data.RedditResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*

const val HOST_URL = "https://reddit.com/"
class Util {

    suspend fun addPostsToDB(afterSlug: String, context: Context): String {

        val channel = Channel<String>()

        val interceptor: HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
            this.level = HttpLoggingInterceptor.Level.BODY
        }

        val client: OkHttpClient = OkHttpClient.Builder().apply {
            this.addInterceptor(interceptor)
        }.build()

        val retrofit = Retrofit.Builder()
            .baseUrl(HOST_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        val redditAPI = retrofit.create(RedditAPI::class.java)

        // Call the API and retrieve the other information here
        val redditCall = redditAPI.getPosts(afterSlug)

        GlobalScope.launch {
            val response = redditCall.execute()

            val body = response.body()
            val posts = body?.data?.children
            val newAfterSlug = body?.data?.after!!

            val imgPosts = posts?.filter { it.data?.post_hint == "image" }
            if (imgPosts != null) {
                imgPosts!!.forEach {
                    val newPost = Post(
                        postId = null,
                        postTitle = it?.data?.title!!,
                        postContentHint = it?.data?.post_hint!!,
                        postText = it?.data?.selftext!!,
                        postName = it?.data?.name!!,
                        postThumbnailUrl = it?.data.thumbnail!!,
                        postImageUrl = it?.data?.url!!
                    )
                    val newId = AppDatabase.getInstance(context).postDao().insertPost(newPost)
                    newPost.postId = newId
                }
            }

            Log.d("AFTER_SLUG", "WAITING TO SEND TO CHANNEL")
            channel.send(newAfterSlug)
            Log.d("AFTER_SLUG", "SEND TO CHANNEL")
        }

        Log.d("AFTER_SLUG", "WAITING TO RECEIVE")
        val toReturn = channel.receive()
        Log.d("AFTER_SLUG", "HAVE RECEIVED $toReturn")
        return toReturn
    }
}

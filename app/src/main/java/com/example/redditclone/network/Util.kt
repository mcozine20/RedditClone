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

const val HOST_URL = "https://reddit.com/"
class Util {


    //lateinit var newAfterSlug: String
    var newAfterSlug = ""


    fun addPostsToDB(afterSlug: String, context: Context): String {

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
        val redditCall = redditAPI.getPosts(afterSlug = "")


        redditCall.enqueue(object : Callback<RedditResponse> {

            override fun onFailure(call: Call<RedditResponse>, t: Throwable) {

            }

            override fun onResponse(call: Call<RedditResponse>, response: Response<RedditResponse>) {
                val body = response.body()
                val posts = body?.data?.children
                this@Util.newAfterSlug = body?.data?.after!!
                Log.d("UTIL", "Tried to update newAfterSlug with " + this@Util.newAfterSlug)


                val imgPosts = posts?.filter { it.data?.post_hint == "image" }
                Log.d("debug", "empty: ${imgPosts.isNullOrEmpty()}}")

                Thread {
                    if (imgPosts != null) {
                        imgPosts!!.forEach {
                            val newPost = Post(
                                postId = null,
                                postTitle = it?.data?.title!!,
                                postContentHint = it?.data?.post_hint!!,
                                postText = it?.data?.selftext!!,
                                postImageUrl = it?.data?.url!!
                            )
                            val newId = AppDatabase.getInstance(context).postDao().insertPost(newPost)
                            newPost.postId = newId
                        }
                    }

                }.start()

            }
        })
        Log.d("UTIL", "newAfterSlug = " + newAfterSlug)

        return this.newAfterSlug
    }
}

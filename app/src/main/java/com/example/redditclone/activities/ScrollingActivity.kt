package com.example.redditclone.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import com.example.redditclone.R
import com.example.redditclone.adaptor.PostAdaptor
import com.example.redditclone.data.AppDatabase
import com.example.redditclone.data.Post
import kotlinx.android.synthetic.main.activity_scrolling.*
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import android.util.Log
import android.widget.Toolbar
import com.example.redditclone.network.Util
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*


class ScrollingActivity : AppCompatActivity() {

    private lateinit var postAdaptor: PostAdaptor
    lateinit var afterSlug: String

    companion object {
        const val KEY_AFTER_SLUG = "KEY_AFTER_SLUG"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scrolling)
        setSupportActionBar(toolbar)

        Log.d("SANITY_CHECK", "onCreate is being called!")

        if (intent.hasExtra(KEY_AFTER_SLUG)){
            Log.d("afterSlug", "about to request KEY_AFTER_SLUG")
            afterSlug = intent.getStringExtra(KEY_AFTER_SLUG)
            Log.d("afterSlug", "$afterSlug")
        }

        initRecyclerViewFromDB()

        resetButton.setOnClickListener{
            GlobalScope.launch {
                AppDatabase.getInstance(this@ScrollingActivity).postDao().deleteAll()

                // BLOCKING FUNCTION
                afterSlug = Util().addPostsToDB(afterSlug, this@ScrollingActivity)
                Log.d("afterSlug", "$afterSlug")

                runOnUiThread {
                    postAdaptor.removeAll()
                    initRecyclerViewFromDB()
                }
            }
        }
    }

//    override fun onDestroy() {
//        super.onDestroy()
//        Thread {
//            AppDatabase.getInstance(this@ScrollingActivity).postDao().deleteAll()
//        }
//    }

    private fun initRecyclerViewFromDB() {
        Thread {
            val listPosts = AppDatabase.getInstance(this@ScrollingActivity).postDao().getAllPosts()

            runOnUiThread {
                postAdaptor = PostAdaptor(this, listPosts, { post : Post -> postItemClicked(post)})
                recyclerPosts.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
                recyclerPosts.adapter = postAdaptor

            }

            recyclerPosts.addOnScrollListener(object : androidx.recyclerview.widget.RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: androidx.recyclerview.widget.RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)

                    if (!recyclerView.canScrollVertically(1)) {
                        Toast.makeText(this@ScrollingActivity, "Last", Toast.LENGTH_LONG).show()

                        GlobalScope.launch {

                            // BLOCKING FUNCTION
                            Log.d("PREVIOUS_AFTER_SLUG", "$afterSlug")
                            afterSlug = Util().addPostsToDB(afterSlug, this@ScrollingActivity)
                            Log.d("AFTER_SLUG", "$afterSlug")
                            initRecyclerViewFromDB()
                        }
                    }
                }
            })
        }.start()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_scrolling, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun postItemClicked(post: Post) {
        val postIntent = Intent(this, PostActivity::class.java)
        postIntent.putExtra("POST_TITLE", post.postTitle)
        postIntent.putExtra("POST_TEXT", post.postText)
        postIntent.putExtra("POST_URL", post.postImageUrl)
        startActivity(postIntent)
    }

    fun addPostsToDB(){

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
                this@ScrollingActivity.afterSlug = body?.data?.after!!
                Log.d("UTIL", "Tried to update newAfterSlug with " + this@ScrollingActivity.afterSlug)


                val imgPosts = posts?.filter { it.data?.post_hint == "image" }
                Log.d("debug", "empty: ${imgPosts.isNullOrEmpty()}}")

                Thread {
                    if (imgPosts != null) {
                        runOnUiThread {
                            afterSlug = body?.data?.after
                        }
                        imgPosts!!.forEach {
                            val newPost = Post(
                                postId = null,
                                postTitle = it?.data?.title!!,
                                postContentHint = it?.data?.post_hint!!,
                                postText = it?.data?.selftext!!,
                                postImageUrl = it?.data?.url!!,
                                postName = it?.data?.name!!,
                                postThumbnailUrl = it?.data?.thumbnail!!
                            )
                            val newId = AppDatabase.getInstance(this@ScrollingActivity).postDao().insertPost(newPost)
                            newPost.postId = newId
                        }

                    }

                }.start()

            }
        })
        Log.d("UTIL", "newAfterSlug = " + afterSlug)

        //return this.newAfterSlug
    }

}

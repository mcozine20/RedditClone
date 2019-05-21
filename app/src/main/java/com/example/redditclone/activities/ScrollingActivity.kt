package com.example.redditclone.activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import com.example.redditclone.R
import com.example.redditclone.adaptor.PostAdaptor
import com.example.redditclone.data.AppDatabase
import com.example.redditclone.data.Post
import kotlinx.android.synthetic.main.activity_scrolling.*
import android.widget.Toast
import android.support.v7.widget.RecyclerView
import com.example.redditclone.network.Util
import java.util.*


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

        resetButton.setOnClickListener{
            Thread {
                AppDatabase.getInstance(this@ScrollingActivity).postDao().deleteAll()
                runOnUiThread {
                    postAdaptor.removeAll()
                }
                initRecyclerViewFromDB()
            }.start()
        }

        if (intent.hasExtra(KEY_AFTER_SLUG)){
            afterSlug = intent.getStringExtra(KEY_AFTER_SLUG)
        }

        initRecyclerViewFromDB()
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
                recyclerPosts.layoutManager = LinearLayoutManager(this)
                recyclerPosts.adapter = postAdaptor
            }

            recyclerPosts.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)

                    if (!recyclerView.canScrollVertically(1)) {
                        Toast.makeText(this@ScrollingActivity, "Last", Toast.LENGTH_LONG).show()
                        afterSlug = Util().addPostsToDB(afterSlug, this@ScrollingActivity)
                        initRecyclerViewFromDB()
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

}

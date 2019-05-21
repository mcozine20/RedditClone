package com.example.redditclone.activities

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.Menu
import android.view.MenuItem
import com.example.redditclone.R
import com.example.redditclone.adaptor.PostAdaptor
import com.example.redditclone.data.AppDatabase
import com.example.redditclone.data.Post
//import com.example.redditclone.dialogs.NewPostDialog
import com.example.redditclone.touch.PostRecyclerTouchCallback
import kotlinx.android.synthetic.main.activity_scrolling.*
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt
import android.widget.Toast
import android.support.v4.view.ViewCompat.canScrollVertically
import android.support.v7.widget.RecyclerView




class ScrollingActivity : AppCompatActivity() { //, NewPostDialog.PostHandler {

    lateinit var postAdaptor: PostAdaptor

    companion object {
        const val KEY_ITEM_TO_EDIT = "KEY_ITEM_TO_EDIT"
        const val KEY_WAS_OPEN = "KEY_WAS_OPEN"
        const val TAG_POST_DIALOG = "TAG_POST_DIALOG"
        const val TUTORIAL_PRIMARY_TEXT = "New Post"
        const val TUTORIAL_SECONDARY_TEXT = "Click here to add a post"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scrolling)
        setSupportActionBar(toolbar)

//        fab.setOnClickListener { view ->
//            showAddPostDialog()
//        }

        if (!wasOpenedEarlier()) {
            MaterialTapTargetPrompt.Builder(this)
                .setTarget(R.id.fab)
                .setPrimaryText(TUTORIAL_PRIMARY_TEXT)
                .setSecondaryText(TUTORIAL_SECONDARY_TEXT)
                .show()
        }

        saveFirstOpenInfo()

        initRecyclerViewFromDB()

    }

    private fun saveFirstOpenInfo() {
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        val editor = sharedPref.edit()
        editor.putBoolean(KEY_WAS_OPEN, true)
        editor.apply()
    }

    private fun wasOpenedEarlier(): Boolean {
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        return sharedPref.getBoolean(KEY_WAS_OPEN, false)
    }

    private fun initRecyclerViewFromDB() {
        Thread {
            val listPosts = AppDatabase.getInstance(this@ScrollingActivity).postDao().getAllPosts()

            runOnUiThread {
                postAdaptor = PostAdaptor(this, listPosts, { post : Post -> postItemClicked(post)})
                recyclerPosts.layoutManager = LinearLayoutManager(this)
                recyclerPosts.adapter = postAdaptor

                val itemDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
                recyclerPosts.addItemDecoration(itemDecoration)

                val callback = PostRecyclerTouchCallback(postAdaptor)
                val touchHelper = ItemTouchHelper(callback)
                touchHelper.attachToRecyclerView(recyclerPosts)
            }

            recyclerPosts.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)

                    if (!recyclerView.canScrollVertically(1)) {
                        Toast.makeText(this@ScrollingActivity, "Last", Toast.LENGTH_LONG).show()

                    }
                }
            })

        }.start()
    }

    var editIndex: Int = -1

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

//    override fun postCreated(post: Post) {
//        Thread {
//            val newId = AppDatabase.getInstance(this).postDao().insertPost(post)
//            post.postId = newId
//            runOnUiThread {
//                postAdaptor.addPost(post)
//            }
//        }.start()
//    }
//
//    override fun postUpdated(post: Post) {
//        Thread {
//            AppDatabase.getInstance(this@ScrollingActivity).postDao().updatePost(post)
//            runOnUiThread{
//                postAdaptor.updatePost(post, editIndex)
//            }
//        }.start()
//    }
//
//    override fun deleteAllPosts() {
//        Thread {
//            AppDatabase.getInstance(this@ScrollingActivity).postDao().deleteAll()
//            runOnUiThread{
//                postAdaptor.deleteAll()
//            }
//        }.start()
//    }

    private fun postItemClicked(post: Post) {
        val postIntent = Intent(this, PostActivity::class.java)
        postIntent.putExtra(Intent.EXTRA_TEXT, post.postTitle)
        startActivity(postIntent)
    }

}

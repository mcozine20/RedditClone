package com.example.redditclone.adaptor

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.View
import com.example.redditclone.R
import com.example.redditclone.activities.ScrollingActivity
import com.example.redditclone.data.AppDatabase
import com.example.redditclone.data.Post
import com.example.redditclone.touch.PostTouchHelperCallback
import kotlinx.android.synthetic.main.post_row.view.*
import java.util.*

class PostAdaptor : RecyclerView.Adapter<PostAdaptor.ViewHolder>/*, PostTouchHelperCallback*/ {

    var postItems = mutableListOf<Post>()

    private var context: Context

    private val clickListener: (Post) -> Unit

    constructor(context: Context, postsList: List<Post>, clickListener: (Post) -> Unit) : super() {
        this.context = context
        postItems.addAll(postsList)
        this.clickListener = clickListener
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val postRowView = LayoutInflater.from(viewGroup.context).inflate(
            R.layout.post_row, viewGroup, false
        )
        return ViewHolder(postRowView)
    }

    override fun getItemCount(): Int {
        return postItems.size
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val currentPost = postItems.get(viewHolder.adapterPosition)
        viewHolder.bind(currentPost, clickListener)
        /*
        viewHolder.btnDelete.setOnClickListener {
            deleteCity(viewHolder.adapterPosition)
        } */
    }

    fun addPost(post: Post) {
        postItems.add(0, post)
        notifyItemInserted(0)
    }

    fun updatePost(post: Post, editIndex: Int) {
        postItems.set(editIndex, post)
        notifyItemChanged(editIndex)
    }

    private fun deletePost(deletePosition: Int) {
        Thread {
            AppDatabase.getInstance(context).postDao().deletePost(postItems.get(deletePosition))
            (context as ScrollingActivity).runOnUiThread{
                postItems.removeAt(deletePosition)
                notifyItemRemoved(deletePosition)
            }
        }.start()
    }

    fun deleteAll() {
        Thread {
            AppDatabase.getInstance(context).postDao().deleteAll()
            (context as ScrollingActivity).runOnUiThread{
                postItems = mutableListOf()
                notifyDataSetChanged()
            }
        }.start()
    }
/*
    override fun onDismissed(position: Int) {
        deletePost(position)
    }

    override fun onItemMoved(fromPosition: Int, toPosition: Int) {
        Collections.swap(postItems, fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)
    }
*/
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        fun bind(post: Post, clickListener: (Post) -> Unit) {
            itemView.tvPostTitle.text = post.postTitle
            itemView.setOnClickListener { clickListener(post) }
        }
    }

}
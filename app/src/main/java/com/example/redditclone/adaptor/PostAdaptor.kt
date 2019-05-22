package com.example.redditclone.adaptor

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.View
import com.bumptech.glide.Glide
import com.example.redditclone.R
import com.example.redditclone.data.Post
import kotlinx.android.synthetic.main.activity_post.*
import kotlinx.android.synthetic.main.post_row.view.*

class PostAdaptor : RecyclerView.Adapter<PostAdaptor.ViewHolder> {

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

    fun removeAll() {
        postItems.clear()
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val currentPost = postItems.get(viewHolder.adapterPosition)
        viewHolder.bind(currentPost, clickListener)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        fun bind(post: Post, clickListener: (Post) -> Unit) {
            itemView.tvPostTitle.text = post.postTitle
            Glide.with(itemView).load(post.postThumbnailUrl).into(itemView.ivThumbnail)
            itemView.setOnClickListener { clickListener(post) }
        }
    }

}
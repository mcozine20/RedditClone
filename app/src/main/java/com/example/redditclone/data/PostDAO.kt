package com.example.redditclone.data

import android.arch.persistence.room.*

@Dao
interface PostDAO {
    @Query("SELECT * FROM posts")
    fun getAllPosts(): List<Post>

    @Insert
    fun insertPost(post: Post): Long

    @Insert
    fun insertPosts(vararg posts: Post): List<Long>

    @Delete
    fun deletePost(post: Post)

    @Update
    fun updatePost(post: Post)

    @Query("DELETE FROM posts")
    fun deleteAll()
}
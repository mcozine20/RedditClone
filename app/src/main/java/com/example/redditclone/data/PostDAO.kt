package com.example.redditclone.data

import androidx.room.*
import com.example.redditclone.data.AppDatabase

@Dao
interface PostDAO {
    @Query("SELECT * FROM posts")
    fun getAllPosts(): List<Post>

    @Insert
    suspend fun insertPost(post: Post): Long

    @Insert
    suspend fun insertPosts(vararg posts: Post): List<Long>

    @Delete
    suspend fun deletePost(post: Post)

    @Update
    suspend fun updatePost(post: Post)

    @Query("DELETE FROM posts")
    suspend fun deleteAll()
}
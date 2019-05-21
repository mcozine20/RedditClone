package com.example.redditclone.data

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "posts")
data class Post(
    @PrimaryKey(autoGenerate = true) var postId: Long?,
    @ColumnInfo(name = "postTitle") var postTitle: String,
    @ColumnInfo(name = "postText") var postText: String
) : Serializable
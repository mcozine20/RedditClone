package com.example.redditclone.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "posts")
data class Post(
    @PrimaryKey(autoGenerate = true) var postId: Long?,
    @ColumnInfo(name = "postTitle") var postTitle: String,
    @ColumnInfo(name = "postText") var postText: String,
    @ColumnInfo(name = "postContentHint") var postContentHint: String,
    @ColumnInfo(name = "postImageUrl") var postImageUrl: String,
    @ColumnInfo(name = "postName") var postName: String,
    @ColumnInfo(name = "postThumbnailUrl") var postThumbnailUrl: String
) : Serializable
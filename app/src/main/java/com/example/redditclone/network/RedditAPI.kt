package com.example.redditclone.network

import com.example.redditclone.data.RedditResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query


// URL: https://www.reddit.com/r/all/top.json
// HOST:https://www.reddit.com/
//
// PATH: /r/all/top.json
//
// QUERY param separator: ?
// QUERY params: base

interface RedditAPI {
    @GET("/r/all/hot.json")
    fun getPosts(@Query("after") afterSlug: String) : Call<RedditResponse>
}
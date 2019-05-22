package com.example.redditclone.data

data class RedditResponse(val kind:String?, val data:Data?)

data class Data(val modhash:String?, val dist:Int?, val children:List<RedditPost>?, val after:String?, val before:String?)

data class RedditPost(val kind:String?, val data:PostData?)

data class PostData(val subreddit:String?, val selftext: String?, val title:String?, val thumbnail:String?, val author:String?, val post_hint:String?, val url:String?, val name:String?)
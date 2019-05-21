package com.example.redditclone.activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.example.redditclone.R
import kotlinx.android.synthetic.main.activity_post.*

class PostActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)

        var intentThatStartedThis = getIntent()
        var postTitle = intentThatStartedThis.getStringExtra(Intent.EXTRA_TEXT)

        tvPostTitle.text = postTitle
    }

}
package com.example.redditclone.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.widget.EditText
import com.example.redditclone.R
import com.example.redditclone.activities.ScrollingActivity
import com.example.redditclone.data.Post
import kotlinx.android.synthetic.main.new_post_dialog.view.*
import java.lang.RuntimeException

class NewPostDialog : DialogFragment() {

    interface PostHandler {
        fun postCreated(item: Post)
        fun postUpdated(item: Post)
        fun deleteAllPosts()
    }

    private lateinit var postHandler: PostHandler

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        if (context is PostHandler) {
            postHandler = context
        } else {
            throw RuntimeException(
                getString(R.string.posthandler_interface_error))
        }
    }

    private lateinit var etPostName: EditText
    private lateinit var etPostContent: EditText

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())

        builder.setTitle(getString(R.string.new_post))

        val rootView = requireActivity().layoutInflater.inflate(
            R.layout.new_post_dialog, null
        )
        etPostName = rootView.etTitle
        etPostContent = rootView.etContent
        builder.setView(rootView)

        val arguments = this.arguments

        if (arguments != null && arguments.containsKey(ScrollingActivity.KEY_ITEM_TO_EDIT)) {
            val postItem = arguments.getSerializable(ScrollingActivity.KEY_ITEM_TO_EDIT) as Post

            etPostName.setText(postItem.postTitle)
            etPostContent.setText(postItem.postText)

            builder.setTitle(getString(R.string.edit_post))
        }

        builder.setPositiveButton(getString(R.string.ok)) {
                dialog, witch -> // empty
        }

        return builder.create()
    }


    override fun onResume() {
        super.onResume()

        val positiveButton = (dialog as AlertDialog).getButton(Dialog.BUTTON_POSITIVE)
        positiveButton.setOnClickListener {
            if (etPostName.text.isNotEmpty()) {
                val arguments = this.arguments
                if (arguments != null && arguments.containsKey(ScrollingActivity.KEY_ITEM_TO_EDIT)) {
                    handlePostEdit()
                } else {
                    handlePostCreate()
                }

                dialog.dismiss()
            } else {
                etPostName.error = getString(R.string.empty_field_error)
            }
        }
    }

    private fun handlePostCreate() {
        postHandler.postCreated(
            Post(null, etPostName.text.toString(), etPostContent.text.toString())
        )
    }

    private fun handlePostEdit() {
        val postToEdit = arguments?.getSerializable(
            ScrollingActivity.KEY_ITEM_TO_EDIT
        ) as Post
        postToEdit.postTitle = etPostName.text.toString()
        postToEdit.postText = etPostContent.text.toString()

        postHandler.postUpdated(postToEdit)
    }

}

package com.project.pradyotprakash.polking.utility.diffUtilCallbacks

import androidx.recyclerview.widget.DiffUtil
import com.project.pradyotprakash.polking.utility.CommentModel

class InnerCommentCallback(
    private val mOldList: ArrayList<CommentModel>,
    private val mNewList: ArrayList<CommentModel>
) : DiffUtil.Callback() {

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return (mOldList[oldItemPosition].docId == mNewList[newItemPosition].docId)
    }

    override fun getOldListSize(): Int {
        return mOldList.size
    }

    override fun getNewListSize(): Int {
        return mNewList.size
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldLabel = mOldList[oldItemPosition]
        val newLabel = mNewList[newItemPosition]

        return (oldLabel.comment == newLabel.comment &&
                oldLabel.commentedOn == newLabel.commentedOn &&
                oldLabel.commentedOnDate == newLabel.commentedOnDate &&
                oldLabel.givenBy == newLabel.givenBy &&
                oldLabel.dislikes == newLabel.dislikes &&
                oldLabel.likes == newLabel.likes &&
                oldLabel.commentedOnTime == newLabel.commentedOnTime &&
                oldLabel.innerComment == newLabel.innerComment &&
                oldLabel.docId == newLabel.docId &&
                oldLabel.parentComment == newLabel.parentComment &&
                oldLabel.isEdited == newLabel.isEdited)
    }
}
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
        val old = mOldList[oldItemPosition]
        val new = mNewList[newItemPosition]

        return (old.comment == new.comment &&
                old.commentedOn == new.commentedOn &&
                old.commentedOnDate == new.commentedOnDate &&
                old.givenBy == new.givenBy &&
                old.commentedOnTime == new.commentedOnTime &&
                old.docId == new.docId &&
                old.parentComment == new.parentComment &&
                old.isEdited == new.isEdited)
    }
}
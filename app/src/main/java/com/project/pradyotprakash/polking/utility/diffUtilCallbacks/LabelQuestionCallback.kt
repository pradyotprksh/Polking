package com.project.pradyotprakash.polking.utility.diffUtilCallbacks

import androidx.recyclerview.widget.DiffUtil
import com.project.pradyotprakash.polking.utility.LabelModel

class LabelQuestionCallback(
    private val mOldList: ArrayList<LabelModel>,
    private val mNewList: ArrayList<LabelModel>
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

        return (old.docId == new.docId &&
                old.questionId == new.questionId &&
                old.imageUrl == new.imageUrl)
    }
}
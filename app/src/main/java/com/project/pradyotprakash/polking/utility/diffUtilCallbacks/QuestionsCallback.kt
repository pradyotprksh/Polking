package com.project.pradyotprakash.polking.utility.diffUtilCallbacks

import androidx.recyclerview.widget.DiffUtil
import com.project.pradyotprakash.polking.utility.QuestionModel

class QuestionsCallback(
    private val mOldList: ArrayList<QuestionModel>,
    private val mNewList: ArrayList<QuestionModel>
) : DiffUtil.Callback() {

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return mOldList[oldItemPosition].docId == mNewList[newItemPosition].docId
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
                old.question == new.question &&
                old.askedBy == new.askedBy &&
                old.askedOnDate == new.askedOnDate &&
                old.askedOnTime == new.askedOnTime &&
                old.yesVote == new.yesVote &&
                old.noVote == new.noVote &&
                old.imageUrl == new.imageUrl &&
                old.imageName == new.imageName)
    }

}
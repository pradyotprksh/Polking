package com.project.pradyotprakash.polking.utility.diffUtilCallbacks

import androidx.recyclerview.widget.DiffUtil
import com.project.pradyotprakash.polking.utility.QuestionModel

class QuestionsCallback(
    private val mOldList: ArrayList<QuestionModel>,
    private val mNewList: ArrayList<QuestionModel>
) : DiffUtil.Callback() {

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return mOldList[oldItemPosition] == mNewList[newItemPosition]
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

        return oldLabel == newLabel
    }

}
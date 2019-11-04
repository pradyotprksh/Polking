package com.project.pradyotprakash.polking.utility.diffUtilCallbacks

import androidx.recyclerview.widget.DiffUtil
import com.project.pradyotprakash.polking.utility.FAQsQuestionModel

class FAQsCallback(
    private val mOldList: ArrayList<FAQsQuestionModel>,
    private val mNewList: ArrayList<FAQsQuestionModel>
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
                old.openedBy == new.openedBy &&
                old.helpFullYes == new.helpFullYes &&
                old.helpFullNo == new.helpFullNo &&
                old.type == new.type &&
                old.isTopQuestion == new.isTopQuestion &&
                old.answer == new.answer)
    }

}
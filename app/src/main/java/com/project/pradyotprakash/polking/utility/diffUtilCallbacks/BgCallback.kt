package com.project.pradyotprakash.polking.utility.diffUtilCallbacks

import androidx.recyclerview.widget.DiffUtil
import com.project.pradyotprakash.polking.utility.BgModel

class BgCallback(
    private val mOldList: ArrayList<BgModel>,
    private val mNewList: ArrayList<BgModel>
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

        return (old == new)
    }
}
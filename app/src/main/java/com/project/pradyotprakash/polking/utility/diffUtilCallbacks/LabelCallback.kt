package com.project.pradyotprakash.polking.utility.diffUtilCallbacks

import androidx.recyclerview.widget.DiffUtil

class LabelCallback(
    private val mOldLabelList: ArrayList<String>,
    private val mNewLabelList: ArrayList<String>
) : DiffUtil.Callback() {

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return mOldLabelList[oldItemPosition] == mNewLabelList[newItemPosition]
    }

    override fun getOldListSize(): Int {
        return mOldLabelList.size
    }

    override fun getNewListSize(): Int {
        return mNewLabelList.size
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldLabel = mOldLabelList[oldItemPosition]
        val newLabel = mNewLabelList[newItemPosition]

        return oldLabel == newLabel
    }

}
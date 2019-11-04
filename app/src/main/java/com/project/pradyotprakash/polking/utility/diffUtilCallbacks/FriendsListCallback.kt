package com.project.pradyotprakash.polking.utility.diffUtilCallbacks

import androidx.recyclerview.widget.DiffUtil
import com.project.pradyotprakash.polking.utility.FriendsListModel

class FriendsListCallback(
    private val mOldList: ArrayList<FriendsListModel>,
    private val mNewList: ArrayList<FriendsListModel>
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
                old.userId == new.userId &&
                old.madeFriendOn == new.madeFriendOn &&
                old.madeBestFriendOn == new.madeBestFriendOn &&
                old.isBestFriend == new.isBestFriend &&
                old.isFriend == new.isFriend)
    }

}
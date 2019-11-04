package com.project.pradyotprakash.polking.utility.diffUtilCallbacks

import androidx.recyclerview.widget.DiffUtil
import com.project.pradyotprakash.polking.utility.NotificationModel

class NotificationCallback(
    private val mOldList: ArrayList<NotificationModel>,
    private val mNewList: ArrayList<NotificationModel>
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

        return (old.notificationMessageBy == new.notificationMessageBy &&
                old.notificationMessage == new.notificationMessage &&
                old.notificationOn == new.notificationOn &&
                old.notificationForInnerCommentVotes == new.notificationForInnerCommentVotes &&
                old.notificationIsRead == new.notificationIsRead &&
                old.parentCommentVal == new.parentCommentVal &&
                old.innerCommentValue == new.innerCommentValue &&
                old.notificationForQuestionVote == new.notificationForQuestionVote &&
                old.parentCommentId == new.parentCommentId &&
                old.notificationForInnerComment == new.notificationForInnerComment &&
                old.notificationForCommentVotes == new.notificationForCommentVotes &&
                old.commentValue == new.commentValue &&
                old.docId == new.docId &&
                old.notificationQuestionId == new.notificationQuestionId &&
                old.notificationForReview == new.notificationForReview &&
                old.notificationForComment == new.notificationForComment &&
                old.notificationCommentId == new.notificationCommentId &&
                old.notificationReviewId == new.notificationReviewId &&
                old.notificationInnerCommentId == new.notificationInnerCommentId &&
                old.voteType == new.voteType)
    }
}
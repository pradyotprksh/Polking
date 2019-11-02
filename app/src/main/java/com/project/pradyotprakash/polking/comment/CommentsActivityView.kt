package com.project.pradyotprakash.polking.comment

import com.project.pradyotprakash.polking.BaseView
import com.project.pradyotprakash.polking.utility.CommentModel
import java.util.*

interface CommentsActivityView : BaseView {

    // open question stats
    fun showQuestionStats(docId: String)

    // open login
    fun openLoginAct()

    // add comment successfully
    fun successfullyAddedComment()

    // load all comments
    fun loadAllComments(allCommentList: ArrayList<CommentModel>)

    // set image url
    fun setUserProfileImage(imageUrl: String)

    // set notification count
    fun setNotificationIcon(notificationCount: String)

    // start login act
    fun startLogin()

    // start profile act
    fun startProfileAct()
}
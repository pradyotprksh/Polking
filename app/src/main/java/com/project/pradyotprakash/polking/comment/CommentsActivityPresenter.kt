package com.project.pradyotprakash.polking.comment

import com.project.pradyotprakash.polking.BasePresenter

interface CommentsActivityPresenter : BasePresenter {

    // open question stats
    fun showStats(questionId: String)

    // add comment
    fun addComment(commentVal: String, questionId: String?)

    // get all comments
    fun getComments(questionId: String?, filterBy: Int)

    // get user data
    fun getProfileData()

    // is logged in
    fun isLoggedIn()

    // add inner comment
    fun addInnerComment(commnetVal: String, commentId: String, questionId: String)
}
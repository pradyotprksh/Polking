package com.project.pradyotprakash.polking.profile

import com.project.pradyotprakash.polking.BasePresenter

interface ProfileActivityPresenter: BasePresenter {

    // get user data
    fun getUserData()

    // get background images
    fun getBackgroundImages()

    // change doc id of bg
    fun changeBgId(bgDocId: String)

    // set vote
    fun setVote(voteType: Int, docId: String)

    // show stats
    fun showStats(docId: String)

    // call the method to make all the notifications marked as read
    fun callNotificationIsReadMethod()

    // check for chat request
    fun checkForChatRequest(docId: String, askedBy: String)

    // generate chat request
    fun callGenerateChatRequest(docId: String, askedBy: String)
}
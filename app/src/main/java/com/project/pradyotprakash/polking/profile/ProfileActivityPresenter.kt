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
}
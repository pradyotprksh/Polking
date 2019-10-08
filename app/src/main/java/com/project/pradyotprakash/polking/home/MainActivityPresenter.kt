package com.project.pradyotprakash.polking.home

import com.project.pradyotprakash.polking.BasePresenter

interface MainActivityPresenter: BasePresenter {

    // check if user is logged in
    fun isLoggedIn()

    // get profile data
    fun getProfileData()

    // upload the entered question
    fun uploadQuestion(question: String)

    // get questions
    fun getQuestions()

    // set auth state listener
    fun addAuthStateListener()

    // remove listener
    fun removeListener()

    // set vote
    fun setVote(voteType: Int, docId: String)

    // show stats
    fun showStats(docId: String)

}
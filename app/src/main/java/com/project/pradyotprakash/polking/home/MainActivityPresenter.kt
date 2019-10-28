package com.project.pradyotprakash.polking.home

import android.net.Uri
import com.project.pradyotprakash.polking.BasePresenter
import java.util.*

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

    // upload question with images
    fun uploadQuestionWithImage(question: String, picOptionUri: Uri)

    // check if human face
    fun checkIfHumanFace(picOptionUri: Uri)

    // upload question with labels
    fun uploadQuestionWithImage(question: String, picOptionUri: Uri, imageLabel: ArrayList<String>)

}
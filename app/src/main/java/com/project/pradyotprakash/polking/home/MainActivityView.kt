package com.project.pradyotprakash.polking.home

import android.net.Uri
import com.project.pradyotprakash.polking.BaseView
import com.project.pradyotprakash.polking.utility.QuestionModel
import java.util.*

interface MainActivityView: BaseView {

    // start profile act
    fun startProfileAct()

    // start login act
    fun startLogin()

    // open profile edits
    fun openAddProfileDetails()

    // set user profile data
    fun setUserProfileImage(imageUrl: String?)

    // set user name
    fun setUserName(name: String)

    // hide options if not logged in
    fun hideOptions()

    // upload success
    fun showUploadedSuccess()

    // all question list
    fun loadQuestions(allQuestionList: ArrayList<QuestionModel>)

    // show options if logged in
    fun showOptions()

    // set vote
    fun setVotes(voteType: Int, docId: String)

    // show question stats
    fun showStats(docId: String)

    // open btm sheet for the question
    fun showQuestionStats(docId: String)

    // set notification view
    fun setNotificationIcon(notificationCount: String)

    // set image on the view
    fun setQuestionImage(picOptionUri: Uri)

    // delete the question uri
    fun deleteQuestionImageUri()

    // set image label
    fun setQuestionImage(picOptionUri: Uri, imageLabel: ArrayList<String>)

}
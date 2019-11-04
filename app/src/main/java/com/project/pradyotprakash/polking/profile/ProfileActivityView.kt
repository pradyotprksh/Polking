package com.project.pradyotprakash.polking.profile

import com.project.pradyotprakash.polking.BaseView
import com.project.pradyotprakash.polking.utility.BgModel
import java.util.*

interface ProfileActivityView: BaseView {

    // set bg array list
    fun setBgList(allBgList: ArrayList<BgModel>)

    // set bg image
    fun setBgImage(imageUrl: String, docId: String)

    // set profile image
    fun setUserProfileImage(imageUrl: String?)

    // open profile details page
    fun openAddProfileDetails()

    // set user name
    fun setUserName(name: String?)

    // set user data
    fun setUserDetails(question: String?, friends: String?, bestFriends: String?)

    // set votes
    fun setVotes(voteType: Int, docId: String)

    // show question stats
    fun showStats(docId: String)

    // open btm sheet for the question
    fun showQuestionStats(docId: String)

    // set notification view
    fun setNotificationIcon(notificationCount: String, notificaitonMsg: String)
}
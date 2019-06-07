package com.project.pradyotprakash.polking.home

import com.project.pradyotprakash.polking.BaseView

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
}
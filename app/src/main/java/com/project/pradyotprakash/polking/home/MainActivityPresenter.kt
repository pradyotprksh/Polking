package com.project.pradyotprakash.polking.home

import com.project.pradyotprakash.polking.BasePresenter

interface MainActivityPresenter: BasePresenter {

    // check if user is logged in
    fun isLoggedIn()

    // get profile data
    fun getProfileData()
}
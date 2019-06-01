package com.project.pradyotprakash.polking.profile

import com.project.pradyotprakash.polking.BasePresenter

interface ProfileActivityPresenter: BasePresenter {

    // get user data
    fun getUserData()

    // get background images
    fun getBackgroundImages()
}
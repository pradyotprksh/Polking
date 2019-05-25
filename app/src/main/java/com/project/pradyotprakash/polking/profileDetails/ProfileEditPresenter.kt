package com.project.pradyotprakash.polking.profileDetails

import com.project.pradyotprakash.polking.BasePresenter

interface ProfileEditPresenter: BasePresenter {

    // attach view
    fun attachView(view: ProfileEditBtmSheet)

    // check for read storage permission
    fun checkReadPermission() : Boolean

    // check for write storage permission
    fun checkWritePermission() : Boolean

}
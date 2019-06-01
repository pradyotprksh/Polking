package com.project.pradyotprakash.polking.profile

import com.project.pradyotprakash.polking.BaseView
import com.project.pradyotprakash.polking.utility.BgModel
import java.util.*

interface ProfileActivityView: BaseView {

    // hide the background option if bg not available
    fun hideBackGroundOption()

    // set bg array list
    fun setBgList(allBgList: ArrayList<BgModel>)

    // set bg image
    fun setBgImage(imageUrl: String)

    // set profile image
    fun setUserProfileImage(imageUrl: String?)

    // open profile details page
    fun openAddProfileDetails()

    // set user name
    fun setUserName(name: String?)
}
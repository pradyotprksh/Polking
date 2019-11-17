package com.project.pradyotprakash.polking.chatWindow

import com.project.pradyotprakash.polking.BaseView

interface ChatWindowView : BaseView {

    // set user data
    fun setUserData(userDetails: String)

    // set user image url
    fun setUserImage(imageUrl: String)
}
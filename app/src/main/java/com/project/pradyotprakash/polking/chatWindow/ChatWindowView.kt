package com.project.pradyotprakash.polking.chatWindow

import com.project.pradyotprakash.polking.BaseView
import com.project.pradyotprakash.polking.utility.ChatModel
import java.util.*

interface ChatWindowView : BaseView {

    // set user data
    fun setUserData(userDetails: String)

    // set user image url
    fun setUserImage(imageUrl: String)

    // message uploaded successfully
    fun messageUploaded()

    // set chat list
    fun setChatList(allChatList: ArrayList<ChatModel>)

    // show chat delete option
    fun showDeleteOption()

    // hide delete option
    fun hideDeleteOption()
}
package com.project.pradyotprakash.polking.chatWindow

import com.project.pradyotprakash.polking.BasePresenter

interface ChatWindowPresenter : BasePresenter {

    // get the chat details
    fun getChatDetails(chatWindowId: String)

    // update typing status
    fun updateTypingStatus(typingStarted: Boolean, chatWindowId: String)

    // upload message
    fun uploadMessage(text: String, chatWindowId: String)

    // get chat list
    fun getChatList(chatWindowId: String)

    // delete chat
    fun deleteChat(chatWindowId: String)
}
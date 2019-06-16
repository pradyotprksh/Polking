package com.project.pradyotprakash.polking.profile.faq

import com.project.pradyotprakash.polking.BaseView
import com.project.pradyotprakash.polking.utility.FAQsQuestionModel

interface FAQsActivityView : BaseView {

    // load question response question
    fun loadQuestionResponse(questionResponseModelList: ArrayList<FAQsQuestionModel>)

    // load friend best friend question
    fun loadFriendBestFriend(friendBestFriendModelList: ArrayList<FAQsQuestionModel>)

    // load block report question
    fun loadBlockReport(blockReportModelList: ArrayList<FAQsQuestionModel>)

    // load top questions
    fun loadTopQuestion(topQuestionModelList: ArrayList<FAQsQuestionModel>)

    // hide top question
    fun hideTopQuestion()

    // hide block report
    fun hideBlockReport()

    // hide friend best friend
    fun hideFriendBestFriend()

    // hide question response
    fun hideQuestionResponse()


}
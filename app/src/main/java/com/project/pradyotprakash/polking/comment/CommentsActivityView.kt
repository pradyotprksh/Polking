package com.project.pradyotprakash.polking.comment

import com.project.pradyotprakash.polking.BaseView

interface CommentsActivityView : BaseView {

    // open question stats
    fun showQuestionStats(docId: String)

    // open login
    fun openLoginAct()
}
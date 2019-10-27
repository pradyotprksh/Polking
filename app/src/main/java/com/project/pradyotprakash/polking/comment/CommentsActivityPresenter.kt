package com.project.pradyotprakash.polking.comment

import com.project.pradyotprakash.polking.BasePresenter

interface CommentsActivityPresenter : BasePresenter {

    // open question stats
    fun showStats(questionId: String)
}
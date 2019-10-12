package com.project.pradyotprakash.polking

interface BaseView {

    fun showLoading()
    fun hideLoading()
    fun stopAct()

    // type = 1-> error 2->information 3-> success 4-> no network
    fun showMessage(message: String, type: Int)

}
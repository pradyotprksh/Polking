package com.project.pradyotprakash.polking

interface BasePresenter {

    fun start()
    fun stop()
    fun isNetworkAvailable() : Boolean

}
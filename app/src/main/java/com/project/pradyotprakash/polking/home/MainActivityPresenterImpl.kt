package com.project.pradyotprakash.polking.home

import android.app.Activity
import javax.inject.Inject

class MainActivityPresenterImpl @Inject constructor() : MainActivityPresenter {

    lateinit var mContext: Activity
    @Inject lateinit var mView: MainActivityView

    @Inject
    internal fun MainActivityPresenterImpl(activity: Activity) {
        mContext = activity
    }

    override fun start() {

    }

    override fun stop() {

    }

    override fun isNetworkAvailable(): Boolean {
        return true
    }

}
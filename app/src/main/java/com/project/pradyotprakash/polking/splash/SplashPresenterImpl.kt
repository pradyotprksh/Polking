package com.project.pradyotprakash.polking.splash

import android.app.Activity
import javax.inject.Inject

class SplashPresenterImpl @Inject constructor() : SplashPresenter {

    lateinit var mContext: Activity
    @Inject lateinit var mView: SplashView

    @Inject
    internal fun SplashPresenterImpl(activity: Activity) {
        mContext = activity
    }


    override fun start() {
        mView.showLoading()
    }

    override fun stop() {

    }

    override fun isNetworkAvailable(): Boolean {
        return true
    }

}
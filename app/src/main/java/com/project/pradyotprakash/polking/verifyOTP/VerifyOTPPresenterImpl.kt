package com.project.pradyotprakash.polking.verifyOTP

import android.app.Activity
import javax.inject.Inject

class VerifyOTPPresenterImpl @Inject constructor() : VerifyOTPPresenter {

    lateinit var mContext: Activity
    @Inject
    lateinit var mView: VerifyOTPView

    @Inject
    internal fun VerifyOTPPresenterImpl(activity: Activity) {
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
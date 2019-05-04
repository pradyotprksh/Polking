package com.project.pradyotprakash.polking.signin

import android.app.Activity
import javax.inject.Inject

class SignInPresenterImpl @Inject constructor() : SignInPresenter {

    lateinit var mContext: Activity
    @Inject lateinit var mView: SignInView

    @Inject
    internal fun SignInPresenterImpl(activity: Activity) {
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
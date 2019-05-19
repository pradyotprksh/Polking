package com.project.pradyotprakash.polking.profile

import android.app.Activity
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

class ProfileActivityPresenterImpl @Inject constructor() : ProfileActivityPresenter {

    lateinit var mContext: Activity
    @Inject
    lateinit var mView: ProfileActivityView
    private lateinit var mAuth: FirebaseAuth

    @Inject
    internal fun ProfileActivityPresenterImpl(activity: Activity) {
        mContext = activity
        mAuth = FirebaseAuth.getInstance()
    }

    override fun start() {

    }

    override fun stop() {

    }

    override fun isNetworkAvailable(): Boolean {
        return true
    }
}
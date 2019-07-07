package com.project.pradyotprakash.polking.otherUsers

import android.app.Activity
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

class OtherUserProfilePresenterImpl @Inject constructor() : OtherUserProfilePresenter {

    lateinit var mContext: Activity
    @Inject
    lateinit var mView: OtherUserProfileView
    private lateinit var mAuth: FirebaseAuth

    @Inject
    internal fun OtherUserProfilePresenterImpl(activity: Activity) {
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
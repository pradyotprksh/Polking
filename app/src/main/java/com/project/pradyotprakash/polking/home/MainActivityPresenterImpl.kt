package com.project.pradyotprakash.polking.home

import android.app.Activity
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

class MainActivityPresenterImpl @Inject constructor() : MainActivityPresenter {

    lateinit var mContext: Activity
    @Inject lateinit var mView: MainActivityView
    private lateinit var mAuth: FirebaseAuth

    @Inject
    internal fun MainActivityPresenterImpl(activity: Activity) {
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

    override fun isLoggedIn() {
        val currentUser = mAuth.currentUser
        if (currentUser!=null) {
            mView.startProfileAct()
        } else {
            mView.startLogin()
        }
    }

}
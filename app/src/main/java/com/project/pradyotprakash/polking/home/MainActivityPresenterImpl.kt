package com.project.pradyotprakash.polking.home

import android.app.Activity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject


class MainActivityPresenterImpl @Inject constructor() : MainActivityPresenter {

    lateinit var mContext: Activity
    @Inject lateinit var mView: MainActivityView
    private lateinit var mAuth: FirebaseAuth
    private var currentUser: FirebaseUser? = null
    private lateinit var dataBase: FirebaseFirestore

    @Inject
    internal fun MainActivityPresenterImpl(activity: Activity) {
        mContext = activity
        mAuth = FirebaseAuth.getInstance()
        currentUser = mAuth.currentUser
        dataBase = FirebaseFirestore.getInstance()
    }

    override fun start() {

    }

    override fun stop() {

    }

    override fun isNetworkAvailable(): Boolean {
        return true
    }

    override fun isLoggedIn() {
        mView.showLoading()
        if (currentUser!=null) {
            mView.startProfileAct()
        } else {
            mView.startLogin()
        }
    }

    override fun getProfileData() {
        mView.showLoading()
        if (currentUser!=null) {

            dataBase.collection("users").document(currentUser!!.uid)
                .addSnapshotListener { snapshot, exception ->
                    if (exception != null) {
                        mView.showMessage(
                            "Something Went Wrong. ${exception.localizedMessage}", 1
                        )
                    }

                    if (snapshot != null && snapshot.exists()) {
                        mView.setUserProfileImage(snapshot.data!!["imageUrl"].toString())
                        mView.setUserName(snapshot.data!!["name"].toString())
                        mView.hideLoading()
                    } else {
                        mView.openAddProfileDetails()
                        mView.hideLoading()
                    }
                }
        }
    }

    override fun getBestFrndQuestions() {
        mView.showLoading()
        if (currentUser != null) {


        } else {

        }
    }

}
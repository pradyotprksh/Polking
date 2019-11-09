package com.project.pradyotprakash.polking.splash

import android.app.Activity
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class SplashPresenterImpl @Inject constructor() : SplashPresenter {

    lateinit var mContext: Activity
    @Inject lateinit var mView: SplashView
    private lateinit var dataBase: FirebaseFirestore

    @Inject
    internal fun SplashPresenterImpl(activity: Activity) {
        mContext = activity
        dataBase = FirebaseFirestore.getInstance()
    }


    override fun start() {
        dataBase.collection("status").document("status").get()
            .addOnSuccessListener { result ->
                if (result.exists()) {
                    if (result.data!!["isInDevelopment"].toString() == "true") {
                        mView.showMessage(
                            "The Server is in development. Please try after some time. Sorry for the inconvenience. \uD83D\uDE4F",
                            2
                        )
                    } else {
                        mView.showLoading()
                    }
                } else {
                    mView.showLoading()
                }

            }.addOnFailureListener {
                mView.showLoading()
            }.addOnCanceledListener {
                mView.showLoading()
            }
    }

    override fun stop() {

    }

    override fun isNetworkAvailable(): Boolean {
        return true
    }

}
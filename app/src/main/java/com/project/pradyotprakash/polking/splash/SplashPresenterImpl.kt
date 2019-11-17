package com.project.pradyotprakash.polking.splash

import android.app.Activity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.project.pradyotprakash.polking.R
import com.project.pradyotprakash.polking.utility.AppConstants.Companion.DEVELOPER_UID
import com.skydoves.whatif.whatIfNotNull
import javax.inject.Inject

class SplashPresenterImpl @Inject constructor() : SplashPresenter {

    lateinit var mContext: Activity
    @Inject lateinit var mView: SplashView
    private lateinit var dataBase: FirebaseFirestore
    private lateinit var mAuth: FirebaseAuth

    @Inject
    internal fun SplashPresenterImpl(activity: Activity) {
        mContext = activity
        dataBase = FirebaseFirestore.getInstance()
        mAuth = FirebaseAuth.getInstance()
    }


    override fun start() {
        dataBase.collection("status").document("status").get()
            .addOnSuccessListener { result ->
                if (result.exists()) {
                    if (result.data!!["isInDevelopment"].toString() == "true") {
                        mAuth.currentUser.whatIfNotNull(
                            whatIf = {
                                if (mAuth.currentUser!!.uid == DEVELOPER_UID) {
                                    mView.showLoading()
                                }
                            },
                            whatIfNot = {
                                mView.showMessage(
                                    mContext.getString(R.string.server_in_developemnt) +
                                            mContext.getString(R.string.try_some_time) +
                                            "Sorry for the inconvenience. \uD83D\uDE4F",
                                    2
                                )
                            }
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
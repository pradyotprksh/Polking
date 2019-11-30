package com.project.pradyotprakash.polking.faq

import android.app.Activity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class FAQsActivityPresenterImpl @Inject constructor() :
    FAQsActivityPresenter {

    lateinit var mContext: Activity
    @Inject
    lateinit var mView: FAQsActivityView
    private lateinit var mAuth: FirebaseAuth
    private var currentUser: FirebaseUser? = null
    private lateinit var dataBase: FirebaseFirestore

    @Inject
    internal fun FAQsActivityPresenterImpl(activity: Activity) {
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

    override fun getQuestions() {

    }
}
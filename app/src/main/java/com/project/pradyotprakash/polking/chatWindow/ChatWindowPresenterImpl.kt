package com.project.pradyotprakash.polking.chatWindow

import android.app.Activity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.FirebaseFunctions
import javax.inject.Inject

class ChatWindowPresenterImpl @Inject constructor() : ChatWindowPresenter {

    lateinit var mContext: Activity
    @Inject
    lateinit var mView: ChatWindowView
    private lateinit var mAuth: FirebaseAuth
    private lateinit var firebaseFunctions: FirebaseFunctions
    private lateinit var addCommentFirestore: FirebaseFirestore
    private lateinit var dataBase: FirebaseFirestore

    @Inject
    internal fun ChatWindowPresenterImpl(activity: Activity) {
        mContext = activity
        mAuth = FirebaseAuth.getInstance()
        firebaseFunctions = FirebaseFunctions.getInstance()
        addCommentFirestore = FirebaseFirestore.getInstance()
        dataBase = FirebaseFirestore.getInstance()
    }

    override fun start() {

    }

    override fun stop() {

    }

    override fun isNetworkAvailable(): Boolean {
        return true
    }
}
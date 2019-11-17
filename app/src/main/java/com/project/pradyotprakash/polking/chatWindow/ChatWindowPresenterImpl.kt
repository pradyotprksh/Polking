package com.project.pradyotprakash.polking.chatWindow

import android.app.Activity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.FirebaseFunctions
import com.skydoves.whatif.whatIfNotNull
import javax.inject.Inject

class ChatWindowPresenterImpl @Inject constructor() : ChatWindowPresenter {

    lateinit var mContext: Activity
    @Inject
    lateinit var mView: ChatWindowView
    private lateinit var mAuth: FirebaseAuth
    private lateinit var firebaseFunctions: FirebaseFunctions
    private lateinit var dataBase: FirebaseFirestore
    private lateinit var getUserData: FirebaseFirestore

    @Inject
    internal fun ChatWindowPresenterImpl(activity: Activity) {
        mContext = activity
        mAuth = FirebaseAuth.getInstance()
        firebaseFunctions = FirebaseFunctions.getInstance()
        dataBase = FirebaseFirestore.getInstance()
        getUserData = FirebaseFirestore.getInstance()
    }

    override fun getChatDetails(chatWindowId: String) {
        mAuth.currentUser.whatIfNotNull {
            dataBase
                .collection("request")
                .document(mAuth.currentUser!!.uid)
                .collection("messageRequest")
                .document(chatWindowId)
                .addSnapshotListener { snapshot, exception ->
                    exception.whatIfNotNull {
                        mView.showMessage(
                            "Something Went Wrong. ${exception!!.localizedMessage}", 1
                        )
                    }

                    snapshot.whatIfNotNull {
                        if (snapshot!!.exists()) {
                            try {

                                getUserData
                                    .collection("users")
                                    .document(chatWindowId)
                                    .addSnapshotListener { usersnapshot, exception ->

                                        exception.whatIfNotNull {
                                            mView.showMessage(
                                                "Something Went Wrong. ${exception!!.localizedMessage}",
                                                1
                                            )
                                        }

                                        usersnapshot.whatIfNotNull {
                                            if (usersnapshot!!.exists()) {
                                                try {

                                                    if (snapshot.data!!["isUserTyping"] == "true") {
                                                        mView.setUserData(
                                                            usersnapshot.data!!["name"].toString()
                                                                    + " is typing..."
                                                        )
                                                    } else {
                                                        mView.setUserData(usersnapshot.data!!["name"].toString())
                                                    }

                                                    mView.setUserImage(usersnapshot.data!!["imageUrl"].toString())

                                                } catch (exception: Exception) {
                                                    exception.printStackTrace()
                                                }
                                            }
                                        }
                                    }

                            } catch (exception: Exception) {
                                exception.printStackTrace()
                            }
                        }
                    }

                }
        }
    }

    override fun updateTypingStatus(typingStarted: Boolean, chatWindowId: String) {
        dataBase
            .collection("request")
            .document(chatWindowId)
            .collection("messageRequest")
            .document(mAuth.currentUser!!.uid)
            .update(
                mapOf(
                    "isUserTyping" to typingStarted.toString()
                )
            )
    }

    override fun start() {

    }

    override fun stop() {

    }

    override fun isNetworkAvailable(): Boolean {
        return true
    }
}
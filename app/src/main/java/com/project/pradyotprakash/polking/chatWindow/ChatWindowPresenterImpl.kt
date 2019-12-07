package com.project.pradyotprakash.polking.chatWindow

import android.app.Activity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage
import com.google.firebase.ml.naturallanguage.smartreply.FirebaseTextMessage
import com.google.firebase.ml.naturallanguage.smartreply.SmartReplySuggestionResult
import com.project.pradyotprakash.polking.R
import com.project.pradyotprakash.polking.utility.ChatModel
import com.skydoves.whatif.whatIfNotNull
import java.util.*
import javax.inject.Inject

class ChatWindowPresenterImpl @Inject constructor() : ChatWindowPresenter {

    lateinit var mContext: Activity
    @Inject
    lateinit var mView: ChatWindowView
    private lateinit var mAuth: FirebaseAuth
    private lateinit var firebaseFunctions: FirebaseFunctions
    private lateinit var dataBase: FirebaseFirestore
    private lateinit var dataBase1: FirebaseFirestore
    private lateinit var getUserData: FirebaseFirestore
    private val allChatList = ArrayList<ChatModel>()
    private val commonReply = ArrayList<String>()

    @Inject
    internal fun ChatWindowPresenterImpl(activity: Activity) {
        mContext = activity
        mAuth = FirebaseAuth.getInstance()
        firebaseFunctions = FirebaseFunctions.getInstance()
        dataBase = FirebaseFirestore.getInstance()
        dataBase1 = FirebaseFirestore.getInstance()
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

                                if (snapshot["isCompleted"] == "true") {
                                    mView.stopAct()
                                }

                                if (snapshot["requestBy"] == mAuth.currentUser!!.uid) {
                                    mView.showDeleteOption()
                                } else {
                                    mView.hideDeleteOption()
                                }

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
                                                        mView.showUserTyping(
                                                            usersnapshot.data!!["name"].toString()
                                                                    + " typing..."
                                                        )
                                                    } else {
                                                        mView.hideUserTyping()
                                                    }

                                                    mView.setUserData(usersnapshot.data!!["name"].toString())

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

    override fun uploadMessage(text: String, chatWindowId: String) {
        mAuth.currentUser.whatIfNotNull {
            dataBase
                .collection("acceptedRequest")
                .document(mAuth.currentUser!!.uid)
                .collection("messages")
                .document(chatWindowId)
                .collection("${mAuth.currentUser!!.uid}$chatWindowId")
                .document()
                .set(
                    hashMapOf(
                        "message" to text,
                        "messageOn" to (System.currentTimeMillis() / 1000).toString(),
                        "messageBy" to mAuth.currentUser!!.uid,
                        "messageTo" to chatWindowId
                    )
                ).addOnSuccessListener {
                    dataBase1
                        .collection("acceptedRequest")
                        .document(chatWindowId)
                        .collection("messages")
                        .document(mAuth.currentUser!!.uid)
                        .collection("$chatWindowId${mAuth.currentUser!!.uid}")
                        .document()
                        .set(
                            hashMapOf(
                                "message" to text,
                                "messageOn" to (System.currentTimeMillis() / 1000).toString(),
                                "messageBy" to mAuth.currentUser!!.uid,
                                "messageTo" to chatWindowId
                            )
                        ).addOnSuccessListener {
                            mView.messageUploaded()
                        }
                }
        }
    }

    override fun getChatList(chatWindowId: String) {
        mAuth.currentUser.whatIfNotNull {
            dataBase
                .collection("acceptedRequest")
                .document(mAuth.currentUser!!.uid)
                .collection("messages")
                .document(chatWindowId)
                .collection("${mAuth.currentUser!!.uid}$chatWindowId")
                .orderBy("messageOn", Query.Direction.DESCENDING)
                .addSnapshotListener { snapshot, exception ->
                    exception.whatIfNotNull {
                        mView.showMessage(
                            "Something Went Wrong. ${exception!!.localizedMessage}", 1
                        )
                    }

                    allChatList.clear()

                    try {
                        for (doc in snapshot!!) {
                            val docId = doc.id
                            val chatList: ChatModel =
                                doc.toObject<ChatModel>(ChatModel::class.java).withId(docId)
                            this.allChatList.add(chatList)
                        }

                        callSmartReplyMethod(allChatList[0].message, allChatList[0].docId)

                        mView.setChatList(allChatList)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        mView.showMessage(e.localizedMessage, 1)
                    }
                }
        }
    }

    private fun callSmartReplyMethod(message: String, docId: String) {
        commonReply.clear()
        commonReply.add("Yes")
        commonReply.add("No")
        commonReply.add("Hello")
        commonReply.add(mContext.getString(R.string.smiley))
        commonReply.add(mContext.getString(R.string.thumbs_up))
        commonReply.add(mContext.getString(R.string.thumbs_down))
        val conversation = ArrayList<FirebaseTextMessage>()
        conversation.add(
            FirebaseTextMessage.createForRemoteUser(
                message, System.currentTimeMillis(), docId
            )
        )
        val smartReply = FirebaseNaturalLanguage.getInstance().smartReply
        smartReply.suggestReplies(conversation)
            .addOnSuccessListener { result ->
                if (result.status == SmartReplySuggestionResult.STATUS_NOT_SUPPORTED_LANGUAGE) {
                    mView.enterHardcodeReply(commonReply)
                } else if (result.status == SmartReplySuggestionResult.STATUS_SUCCESS) {
                    if (result.suggestions.size > 0) {
                        mView.enterSmartReplies(result.suggestions)
                    } else {
                        mView.enterHardcodeReply(commonReply)
                    }
                } else if (result.status == SmartReplySuggestionResult.STATUS_NO_REPLY) {
                    mView.enterHardcodeReply(commonReply)
                }
            }
            .addOnFailureListener {
                mView.enterHardcodeReply(commonReply)
            }
    }

    override fun deleteChat(chatWindowId: String) {
        mAuth.currentUser.whatIfNotNull {
            mView.showLoading()
            val data = HashMap<String, Any>()
            data["isCompleted"] = "true"
            dataBase
                .collection("request")
                .document(mAuth.currentUser!!.uid)
                .collection("messageRequest")
                .document(chatWindowId)
                .update(data)
                .addOnCompleteListener {
                    dataBase1
                        .collection("request")
                        .document(chatWindowId)
                        .collection("messageRequest")
                        .document(mAuth.currentUser!!.uid)
                        .update(data)
                        .addOnCompleteListener {
                            mView.hideLoading()
                            mView.stopAct()
                        }
                }
                .addOnFailureListener {
                    mView.hideLoading()
                }
        }
    }

    override fun start() {

    }

    override fun stop() {

    }

    override fun isNetworkAvailable(): Boolean {
        return true
    }
}
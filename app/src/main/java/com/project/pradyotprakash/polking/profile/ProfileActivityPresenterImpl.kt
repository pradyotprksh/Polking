package com.project.pradyotprakash.polking.profile

import android.app.Activity
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.FirebaseFunctionsException
import com.project.pradyotprakash.polking.R
import com.project.pradyotprakash.polking.utility.BgModel
import com.skydoves.whatif.whatIfNotNull
import java.util.*
import javax.inject.Inject

class ProfileActivityPresenterImpl @Inject constructor() : ProfileActivityPresenter {

    lateinit var mContext: Activity
    @Inject
    lateinit var mView: ProfileActivityView
    private lateinit var mAuth: FirebaseAuth
    private var currentUser: FirebaseUser? = null
    private lateinit var dataBase: FirebaseFirestore
    private lateinit var bgDataBase: FirebaseFirestore
    private lateinit var addVotesDataBase: FirebaseFirestore
    private lateinit var firebaseFunctions: FirebaseFunctions
    private lateinit var generateChatRequest: FirebaseFirestore
    private lateinit var getQuestionFirestore: FirebaseFirestore
    private lateinit var isChatRequestAlreadyMade: FirebaseFirestore
    private val allBgList = ArrayList<BgModel>()

    @Inject
    internal fun ProfileActivityPresenterImpl(activity: Activity) {
        mContext = activity
        mAuth = FirebaseAuth.getInstance()
        currentUser = mAuth.currentUser
        dataBase = FirebaseFirestore.getInstance()
        bgDataBase = FirebaseFirestore.getInstance()
        addVotesDataBase = FirebaseFirestore.getInstance()
        generateChatRequest = FirebaseFirestore.getInstance()
        getQuestionFirestore = FirebaseFirestore.getInstance()
        isChatRequestAlreadyMade = FirebaseFirestore.getInstance()
        firebaseFunctions = FirebaseFunctions.getInstance()
    }

    override fun getUserData() {
        mView.showLoading()
        currentUser.whatIfNotNull(
            whatIf = {
                setListnerForUserData()
                getTheUserData()
            },
            whatIfNot = {
                mView.showMessage(mContext.getString(R.string.user_not_found), 1)
            }
        )
    }

    private fun getTheUserData() {
        dataBase.collection("users").document(currentUser!!.uid).get()
            .addOnSuccessListener { result ->
                if (result.exists()) {

                    bgDataBase.collection("background_images")
                        .document(result.getString("bg_option")!!).get()
                        .addOnSuccessListener { resultBg ->
                            mView.showLoading()
                            if (result.exists()) {
                                mView.setBgImage(resultBg.getString("imageUrl")!!, resultBg.id)
                                mView.hideLoading()
                            } else {
                                mView.showMessage(mContext.getString(R.string.not_found_bg), 1)
                                mView.hideLoading()
                            }
                        }.addOnFailureListener { exception ->
                            mView.showMessage(
                                "Something Went Wrong. ${exception.localizedMessage}",
                                1
                            )
                            mView.hideLoading()
                        }.addOnCanceledListener {
                            mView.showMessage(mContext.getString(R.string.loading_image_cancel), 4)
                            mView.hideLoading()
                        }

                    mView.hideLoading()
                } else {
                    mView.openAddProfileDetails()
                    mView.hideLoading()
                }
            }.addOnFailureListener {
            mView.showMessage(mContext.getString(R.string.something_went_wring_oops), 1)
            mView.hideLoading()
        }.addOnCanceledListener {
            mView.showMessage(mContext.getString(R.string.getting_details), 4)
            mView.hideLoading()
        }
    }

    override fun callGenerateChatRequest(docId: String, askedBy: String) {
        mAuth.currentUser.whatIfNotNull {
            mView.showLoading()
            generateChatRequest
                .collection("request")
                .document(mAuth.currentUser!!.uid)
                .collection("messageRequest")
                .document(askedBy)
                .set(
                    hashMapOf(
                        "requestBy" to mAuth.currentUser!!.uid,
                        "requestTo" to askedBy,
                        "questionId" to docId,
                        "isRequestAccepted" to "false"
                    )
                )
                .addOnCompleteListener {
                    getQuestionFirestore
                        .collection("request")
                        .document(askedBy)
                        .collection("messageRequest")
                        .document(mAuth.currentUser!!.uid)
                        .set(
                            hashMapOf(
                                "requestBy" to mAuth.currentUser!!.uid,
                                "requestTo" to askedBy,
                                "questionId" to docId,
                                "isRequestAccepted" to "false"
                            )
                        )
                        .addOnCompleteListener {
                            mView.hideLoading()
                        }
                }
                .addOnFailureListener {
                    mView.hideLoading()
                }
        }
    }

    override fun checkForChatRequest(docId: String, askedBy: String) {
        mContext.whatIfNotNull {
            mAuth.currentUser.whatIfNotNull {
                isChatRequestAlreadyMade
                    .collection("request")
                    .document(mAuth.currentUser!!.uid)
                    .collection("messageRequest")
                    .document(askedBy)
                    .get()
                    .addOnSuccessListener { result ->
                        if (result.exists()) {

                            dataBase.collection("users").document(askedBy)
                                .addSnapshotListener { snapshot, exception ->
                                    exception.whatIfNotNull {
                                        mView.showMessage(
                                            "Something Went Wrong. ${exception!!.localizedMessage}",
                                            1
                                        )
                                    }

                                    snapshot.whatIfNotNull {
                                        if (snapshot!!.exists()) {
                                            if (result["questionId"] == docId) {
                                                mView.showMessage(
                                                    "Request already sent. " +
                                                            snapshot.data!!["name"].toString() +
                                                            " needs to accept your chat request to start this conversation",
                                                    2
                                                )
                                            } else {
                                                if (result["isRequestAccepted"] == "false") {
                                                    mView.showMessage(
                                                        "You already made a request for another question.",
                                                        2
                                                    )
                                                } else {
                                                    mView.showMessage(
                                                        "You already in a conversation with " +
                                                                snapshot.data!!["name"].toString() + " related to another question.",
                                                        2
                                                    )
                                                }
                                            }
                                        } else {
                                            mView.showChatRequestOption(docId, askedBy)
                                        }
                                    }
                                }
                        } else {
                            mView.showChatRequestOption(docId, askedBy)
                        }
                    }
                    .addOnFailureListener {
                        mView.showChatRequestOption(docId, askedBy)
                    }

            }
        }
    }

    private fun setListnerForUserData() {
        dataBase.collection("users").document(currentUser!!.uid)
            .addSnapshotListener { snapshot, exception ->
                exception.whatIfNotNull {
                    mView.showMessage(
                        "Something Went Wrong. ${exception!!.localizedMessage}", 1
                    )
                }

                snapshot.whatIfNotNull(
                    whatIf = {
                        if (snapshot!!.exists()) {
                            mView.setUserDetails(
                                snapshot.data!!["questions"].toString(),
                                snapshot.data!!["friends"].toString(),
                                snapshot.data!!["best_friends"].toString()
                            )
                            mView.setUserProfileImage(snapshot.data!!["imageUrl"].toString())
                            mView.setUserName(snapshot.data!!["name"].toString())
                            mView.setNotificationIcon(
                                snapshot.data!!["notificationCount"].toString(),
                                snapshot.data!!["notificationMsg"].toString()
                            )
                            mView.hideLoading()
                        } else {
                            mView.openAddProfileDetails()
                            mView.hideLoading()
                        }
                    },
                    whatIfNot = {
                        mView.openAddProfileDetails()
                        mView.hideLoading()
                    }
                )
            }
    }

    override fun callNotificationIsReadMethod() {
        callNotificationFunction()
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    task.exception.whatIfNotNull {
                        val e = task.exception
                        e.whatIfNotNull {
                            if (e is FirebaseFunctionsException) {
                                mView.hideLoading()
                                mView.showMessage("Something Went Wrong. ${e.localizedMessage}", 1)
                            }
                        }
                    }
                }
            }
    }

    private fun callNotificationFunction(): Task<String> {
        val data = HashMap<String, Any>()
        data["userId"] = mAuth.currentUser!!.uid

        return firebaseFunctions
            .getHttpsCallable("markedNotificationRead")
            .call(data)
            .continueWith { task ->
                val result = task.result?.data as String
                result
            }
    }

    override fun getBackgroundImages() {
        currentUser.whatIfNotNull(
            whatIf = {
                mView.showLoading()
                dataBase.collection("background_images")
                    .addSnapshotListener { documentSnapshot, exception ->

                        exception.whatIfNotNull {
                            mView.showMessage(
                                "Something Went Wrong. ${exception!!.localizedMessage}", 1
                            )
                        }

                        this.allBgList.clear()

                        for (doc in documentSnapshot!!) {
                            val docId = doc.id
                            val bgList: BgModel =
                                doc.toObject<BgModel>(BgModel::class.java).withId(docId)
                            this.allBgList.add(bgList)
                        }
                        mView.setBgList(allBgList)
                        mView.hideLoading()
                    }
            },
            whatIfNot = {
                mView.showMessage(mContext.getString(R.string.user_not_found), 1)
            }
        )
    }

    override fun start() {

    }

    override fun stop() {

    }

    override fun isNetworkAvailable(): Boolean {
        return true
    }

    override fun changeBgId(bgDocId: String) {
        mView.showLoading()
        val userData = HashMap<String, Any>()
        userData["bg_option"] = bgDocId
        dataBase.collection("users")
            .document(mAuth.currentUser!!.uid)
            .update(userData).addOnSuccessListener {
                mView.hideLoading()
            }.addOnFailureListener { exception ->
                mView.hideLoading()
                mView.showMessage(
                    "Something Went Wrong. ${exception.localizedMessage}",
                    1
                )
            }.addOnCanceledListener {
                mView.hideLoading()
                mView.showMessage(mContext.getString(R.string.not_uploaded), 4)
            }.addOnCompleteListener {
                mView.stopAct()
            }
    }

    override fun setVote(voteType: Int, docId: String) {
        mView.showLoading()

        currentUser.whatIfNotNull {
            val voteData = HashMap<String, Any>()
            voteData["voteType"] = voteType
            voteData["questionId"] = docId

            addVotesDataBase
                .collection("users")
                .document(currentUser!!.uid)
                .collection("votes")
                .document(docId)
                .set(voteData).addOnSuccessListener {
                    mView.hideLoading()
                }.addOnFailureListener { exception ->
                    mView.showMessage(
                        "Something Went Wrong. ${exception.localizedMessage}",
                        1
                    )
                    mView.hideLoading()
                }.addOnCanceledListener {
                    mView.showMessage(mContext.getString(R.string.not_uploaded_question), 4)
                    mView.hideLoading()
                }
        }
    }

    override fun showStats(docId: String) {
        callStatsFunction(docId)
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    task.exception.whatIfNotNull(
                        whatIf = {
                            val e = task.exception
                            e.whatIfNotNull(
                                whatIf = {
                                    if (e is FirebaseFunctionsException) {
                                        mView.hideLoading()
                                        openStats(docId)
                                    } else {
                                        openStats(docId)
                                    }
                                },
                                whatIfNot = {
                                    openStats(docId)
                                }
                            )
                        },
                        whatIfNot = {
                            openStats(docId)
                        }
                    )
                } else {
                    openStats(docId)
                }
            }
    }

    private fun openStats(docId: String) {
        mView.hideLoading()
        mView.showQuestionStats(docId)
    }

    private fun callStatsFunction(docId: String): Task<String> {
        val data = HashMap<String, Any>()
        data["questionId"] = docId
        data["userId"] = mAuth.currentUser!!.uid

        return firebaseFunctions
            .getHttpsCallable("showQuestionStats")
            .call(data)
            .continueWith { task ->
                val result = task.result?.data as String
                result
            }
    }

}
package com.project.pradyotprakash.polking.profile.faq

import android.app.Activity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.project.pradyotprakash.polking.R
import com.project.pradyotprakash.polking.utility.FAQsQuestionModel
import javax.inject.Inject

class FAQsActivityPresenterImpl @Inject constructor() : FAQsActivityPresenter {

    lateinit var mContext: Activity
    @Inject
    lateinit var mView: FAQsActivityView
    private lateinit var mAuth: FirebaseAuth
    private var currentUser: FirebaseUser? = null
    private lateinit var dataBase: FirebaseFirestore
    private val questionResponseModelList = ArrayList<FAQsQuestionModel>()
    private val friendBestFriendModelList = ArrayList<FAQsQuestionModel>()
    private val blockReportModelList = ArrayList<FAQsQuestionModel>()
    private val topQuestionModelList = ArrayList<FAQsQuestionModel>()

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
        if (mAuth.currentUser != null) {
            mView.showLoading()

            dataBase.collection("faqs").addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    mView.showMessage(
                        "Something Went Wrong. ${exception.localizedMessage}", 1
                    )
                }

                for (doc in snapshot!!.documentChanges) {
                    mView.showLoading()
                    if (doc.type == DocumentChange.Type.ADDED) {

                        val docId = doc.document.id
                        val quesList: FAQsQuestionModel =
                            doc.document.toObject<FAQsQuestionModel>(FAQsQuestionModel::class.java).withId(docId)

                        if (quesList.isTopQuestion == "true") {
                            topQuestionModelList.add(quesList)
                        }
                        when {
                            quesList.type == "queRes" -> {
                                questionResponseModelList.add(quesList)
                            }
                            quesList.type == "friendBestFriend" -> {
                                friendBestFriendModelList.add(quesList)
                            }
                            quesList.type == "blockReport" -> {
                                blockReportModelList.add(quesList)
                            }
                        }
                    }
                }

                if (topQuestionModelList.size > 0) {
                    mView.loadTopQuestion(topQuestionModelList)
                } else {
                    mView.hideTopQuestion()
                }
                if (questionResponseModelList.size > 0) {
                    mView.loadQuestionResponse(questionResponseModelList)
                } else {
                    mView.hideQuestionResponse()
                }
                if (friendBestFriendModelList.size > 0) {
                    mView.loadFriendBestFriend(friendBestFriendModelList)
                } else {
                    mView.hideFriendBestFriend()
                }
                if (blockReportModelList.size > 0) {
                    mView.loadBlockReport(blockReportModelList)
                } else {
                    mView.hideBlockReport()
                }

                mView.hideLoading()

            }

        } else {
            mView.hideLoading()
            mView.showMessage(mContext.getString(R.string.user_not_found), 1)
        }
    }
}
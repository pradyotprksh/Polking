package com.project.pradyotprakash.polking.home

import android.annotation.SuppressLint
import android.app.Activity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.project.pradyotprakash.polking.R
import com.project.pradyotprakash.polking.utility.QuestionModel
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.HashMap


class MainActivityPresenterImpl @Inject constructor() : MainActivityPresenter {

    private val allQuestionList = ArrayList<QuestionModel>()
    lateinit var mContext: Activity
    @Inject lateinit var mView: MainActivityView
    private lateinit var mAuth: FirebaseAuth
    private var currentUser: FirebaseUser? = null
    private lateinit var dataBase: FirebaseFirestore

    @SuppressLint("SimpleDateFormat")
    var dateFormat: SimpleDateFormat = SimpleDateFormat("yyyy/MM/dd")
    @SuppressLint("SimpleDateFormat")
    var timeFormat: SimpleDateFormat = SimpleDateFormat("HH:mm:ss")

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
        } else {
            mView.hideOptions()
        }
    }

    override fun addAuthStateListener() {
        mAuth.addAuthStateListener { mAuth ->
            if (mAuth.currentUser != null) {
                currentUser = mAuth.currentUser
                getUserData()
                mView.showOptions()
            } else {
                mView.hideOptions()
            }
        }
    }

    private fun getUserData() {
        dataBase.collection("users").document(currentUser!!.uid).get().addOnSuccessListener { result ->

            if (result.exists()) {
                mView.setUserProfileImage(result.data!!["imageUrl"].toString())
                mView.setUserName(result.data!!["name"].toString())
                mView.hideLoading()
            } else {
                mView.hideOptions()
            }

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

    override fun getBestFrndQuestions() {
        mView.showLoading()
        if (currentUser != null) {

        } else {
            mView.hideOptions()
        }
    }

    override fun removeListener() {
        mAuth.removeAuthStateListener {}
    }

    override fun uploadQuestion(question: String) {
        mView.showLoading()
        if (currentUser != null) {
            val date = Date()
            val questionData = HashMap<String, Any>()
            questionData["question"] = question
            questionData["askedBy"] = currentUser!!.uid
            questionData["askedOnDate"] = dateFormat.format(date)
            questionData["askedOnTime"] = timeFormat.format(date)
            questionData["yesVote"] = "0"
            questionData["noVote"] = "0"

            dataBase.collection("question").add(questionData).addOnSuccessListener {
                mView.hideLoading()
                mView.showUploadedSuccess()
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

        } else {
            mView.hideOptions()
        }
    }

    override fun getQuestions() {

        dataBase.collection("question")
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    mView.showMessage(
                        "Something Went Wrong. ${exception.localizedMessage}", 1
                    )
                }

                allQuestionList.clear()

                try {
                    for (doc in snapshot!!.documentChanges) {
                        mView.showLoading()
                        if (doc.type == DocumentChange.Type.ADDED ||
                            doc.type == DocumentChange.Type.MODIFIED ||
                            doc.type == DocumentChange.Type.REMOVED
                        ) {

                            val docId = doc.document.id
                            val quesList: QuestionModel =
                                doc.document.toObject<QuestionModel>(QuestionModel::class.java).withId(docId)
                            this.allQuestionList.add(quesList)

                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    mView.showMessage(e.localizedMessage, 1)
                }

                mView.loadQuestions(allQuestionList)

                mView.hideLoading()

            }

    }

}
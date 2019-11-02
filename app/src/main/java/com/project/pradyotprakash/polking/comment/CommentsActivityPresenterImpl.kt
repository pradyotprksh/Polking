package com.project.pradyotprakash.polking.comment

import android.annotation.SuppressLint
import android.app.Activity
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.FirebaseFunctionsException
import com.project.pradyotprakash.polking.R
import com.project.pradyotprakash.polking.utility.CommentModel
import com.skydoves.whatif.whatIfNotNull
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class CommentsActivityPresenterImpl @Inject constructor() : CommentsActivityPresenter {

    private val allCommentList: ArrayList<CommentModel> = ArrayList()
    lateinit var mContext: Activity
    @Inject
    lateinit var mView: CommentsActivityView
    private lateinit var mAuth: FirebaseAuth
    private lateinit var firebaseFunctions: FirebaseFunctions
    private lateinit var addCommentFirestore: FirebaseFirestore
    private lateinit var dataBase: FirebaseFirestore
    private lateinit var getCommentFirestore: FirebaseFirestore

    @SuppressLint("SimpleDateFormat")
    var dateFormat: SimpleDateFormat = SimpleDateFormat("yyyy/MM/dd")
    @SuppressLint("SimpleDateFormat")
    var timeFormat: SimpleDateFormat = SimpleDateFormat("HH:mm:ss")
    @SuppressLint("SimpleDateFormat")
    var dateTimeFormat: SimpleDateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")

    @Inject
    internal fun CommentsActivityPresenterImpl(activity: Activity) {
        mContext = activity
        mAuth = FirebaseAuth.getInstance()
        firebaseFunctions = FirebaseFunctions.getInstance()
        addCommentFirestore = FirebaseFirestore.getInstance()
        getCommentFirestore = FirebaseFirestore.getInstance()
        dataBase = FirebaseFirestore.getInstance()
    }

    override fun showStats(questionId: String) {
        mAuth.currentUser.whatIfNotNull(
            whatIf = {
                mView.showLoading()
                callStatsFunction(questionId)
                    .addOnCompleteListener { task ->
                        if (!task.isSuccessful) {
                            task.exception.whatIfNotNull(
                                whatIf = {
                                    val e = task.exception
                                    e.whatIfNotNull(
                                        whatIf = {
                                            if (e is FirebaseFunctionsException) {
                                                mView.hideLoading()
                                                mView.showMessage(
                                                    "Something Went Wrong." +
                                                            " ${e.localizedMessage}", 1
                                                )
                                            } else {
                                                openStats(questionId)
                                            }
                                        },
                                        whatIfNot = {
                                            openStats(questionId)
                                        }
                                    )
                                },
                                whatIfNot = {
                                    openStats(questionId)
                                }
                            )
                        } else {
                            openStats(questionId)
                        }
                    }
            },
            whatIfNot = {
                mView.openLoginAct()
            }
        )
    }

    override fun getComments(questionId: String?, filterBy: Int) {
        mAuth.currentUser.whatIfNotNull(
            whatIf = {
                questionId.whatIfNotNull {
                    mView.showLoading()
                    val filterByVal = when (filterBy) {
                        1 -> "commentedOn"
                        2 -> "likes"
                        else -> "dislikes"
                    }
                    getCommentFirestore
                        .collection("question")
                        .document(questionId!!)
                        .collection("comments")
                        .orderBy(filterByVal, Query.Direction.DESCENDING)
                        .addSnapshotListener { snapshot, exception ->
                            exception.whatIfNotNull {
                                mView.showMessage(
                                    "Something Went Wrong. ${exception!!.localizedMessage}", 1
                                )
                            }

                            allCommentList.clear()

                            try {
                                for (doc in snapshot!!) {
                                    val docId = doc.id
                                    val commentList: CommentModel =
                                        doc.toObject<CommentModel>(CommentModel::class.java)
                                            .withId(docId)
                                    this.allCommentList.add(commentList)
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                                mView.showMessage(e.localizedMessage, 1)
                            }

                            mView.loadAllComments(allCommentList)

                            mView.hideLoading()

                        }
                }
            },
            whatIfNot = {
                mView.openLoginAct()
            }
        )
    }

    override fun addInnerComment(commnetVal: String, commentId: String, questionId: String) {
        mAuth.currentUser.whatIfNotNull(
            whatIf = {
                questionId.whatIfNotNull {
                    mView.showLoading()
                    val date = Date()
                    val questionData = HashMap<String, Any>()
                    questionData["comment"] = commnetVal
                    questionData["givenBy"] = mAuth.currentUser!!.uid
                    questionData["commentedOn"] = dateTimeFormat.format(date)
                    questionData["commentedOnDate"] = dateFormat.format(date)
                    questionData["commentedOnTime"] = timeFormat.format(date)
                    questionData["likes"] = "0"
                    questionData["dislikes"] = "0"
                    questionData["innerComment"] = "0"
                    questionData["isEdited"] = "false"
                    questionData["parentComment"] = commentId

                    addCommentFirestore
                        .collection("question")
                        .document(questionId)
                        .collection("comments")
                        .document(commentId)
                        .collection("innerComment")
                        .add(questionData)
                        .addOnSuccessListener {
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
            },
            whatIfNot = {
                mView.openLoginAct()
            }
        )
    }

    override fun addComment(commentVal: String, questionId: String?) {
        mAuth.currentUser.whatIfNotNull(
            whatIf = {
                questionId.whatIfNotNull {
                    mView.showLoading()
                    val date = Date()
                    val questionData = HashMap<String, Any>()
                    questionData["comment"] = commentVal
                    questionData["givenBy"] = mAuth.currentUser!!.uid
                    questionData["commentedOn"] = dateTimeFormat.format(date)
                    questionData["commentedOnDate"] = dateFormat.format(date)
                    questionData["commentedOnTime"] = timeFormat.format(date)
                    questionData["likes"] = "0"
                    questionData["dislikes"] = "0"
                    questionData["innerComment"] = "0"
                    questionData["isEdited"] = "false"

                    addCommentFirestore
                        .collection("question")
                        .document(questionId!!)
                        .collection("comments")
                        .add(questionData)
                        .addOnSuccessListener {
                            mView.hideLoading()
                            mView.successfullyAddedComment()
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
            },
            whatIfNot = {
                mView.openLoginAct()
            }
        )
    }

    override fun getProfileData() {
        mView.showLoading()
        mAuth.currentUser.whatIfNotNull(
            whatIf = {
                dataBase.collection("users").document(mAuth.currentUser!!.uid)
                    .addSnapshotListener { snapshot, exception ->
                        exception.whatIfNotNull {
                            mView.showMessage(
                                "Something Went Wrong. ${exception!!.localizedMessage}", 1
                            )
                        }

                        snapshot.whatIfNotNull {
                            if (snapshot!!.exists()) {
                                mView.setUserProfileImage(snapshot.data!!["imageUrl"].toString())
                                mView.setNotificationIcon(snapshot.data!!["notificationCount"].toString())
                                mView.hideLoading()
                            }
                        }
                    }
            },
            whatIfNot = {
                mView.hideLoading()
            }
        )
    }

    override fun isLoggedIn() {
        mView.showLoading()
        mAuth.currentUser.whatIfNotNull(
            whatIf = {
                mView.startProfileAct()
            },
            whatIfNot = {
                mView.startLogin()
            }
        )
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

    override fun start() {

    }

    override fun stop() {

    }

    override fun isNetworkAvailable(): Boolean {
        return true
    }
}
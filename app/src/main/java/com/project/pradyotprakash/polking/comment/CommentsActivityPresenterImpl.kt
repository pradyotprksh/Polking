package com.project.pradyotprakash.polking.comment

import android.app.Activity
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.FirebaseFunctionsException
import com.skydoves.whatif.whatIfNotNull
import javax.inject.Inject

class CommentsActivityPresenterImpl @Inject constructor() : CommentsActivityPresenter {

    lateinit var mContext: Activity
    @Inject
    lateinit var mView: CommentsActivityView
    private lateinit var mAuth: FirebaseAuth
    private lateinit var firebaseFunctions: FirebaseFunctions

    @Inject
    internal fun CommentsActivityPresenterImpl(activity: Activity) {
        mContext = activity
        mAuth = FirebaseAuth.getInstance()
        firebaseFunctions = FirebaseFunctions.getInstance()
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
package com.project.pradyotprakash.polking.profile.notification

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.Coil
import coil.api.load
import coil.request.Request
import com.google.android.material.chip.Chip
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.project.pradyotprakash.polking.R
import com.project.pradyotprakash.polking.home.MainActivity
import com.project.pradyotprakash.polking.profile.ProfileActivity
import com.project.pradyotprakash.polking.utility.NotificationModel
import com.project.pradyotprakash.polking.utility.diffUtilCallbacks.NotificationCallback
import com.skydoves.whatif.whatIfNotNull
import de.hdodenhof.circleimageview.CircleImageView

class NotificationsAdapter(
    private val context: Context,
    private val activity: Activity
) : RecyclerView.Adapter<NotificationsAdapter.ViewHolder>() {

    private var mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private var userFirestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var questionFirestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val allNotificationsList: ArrayList<NotificationModel> = ArrayList()

    fun updateListItems(list: ArrayList<NotificationModel>) {
        val diffCallback = NotificationCallback(this.allNotificationsList, list)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        this.allNotificationsList.clear()
        this.allNotificationsList.addAll(list)
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val view =
            LayoutInflater.from(p0.context).inflate(R.layout.notification_layout_adapter, p0, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return allNotificationsList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {

        userFirestore.collection("users").document(allNotificationsList[p1].notificationMessageBy)
            .addSnapshotListener { snapshot, exception ->
                exception.whatIfNotNull {
                    (context as MainActivity).showMessage(
                        "Something Went Wrong. ${exception!!.localizedMessage}", 1
                    )
                }

                snapshot.whatIfNotNull {
                    if (snapshot!!.exists()) {
                        p0.progressBar7.visibility = View.VISIBLE
                        p0.name_chio.text = snapshot.data!!["name"].toString()
                        p0.user_iv.load(snapshot.data!!["imageUrl"].toString(),
                            Coil.loader(),
                            builder = {
                                this.listener(object : Request.Listener {
                                    override fun onError(data: Any, throwable: Throwable) {
                                        p0.progressBar7.visibility = View.GONE
                                        p0.user_iv.load(R.drawable.ic_default_appcolor)
                                    }

                                    override fun onSuccess(
                                        data: Any,
                                        source: coil.decode.DataSource
                                    ) {
                                        super.onSuccess(data, source)
                                        p0.progressBar7.visibility = View.GONE
                                        p0.user_iv.borderColor =
                                            context.resources.getColor(R.color.colorPrimary)
                                        p0.user_iv.borderWidth = 2
                                    }
                                })
                            })
                        when {
                            allNotificationsList[p1].notificationForQuestionVote == "true" ->
                                setQuestionNotification(p0, p1, snapshot)
                            allNotificationsList[p1].notificationForReview == "true" -> {
                                p0.see_question_chip.visibility = View.GONE
                                p0.notification_message.text =
                                    allNotificationsList[p1].notificationMessage
                            }
                            allNotificationsList[p1].notificationForComment == "true" -> {
                                setCommentNotification(p0, p1, snapshot)
                            }
                            allNotificationsList[p1].notificationForInnerComment == "true" -> {
                                setInnerCommentNotification(p0, p1, snapshot)
                            }
                            allNotificationsList[p1].notificationForCommentVotes == "true" -> {
                                setVoteCommentNotification(p0, p1, snapshot)
                            }
                            allNotificationsList[p1].notificationForInnerCommentVotes == "true" -> {
                                setInnerVoteCommentNotification(p0, p1, snapshot)
                            }
                            else -> {
                                p0.see_question_chip.visibility = View.GONE
                                p0.notification_message.text =
                                    allNotificationsList[p1].notificationMessage
                            }
                        }
                    }
                }

            }

        p0.name_chio.setOnClickListener {
            openUserDetails(allNotificationsList[p1].notificationMessageBy)
        }

        p0.user_iv.setOnClickListener {
            openUserDetails(allNotificationsList[p1].notificationMessageBy)
        }

        p0.see_question_chip.setOnClickListener {
            if (context is ProfileActivity) {
                if (p0.see_question_chip.text == context.getString(R.string.see_comment_list)) {
                    context.openCommentList(
                        allNotificationsList[p1].notificationQuestionId,
                        allNotificationsList[p1].notificationCommentId
                    )
                } else if (p0.see_question_chip.text == context.getString(R.string.see_question)) {
                    context.showStats(allNotificationsList[p1].notificationQuestionId)
                }
            }
        }

    }

    private fun setInnerVoteCommentNotification(
        p0: ViewHolder,
        p1: Int,
        snapshot: DocumentSnapshot
    ) {
        p0.see_question_chip.visibility = View.VISIBLE
        p0.see_question_chip.text = context.getString(R.string.see_comment_list)
        questionFirestore.collection("question")
            .document(allNotificationsList[p1].notificationQuestionId)
            .get()
            .addOnSuccessListener { result ->
                if (result.exists()) {
                    if (allNotificationsList[p1].voteType == "1") {
                        when {
                            snapshot.data!!["gender"].toString() == "0" ->
                                p0.notification_message.text =
                                    allNotificationsList[p1].notificationMessage + " " +
                                            allNotificationsList[p1].commentValue + " for the question" +
                                            " \"" + result.data!!["question"].toString() +
                                            "\" and he agrees with your comment."
                            snapshot.data!!["gender"].toString() == "1" ->
                                p0.notification_message.text =
                                    allNotificationsList[p1].notificationMessage + " " +
                                            allNotificationsList[p1].commentValue + " for the question" +
                                            " \"" + result.data!!["question"].toString() +
                                            "\" and she agrees with your comment."
                            else -> p0.notification_message.text =
                                allNotificationsList[p1].notificationMessage + " " +
                                        allNotificationsList[p1].commentValue + " for the question" +
                                        " \"" + result.data!!["question"].toString() +
                                        "\" and agrees with your comment."
                        }
                    } else {
                        when {
                            snapshot.data!!["gender"].toString() == "0" ->
                                p0.notification_message.text =
                                    allNotificationsList[p1].notificationMessage + " " +
                                            allNotificationsList[p1].commentValue + " for the question" +
                                            " \"" + result.data!!["question"].toString() +
                                            "\" and he disagrees with your comment."
                            snapshot.data!!["gender"].toString() == "1" ->
                                p0.notification_message.text =
                                    allNotificationsList[p1].notificationMessage + " " +
                                            allNotificationsList[p1].commentValue + " for the question" +
                                            " \"" + result.data!!["question"].toString() +
                                            "\" and she disagrees with your comment."
                            else -> p0.notification_message.text =
                                allNotificationsList[p1].notificationMessage + " " +
                                        allNotificationsList[p1].commentValue + " for the question" +
                                        " \"" + result.data!!["question"].toString() +
                                        "\" and disagrees with your comment."
                        }
                    }
                }
            }
    }

    private fun setVoteCommentNotification(
        p0: ViewHolder,
        p1: Int,
        snapshot: DocumentSnapshot
    ) {
        p0.see_question_chip.visibility = View.VISIBLE
        p0.see_question_chip.text = context.getString(R.string.see_comment_list)
        questionFirestore.collection("question")
            .document(allNotificationsList[p1].notificationQuestionId)
            .get()
            .addOnSuccessListener { result ->
                if (result.exists()) {
                    if (allNotificationsList[p1].voteType == "1") {
                        when {
                            snapshot.data!!["gender"].toString() == "0" ->
                                p0.notification_message.text =
                                    allNotificationsList[p1].notificationMessage + " " +
                                            allNotificationsList[p1].commentValue + " for the question" +
                                            " \"" + result.data!!["question"].toString() +
                                            "\" and he agrees with your comment."
                            snapshot.data!!["gender"].toString() == "1" ->
                                p0.notification_message.text =
                                    allNotificationsList[p1].notificationMessage + " " +
                                            allNotificationsList[p1].commentValue + " for the question" +
                                            " \"" + result.data!!["question"].toString() +
                                            "\" and she agrees with your comment."
                            else -> p0.notification_message.text =
                                allNotificationsList[p1].notificationMessage + " " +
                                        allNotificationsList[p1].commentValue + " for the question" +
                                        " \"" + result.data!!["question"].toString() +
                                        "\" and agrees with your comment."
                        }
                    } else {
                        when {
                            snapshot.data!!["gender"].toString() == "0" ->
                                p0.notification_message.text =
                                    allNotificationsList[p1].notificationMessage + " " +
                                            allNotificationsList[p1].commentValue + " for the question" +
                                            " \"" + result.data!!["question"].toString() +
                                            "\" and he disagrees with your comment."
                            snapshot.data!!["gender"].toString() == "1" ->
                                p0.notification_message.text =
                                    allNotificationsList[p1].notificationMessage + " " +
                                            allNotificationsList[p1].commentValue + " for the question" +
                                            " \"" + result.data!!["question"].toString() +
                                            "\" and she disagrees with your comment."
                            else -> p0.notification_message.text =
                                allNotificationsList[p1].notificationMessage + " " +
                                        allNotificationsList[p1].commentValue + " for the question" +
                                        " \"" + result.data!!["question"].toString() +
                                        "\" and disagrees with your comment."
                        }
                    }
                }
            }
    }

    private fun setInnerCommentNotification(
        p0: ViewHolder,
        p1: Int,
        snapshot: DocumentSnapshot
    ) {
        p0.see_question_chip.visibility = View.VISIBLE
        p0.see_question_chip.text = context.getString(R.string.see_comment_list)
        questionFirestore.collection("question")
            .document(allNotificationsList[p1].notificationQuestionId)
            .get()
            .addOnSuccessListener { result ->
                if (result.exists()) {
                    p0.notification_message.text =
                        allNotificationsList[p1].notificationMessage + " " +
                                allNotificationsList[p1].parentCommentVal +
                                " \"" + result.data!!["question"].toString() +
                                "\" which is " + allNotificationsList[p1].innerCommentValue
                }
            }
    }

    private fun setCommentNotification(
        p0: ViewHolder,
        p1: Int,
        snapshot: DocumentSnapshot
    ) {
        p0.see_question_chip.visibility = View.VISIBLE
        p0.see_question_chip.text = context.getString(R.string.see_comment_list)
        questionFirestore.collection("question")
            .document(allNotificationsList[p1].notificationQuestionId)
            .get()
            .addOnSuccessListener { result ->
                if (result.exists()) {
                    p0.notification_message.text =
                        allNotificationsList[p1].notificationMessage +
                                " \"" + result.data!!["question"].toString() +
                                "\" which is " + allNotificationsList[p1].commentValue
                }
            }
    }

    @SuppressLint("SetTextI18n")
    private fun setQuestionNotification(
        p0: ViewHolder,
        p1: Int,
        snapshot: DocumentSnapshot
    ) {
        p0.see_question_chip.visibility = View.VISIBLE
        p0.see_question_chip.text = context.getString(R.string.see_question)
        questionFirestore.collection("question")
            .document(allNotificationsList[p1].notificationQuestionId)
            .get()
            .addOnSuccessListener { result ->
                if (result.exists()) {
                    if (allNotificationsList[p1].voteType == "1") {
                        when {
                            snapshot.data!!["gender"].toString() == "0" ->
                                p0.notification_message.text =
                                    allNotificationsList[p1].notificationMessage +
                                            " \"" + result.data!!["question"].toString() +
                                            "\" and he agrees with your question."
                            snapshot.data!!["gender"].toString() == "1" ->
                                p0.notification_message.text =
                                    allNotificationsList[p1].notificationMessage +
                                            " \"" + result.data!!["question"].toString() +
                                            "\" and she agrees with your question."
                            else -> p0.notification_message.text =
                                allNotificationsList[p1].notificationMessage +
                                        " \"" + result.data!!["question"].toString() +
                                        "\" and agrees with your question."
                        }
                    } else {
                        when {
                            snapshot.data!!["gender"].toString() == "0" ->
                                p0.notification_message.text =
                                    allNotificationsList[p1].notificationMessage +
                                            " \"" + result.data!!["question"].toString() +
                                            "\" and he disagrees with your question."
                            snapshot.data!!["gender"].toString() == "1" ->
                                p0.notification_message.text =
                                    allNotificationsList[p1].notificationMessage +
                                            " \"" + result.data!!["question"].toString() +
                                            "\" and she disagrees with your question."
                            else -> p0.notification_message.text =
                                allNotificationsList[p1].notificationMessage +
                                        " \"" + result.data!!["question"].toString() +
                                        "\" and disagrees with your question."
                        }
                    }
                }
            }
    }

    private fun openUserDetails(notificationMessageBy: String) {
        mAuth.currentUser.whatIfNotNull {
            if (mAuth.currentUser!!.uid != notificationMessageBy) {
                if (context is ProfileActivity) {
                    context.openProfileDetails(notificationMessageBy)
                }
            }
        }
    }

    inner class ViewHolder(mView: View) : RecyclerView.ViewHolder(mView) {
        val name_chio: Chip = mView.findViewById(R.id.name_chio)
        val see_question_chip: Chip = mView.findViewById(R.id.see_question_chip)
        val user_iv: CircleImageView = mView.findViewById(R.id.user_iv)
        val notification_message: TextView = mView.findViewById(R.id.notification_message)
        val progressBar7: ProgressBar = mView.findViewById(R.id.progressBar7)
    }

}

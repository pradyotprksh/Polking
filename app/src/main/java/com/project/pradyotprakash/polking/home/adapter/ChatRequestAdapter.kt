package com.project.pradyotprakash.polking.home.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.Coil
import coil.api.load
import coil.transform.CircleCropTransformation
import com.google.android.material.chip.Chip
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.project.pradyotprakash.polking.R
import com.project.pradyotprakash.polking.home.MainActivity
import com.project.pradyotprakash.polking.profile.ProfileActivity
import com.project.pradyotprakash.polking.utility.diffUtilCallbacks.LabelCallback
import com.project.pradyotprakash.polking.utility.logd
import com.skydoves.whatif.whatIfNotNull

class ChatRequestAdapter(
    private val context: Context,
    private val activity: Activity
) : RecyclerView.Adapter<ChatRequestAdapter.ViewHolder>() {

    private val mChatRequestList: ArrayList<String> = ArrayList()
    private var getRequestData: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var getUserData: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var mAuth: FirebaseAuth = FirebaseAuth.getInstance()

    fun updateListItems(chats: ArrayList<String>) {
        val diffCallback = LabelCallback(this.mChatRequestList, chats)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        this.mChatRequestList.clear()
        this.mChatRequestList.addAll(chats)
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val view =
            LayoutInflater.from(p0.context).inflate(R.layout.label_layout, p0, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mChatRequestList.size
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {

        p0.chat_Tv.chipIcon = context.resources.getDrawable(R.drawable.ic_default_appcolor)

        getChatValues(p0, p1)

        setOnClickListners(p0, p1)
    }

    private fun setOnClickListners(p0: ViewHolder, p1: Int) {
        p0.chat_Tv.setOnClickListener {
            getRequestData
                .collection("request")
                .document(mAuth.currentUser!!.uid)
                .collection("messageRequest")
                .document(mChatRequestList[p1])
                .get()
                .addOnSuccessListener { result ->

                    result.whatIfNotNull {
                        if (result!!.exists()) {
                            try {

                                getUserData
                                    .collection("users")
                                    .document(mChatRequestList[p1])
                                    .addSnapshotListener { usersnapshot, exception ->

                                        exception.whatIfNotNull {
                                            if (activity is MainActivity) {
                                                activity.showMessage(
                                                    "Something Went Wrong. ${exception!!.localizedMessage}",
                                                    1
                                                )
                                            } else if (activity is ProfileActivity) {
                                                activity.showMessage(
                                                    "Something Went Wrong. ${exception!!.localizedMessage}",
                                                    1
                                                )
                                            }
                                        }

                                        usersnapshot.whatIfNotNull {
                                            if (usersnapshot!!.exists()) {
                                                try {
                                                    if (result.data!!["requestBy"].toString() ==
                                                        mAuth.currentUser!!.uid
                                                    ) {

                                                        if (result.data!!["isRequestAccepted"].toString() ==
                                                            "true"
                                                        ) {
                                                            if (activity is MainActivity) {
                                                                activity
                                                                    .openChatWindow(mChatRequestList[p1])
                                                            }
                                                        } else {
                                                            if (activity is MainActivity)
                                                                activity.showMessage(
                                                                    usersnapshot.data!!["name"].toString() +
                                                                            " Haven't Accepted Your Request Yet.",
                                                                    3
                                                                )
                                                        }
                                                    } else {
                                                        if (result.data!!["isRequestAccepted"].toString() ==
                                                            "false"
                                                        ) {
                                                            if (activity is MainActivity) {
                                                                activity
                                                                    .showOptionForAcceptRequest(
                                                                        mChatRequestList[p1]
                                                                    )
                                                            }
                                                        } else {
                                                            if (activity is MainActivity) {
                                                                activity
                                                                    .openChatWindow(mChatRequestList[p1])
                                                            }
                                                        }
                                                    }
                                                } catch (exception: Exception) {
                                                    if (activity is MainActivity) {
                                                        activity.logd(exception.localizedMessage)
                                                    }
                                                }
                                            }
                                        }
                                    }

                            } catch (exception: Exception) {
                                if (activity is MainActivity) {
                                    activity.logd(exception.localizedMessage)
                                }
                            }
                        }
                    }

                }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun getChatValues(p0: ViewHolder, p1: Int) {
        mAuth.currentUser.whatIfNotNull {
            getRequestData
                .collection("request")
                .document(mAuth.currentUser!!.uid)
                .collection("messageRequest")
                .document(mChatRequestList[p1])
                .addSnapshotListener { snapshot, exception ->
                    exception.whatIfNotNull {
                        if (activity is MainActivity) {
                            activity.showMessage(
                                "Something Went Wrong. ${exception!!.localizedMessage}", 1
                            )
                        } else if (activity is ProfileActivity) {
                            activity.showMessage(
                                "Something Went Wrong. ${exception!!.localizedMessage}", 1
                            )
                        }
                    }

                    snapshot.whatIfNotNull {
                        if (snapshot!!.exists()) {
                            try {

                                getUserData
                                    .collection("users")
                                    .document(mChatRequestList[p1])
                                    .addSnapshotListener { usersnapshot, exception ->

                                        exception.whatIfNotNull {
                                            if (activity is MainActivity) {
                                                activity.showMessage(
                                                    "Something Went Wrong. ${exception!!.localizedMessage}",
                                                    1
                                                )
                                            } else if (activity is ProfileActivity) {
                                                activity.showMessage(
                                                    "Something Went Wrong. ${exception!!.localizedMessage}",
                                                    1
                                                )
                                            }
                                        }

                                        usersnapshot.whatIfNotNull {
                                            if (usersnapshot!!.exists()) {
                                                try {

                                                    if (snapshot.data!!["requestBy"].toString() ==
                                                        mAuth.currentUser!!.uid
                                                    ) {

                                                        if (snapshot.data!!["isRequestAccepted"].toString() ==
                                                            "false"
                                                        ) {
                                                            p0.chat_Tv.text =
                                                                usersnapshot.data!!["name"].toString() +
                                                                        " Haven't Accepted Your Request Yet."
                                                        } else {
                                                            if (snapshot.data!!["isUserTyping"].toString() ==
                                                                "true"
                                                            ) {
                                                                p0.chat_Tv.text =
                                                                    usersnapshot.data!!["name"].toString() +
                                                                            " Is Typing..."
                                                                p0.chat_Tv.setTextColor(
                                                                    context.resources.getColor(R.color.agree_color)
                                                                )
                                                            } else {
                                                                p0.chat_Tv.text =
                                                                    "Chat with " + usersnapshot.data!!["name"].toString()
                                                                p0.chat_Tv.setTextColor(
                                                                    context.resources.getColor(R.color.black)
                                                                )
                                                            }
                                                        }

                                                    } else {

                                                        if (snapshot.data!!["isRequestAccepted"].toString() ==
                                                            "false"
                                                        ) {
                                                            p0.chat_Tv.text =
                                                                "Accept Chat Request from " +
                                                                        usersnapshot.data!!["name"].toString() +
                                                                        "?"
                                                        } else {
                                                            if (snapshot.data!!["isUserTyping"].toString() ==
                                                                "true"
                                                            ) {
                                                                p0.chat_Tv.text =
                                                                    usersnapshot.data!!["name"].toString() +
                                                                            " Is Typing..."
                                                                p0.chat_Tv.setTextColor(
                                                                    context.resources.getColor(R.color.agree_color)
                                                                )
                                                            } else {
                                                                p0.chat_Tv.text =
                                                                    "Chat with " +
                                                                            usersnapshot.data!!["name"].toString()
                                                                p0.chat_Tv.setTextColor(
                                                                    context.resources.getColor(R.color.black)
                                                                )
                                                            }
                                                        }

                                                    }

                                                    Coil.load(
                                                        context,
                                                        usersnapshot.data!!["imageUrl"].toString()
                                                    ) {
                                                        this.transformations(
                                                            CircleCropTransformation()
                                                        )
                                                        target { drawable ->
                                                            p0.chat_Tv.chipIcon = drawable
                                                        }
                                                    }

                                                } catch (exception: Exception) {
                                                    if (activity is MainActivity) {
                                                        activity.logd(exception.localizedMessage)
                                                    }
                                                }
                                            }
                                        }
                                    }

                            } catch (exception: Exception) {
                                if (activity is MainActivity) {
                                    activity.logd(exception.localizedMessage)
                                }
                            }
                        }
                    }

                }
        }
    }

    inner class ViewHolder(mView: View) : RecyclerView.ViewHolder(mView) {
        val chat_Tv: Chip = mView.findViewById(R.id.label_tv)
    }

}

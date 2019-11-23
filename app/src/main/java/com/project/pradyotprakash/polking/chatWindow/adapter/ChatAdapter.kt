package com.project.pradyotprakash.polking.chatWindow.adapter

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
import com.project.pradyotprakash.polking.chatWindow.ChatWindow
import com.project.pradyotprakash.polking.utility.ChatModel
import com.project.pradyotprakash.polking.utility.diffUtilCallbacks.ChatCallback
import com.skydoves.whatif.whatIfNotNull

class ChatAdapter(
    private val context: Context,
    private val activity: Activity
) : RecyclerView.Adapter<ChatAdapter.ViewHolder>() {

    private var mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private var userFirestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val mChatList: ArrayList<ChatModel> = ArrayList()

    fun updateListItems(chats: ArrayList<ChatModel>) {
        val diffCallback = ChatCallback(this.mChatList, chats)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        this.mChatList.clear()
        this.mChatList.addAll(chats)
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val view =
            LayoutInflater.from(p0.context).inflate(R.layout.chat_item_layout, p0, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mChatList.size
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        mAuth.currentUser.whatIfNotNull {
            if (mChatList[p1].messageBy == mAuth.currentUser!!.uid) {
                yourVisible(p0, p1)
                getYourImage(p0, p1)
                p0.yourMsg_tv.text = mChatList[p1].message
            } else {
                otherVisible(p0, p1)
                getOtherImage(p0, p1)
                p0.otherMsg_tv.text = mChatList[p1].message
            }
        }
    }

    private fun getYourImage(p0: ViewHolder, p1: Int) {
        userFirestore
            .collection("users")
            .document(mChatList[p1].messageBy)
            .addSnapshotListener { snapshot, exception ->

                exception.whatIfNotNull {
                    if (activity is ChatWindow) {
                        activity.showMessage(
                            "Something Went Wrong. ${exception!!.localizedMessage}", 1
                        )
                    }
                }

                snapshot.whatIfNotNull {
                    if (snapshot!!.exists()) {
                        try {
                            Coil.load(
                                context,
                                snapshot.data!!["imageUrl"].toString()
                            ) {
                                this.transformations(
                                    CircleCropTransformation()
                                )
                                target { drawable ->
                                    p0.yourMsg_tv.chipIcon = drawable
                                }
                            }
                        } catch (e: Exception) {
                            if (activity is ChatWindow) {
                                activity.showMessage(
                                    "Something Went Wrong. ${e.localizedMessage}", 1
                                )
                            }
                        }
                    }
                }
            }
    }

    private fun getOtherImage(p0: ViewHolder, p1: Int) {
        userFirestore
            .collection("users")
            .document(mChatList[p1].messageBy)
            .addSnapshotListener { snapshot, exception ->

                exception.whatIfNotNull {
                    if (activity is ChatWindow) {
                        activity.showMessage(
                            "Something Went Wrong. ${exception!!.localizedMessage}", 1
                        )
                    }
                }

                snapshot.whatIfNotNull {
                    if (snapshot!!.exists()) {
                        try {
                            Coil.load(
                                context,
                                snapshot.data!!["imageUrl"].toString()
                            ) {
                                this.transformations(
                                    CircleCropTransformation()
                                )
                                target { drawable ->
                                    p0.otherMsg_tv.chipIcon = drawable
                                }
                            }
                        } catch (e: Exception) {
                            if (activity is ChatWindow) {
                                activity.showMessage(
                                    "Something Went Wrong. ${e.localizedMessage}", 1
                                )
                            }
                        }
                    }
                }
            }
    }

    private fun otherVisible(p0: ViewHolder, p1: Int) {
        p0.otherMsg_tv.visibility = View.VISIBLE
        p0.yourMsg_tv.visibility = View.GONE
    }

    private fun yourVisible(p0: ViewHolder, p1: Int) {
        p0.otherMsg_tv.visibility = View.GONE
        p0.yourMsg_tv.visibility = View.VISIBLE
    }

    inner class ViewHolder(mView: View) : RecyclerView.ViewHolder(mView) {
        val otherMsg_tv: Chip = mView.findViewById(R.id.otherMsg_tv)
        val yourMsg_tv: Chip = mView.findViewById(R.id.yourMsg_tv)
    }

}
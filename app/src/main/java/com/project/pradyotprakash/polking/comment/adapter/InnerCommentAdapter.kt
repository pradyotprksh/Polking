package com.project.pradyotprakash.polking.comment.adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.Coil
import coil.api.load
import coil.request.Request
import coil.transform.BlurTransformation
import coil.transform.GrayscaleTransformation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.project.pradyotprakash.polking.R
import com.project.pradyotprakash.polking.comment.CommentsAcrivity
import com.project.pradyotprakash.polking.utility.CommentModel
import com.project.pradyotprakash.polking.utility.diffUtilCallbacks.InnerCommentCallback
import com.skydoves.whatif.whatIfNotNull
import de.hdodenhof.circleimageview.CircleImageView
import java.text.SimpleDateFormat

class InnerCommentAdapter(
    private val context: Context,
    private val activity: Activity
) : RecyclerView.Adapter<InnerCommentAdapter.ViewHolder>() {

    private val allCommentList: ArrayList<CommentModel> = ArrayList()
    private lateinit var questionId: String
    private var mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private var userFirestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val view =
            LayoutInflater.from(p0.context).inflate(R.layout.inner_comment_layout, p0, false)
        return ViewHolder(view)
    }

    fun updateListItems(comments: ArrayList<CommentModel>) {
        val diffCallback = InnerCommentCallback(this.allCommentList, comments)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        this.allCommentList.clear()
        this.allCommentList.addAll(comments)
        diffResult.dispatchUpdatesTo(this)
    }

    override fun getItemCount(): Int {
        return allCommentList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, pos: Int) {
        getUserData(holder, pos)

        holder.commentTv.text = allCommentList[pos].comment
    }

    private fun getUserData(holder: ViewHolder, pos: Int) {
        userFirestore.collection("users").document(allCommentList[pos].givenBy)
            .addSnapshotListener { snapshot, exception ->

                exception.whatIfNotNull {
                    if (activity is CommentsAcrivity) {
                        activity.showMessage(
                            "Something Went Wrong. ${exception!!.localizedMessage}", 1
                        )
                    }
                }

                snapshot.whatIfNotNull {
                    if (snapshot!!.exists()) {
                        try {
                            holder.user_iv.load(snapshot.data!!["imageUrl"].toString(),
                                Coil.loader(),
                                builder = {
                                    mAuth.currentUser.whatIfNotNull(
                                        whatIf = {

                                        },
                                        whatIfNot = {
                                            this.transformations(
                                                GrayscaleTransformation(),
                                                BlurTransformation(context)
                                            )
                                        })
                                    this.listener(object : Request.Listener {
                                        override fun onError(data: Any, throwable: Throwable) {
                                            holder.user_iv.load(R.drawable.ic_default_appcolor)
                                        }

                                        override fun onSuccess(
                                            data: Any,
                                            source: coil.decode.DataSource
                                        ) {
                                            super.onSuccess(data, source)
                                            holder.user_iv.borderColor =
                                                context.resources.getColor(R.color.white)
                                            holder.user_iv.borderWidth = 2
                                        }
                                    })
                                })

                            val date =
                                SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse(allCommentList[pos].commentedOn)
                            val dateFinal = SimpleDateFormat("EEE, MMM d, ''yy h:mm a").format(date)

                            holder.userData_tv.text =
                                "${snapshot.data!!["name"].toString()} | $dateFinal"
                        } catch (exception: Exception) {
                            if (activity is CommentsAcrivity) {
                                activity.showMessage(
                                    "Something Went Wrong. ${exception.localizedMessage}", 1
                                )
                            }
                        }
                    }
                }
            }

        holder.user_iv.setOnClickListener {
            if (context is CommentsAcrivity) {
                mAuth.currentUser.whatIfNotNull(
                    whatIf = {
                        if (mAuth.currentUser!!.uid != allCommentList[pos].givenBy) {
                            context.openProfileDetails(allCommentList[pos].givenBy)
                        } else {
                            context.startProfileAct()
                        }
                    },
                    whatIfNot = {
                        context.startLogin()
                    }
                )
            }
        }

    }

    fun setQuestionId(questionId: String) {
        this.questionId = questionId
    }

    inner class ViewHolder(mView: View) : RecyclerView.ViewHolder(mView) {
        val userData_tv: TextView = mView.findViewById(R.id.userData_tv)
        val commentTv: TextView = mView.findViewById(R.id.commentTv)
        val user_iv: CircleImageView = mView.findViewById(R.id.user_iv)
    }

}
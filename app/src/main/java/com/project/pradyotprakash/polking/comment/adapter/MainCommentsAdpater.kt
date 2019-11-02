package com.project.pradyotprakash.polking.comment.adapter

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.Coil
import coil.api.load
import coil.request.Request
import coil.transform.BlurTransformation
import coil.transform.GrayscaleTransformation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.project.pradyotprakash.polking.R
import com.project.pradyotprakash.polking.comment.CommentsAcrivity
import com.project.pradyotprakash.polking.utility.CommentModel
import com.project.pradyotprakash.polking.utility.Utility
import com.skydoves.whatif.whatIfNotNull
import de.hdodenhof.circleimageview.CircleImageView
import java.text.SimpleDateFormat

class MainCommentsAdpater(
    private val allCommentList: ArrayList<CommentModel>,
    private val context: Context,
    private val activity: Activity
) : RecyclerView.Adapter<MainCommentsAdpater.ViewHolder>() {

    private lateinit var questionId: String
    private var mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private var userFirestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var getInnerComments: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val allInnerCommentList: ArrayList<CommentModel> = ArrayList()
    private var innerCommentAdapter: InnerCommentAdapter? = null

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val view =
            LayoutInflater.from(p0.context).inflate(R.layout.main_comment_layout, p0, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return allCommentList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, pos: Int) {
        getUserData(holder, pos)

        holder.commentTv.text = allCommentList[pos].comment
        holder.like_tv.text = allCommentList[pos].likes
        holder.dislike_tv.text = allCommentList[pos].dislikes

        if (allCommentList[pos].innerComment == "0" || allCommentList[pos].innerComment == "") {
            holder.view_replies_tv.visibility = View.GONE
        } else {
            holder.view_replies_tv.visibility = View.VISIBLE
            if (allCommentList[pos].innerComment == "1") {
                holder.view_replies_tv.text = "View ${allCommentList[pos].innerComment} Reply"
            } else {
                holder.view_replies_tv.text = "View ${allCommentList[pos].innerComment} Replies"
            }
        }

        innerCommentAdapter = InnerCommentAdapter(allInnerCommentList, context, activity)
        holder.innerComment_rv.setHasFixedSize(true)
        holder.innerComment_rv.layoutManager = LinearLayoutManager(
            context,
            RecyclerView.VERTICAL, false
        )
        holder.innerComment_rv.adapter = innerCommentAdapter

        holder.view_replies_tv.setOnClickListener {
            Utility().expandCollapse(holder.innerCl)
            mAuth.currentUser.whatIfNotNull(
                whatIf = {
                    if (context is CommentsAcrivity) {
                        context.showLoading()
                    }
                    questionId.whatIfNotNull {
                        getInnerComments
                            .collection("question")
                            .document(questionId)
                            .collection("comments")
                            .document(allCommentList[pos].docId)
                            .collection("innerComment")
                            .orderBy("commentedOn", Query.Direction.DESCENDING)
                            .addSnapshotListener { snapshot, exception ->
                                exception.whatIfNotNull {
                                    if (context is CommentsAcrivity) {
                                        context.showMessage(
                                            "Something Went Wrong. ${exception!!.localizedMessage}",
                                            1
                                        )
                                    }
                                }

                                allInnerCommentList.clear()

                                try {
                                    for (doc in snapshot!!) {
                                        val docId = doc.id
                                        val commentList: CommentModel =
                                            doc.toObject<CommentModel>(CommentModel::class.java)
                                                .withId(docId)
                                        this.allInnerCommentList.add(commentList)
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                    if (context is CommentsAcrivity) {
                                        context.showMessage(
                                            "Something Went Wrong. ${e.localizedMessage}", 1
                                        )
                                    }
                                }

                                if (allInnerCommentList.size > 0) {
                                    innerCommentAdapter?.setQuestionId(questionId)
                                    innerCommentAdapter?.notifyDataSetChanged()
                                }

                                if (context is CommentsAcrivity) {
                                    context.hideLoading()
                                }

                            }
                    }
                },
                whatIfNot = {

                }
            )
        }

        holder.commentVal_rt.setOnEditorActionListener { v, actionId, event ->
            return@setOnEditorActionListener when (actionId) {
                EditorInfo.IME_ACTION_SEND -> {
                    if (holder.commentVal_rt.text.toString().isNotEmpty()) {
                        if (context is CommentsAcrivity) {
                            context.addCommentInner(
                                holder.commentVal_rt.text.toString(),
                                allCommentList[pos].docId
                            )
                            Handler().postDelayed({
                                holder.commentVal_rt.setText("")
                                Utility().hideSoftKeyboard(holder.commentVal_rt)
                            }, 500)
                        }
                    }
                    true
                }
                else -> false
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
    }

    fun setQuestionId(questionId: String) {
        this.questionId = questionId
    }

    inner class ViewHolder(mView: View) : RecyclerView.ViewHolder(mView) {
        val userData_tv: TextView = mView.findViewById(R.id.userData_tv)
        val commentTv: TextView = mView.findViewById(R.id.commentTv)
        val like_tv: TextView = mView.findViewById(R.id.like_tv)
        val dislike_tv: TextView = mView.findViewById(R.id.dislike_tv)
        val view_replies_tv: TextView = mView.findViewById(R.id.view_replies_tv)
        val commentOption_iv: ImageView = mView.findViewById(R.id.commentOption_iv)
        val user_iv: CircleImageView = mView.findViewById(R.id.user_iv)
        val innerComment_rv: RecyclerView = mView.findViewById(R.id.innerComment_rv)
        val commentVal_rt: EditText = mView.findViewById(R.id.commentVal_rt)
        val innerCl: ConstraintLayout = mView.findViewById(R.id.innerCl)
    }

}
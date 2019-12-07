package com.project.pradyotprakash.polking.comment.adapter

import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.HorizontalScrollView
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.Coil
import coil.api.load
import coil.request.Request
import coil.transform.BlurTransformation
import coil.transform.GrayscaleTransformation
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage
import com.google.firebase.ml.naturallanguage.smartreply.FirebaseTextMessage
import com.google.firebase.ml.naturallanguage.smartreply.SmartReplySuggestionResult
import com.project.pradyotprakash.polking.R
import com.project.pradyotprakash.polking.comment.CommentsAcrivity
import com.project.pradyotprakash.polking.utility.CommentModel
import com.project.pradyotprakash.polking.utility.Utility
import com.project.pradyotprakash.polking.utility.diffUtilCallbacks.MainCommentCallback
import com.skydoves.whatif.whatIfNotNull
import de.hdodenhof.circleimageview.CircleImageView
import java.text.SimpleDateFormat

class MainCommentsAdpater(
    private val context: Context,
    private val activity: Activity
) : RecyclerView.Adapter<MainCommentsAdpater.ViewHolder>() {

    private val allCommentList: ArrayList<CommentModel> = ArrayList()
    private lateinit var questionId: String
    private var mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private var userFirestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var getInnerComments: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var addVoteForComment: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var getVotesFirestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val allInnerCommentList: ArrayList<CommentModel> = ArrayList()
    private var innerCommentAdapter: InnerCommentAdapter? = null
    private val commonReply = ArrayList<String>()

    fun updateListItems(comments: java.util.ArrayList<CommentModel>) {
        val diffCallback = MainCommentCallback(this.allCommentList, comments)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        this.allCommentList.clear()
        this.allCommentList.addAll(comments)
        diffResult.dispatchUpdatesTo(this)
    }

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

        setInnerRvAdapter(holder, pos)

        getVotes(holder, pos)

        getInnerCommentsFun(holder, pos)

        setListnerorInnerCommnet(holder, pos)

        setOnClickListner(holder, pos)

        addSmartReply(holder, pos)

        getCommentVotes(holder, pos)

    }

    private fun getCommentVotes(holder: ViewHolder, pos: Int) {
        addVoteForComment
            .collection("question")
            .document(questionId)
            .collection("comments")
            .document(allCommentList[pos].docId)
            .collection("commentVotes")
            .whereEqualTo("voteType", 1)
            .addSnapshotListener { snapshot, exception ->

                exception.whatIfNotNull {
                    if (activity is CommentsAcrivity) {
                        activity.showMessage(
                            "Something Went Wrong. ${exception!!.localizedMessage}", 1
                        )
                    }
                }

                snapshot.whatIfNotNull {
                    try {
                        holder.like_tv.text = "${snapshot!!.size()}"
                    } catch (exception: Exception) {
                        if (activity is CommentsAcrivity) {
                            activity.showMessage(
                                "Something Went Wrong. ${exception.localizedMessage}", 1
                            )
                        }
                    }
                }
            }

        getInnerComments
            .collection("question")
            .document(questionId)
            .collection("comments")
            .document(allCommentList[pos].docId)
            .collection("commentVotes")
            .whereEqualTo("voteType", 0)
            .addSnapshotListener { snapshot, exception ->

                exception.whatIfNotNull {
                    if (activity is CommentsAcrivity) {
                        activity.showMessage(
                            "Something Went Wrong. ${exception!!.localizedMessage}", 1
                        )
                    }
                }

                snapshot.whatIfNotNull {
                    try {
                        holder.dislike_tv.text = "${snapshot!!.size()}"
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

    private fun setOnClickListner(holder: ViewHolder, pos: Int) {
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

        holder.like_tv.setOnClickListener {
            if (context is CommentsAcrivity) {
                context.addReviewForComment(
                    1, allCommentList[pos].docId,
                    holder.like_tv.text.toString()
                )
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                holder.like_tv.compoundDrawables[0].setTint(
                    context
                        .resources.getColor(R.color.light_green)
                )
                holder.dislike_tv.compoundDrawables[0].setTint(
                    context
                        .resources.getColor(R.color.gray)
                )
            }
        }

        holder.dislike_tv.setOnClickListener {
            if (context is CommentsAcrivity) {
                context.addReviewForComment(
                    0, allCommentList[pos].docId,
                    holder.dislike_tv.text.toString()
                )
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                holder.like_tv.compoundDrawables[0].setTint(
                    context
                        .resources.getColor(R.color.gray)
                )
                holder.dislike_tv.compoundDrawables[0].setTint(
                    context
                        .resources.getColor(R.color.disagree_color)
                )
            }
        }
    }

    private fun setListnerorInnerCommnet(holder: ViewHolder, pos: Int) {
        holder.commentVal_rt.setOnEditorActionListener { v, actionId, event ->
            return@setOnEditorActionListener when (actionId) {
                EditorInfo.IME_ACTION_SEND -> {
                    if (holder.commentVal_rt.text.toString().isNotEmpty()) {
                        holder.innerComment_rv.smoothScrollToPosition(0)
                        if (context is CommentsAcrivity) {
                            context.addCommentInner(
                                holder.commentVal_rt.text.toString(),
                                allCommentList[pos].docId,
                                allInnerCommentList.size
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
    }

    private fun setInnerRvAdapter(holder: ViewHolder, pos: Int) {
        innerCommentAdapter = InnerCommentAdapter(context, activity)
        holder.innerComment_rv.setHasFixedSize(true)
        holder.innerComment_rv.layoutManager = LinearLayoutManager(
            context,
            RecyclerView.HORIZONTAL, false
        )
        holder.innerComment_rv.adapter = innerCommentAdapter
    }

    private fun setNumReplies(holder: ViewHolder, pos: Int) {
        if (allInnerCommentList.size == 0) {
            holder.view_replies_tv.text = context.getString(R.string.no_replies_yet)
        } else {
            if (allInnerCommentList.size == 1) {
                holder.view_replies_tv.text = "${allInnerCommentList.size} Reply"
            } else {
                holder.view_replies_tv.text = "${allInnerCommentList.size} Replies"
            }
        }
    }

    private fun getInnerCommentsFun(holder: ViewHolder, pos: Int) {
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

                            this.allInnerCommentList.clear()

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
                                innerCommentAdapter?.updateListItems(this.allInnerCommentList)
                            }

                            if (context is CommentsAcrivity) {
                                context.hideLoading()
                            }

                            setNumReplies(holder, pos)

                        }
                }
            },
            whatIfNot = {

            }
        )
    }

    private fun addSmartReply(holder: ViewHolder, pos: Int) {
        commonReply.clear()
        commonReply.add("Yes")
        commonReply.add("No")
        commonReply.add(context.getString(R.string.smiley))
        commonReply.add(context.getString(R.string.thumbs_up))
        commonReply.add(context.getString(R.string.thumbs_down))
        val conversation = ArrayList<FirebaseTextMessage>()
        conversation.add(
            FirebaseTextMessage.createForRemoteUser(
                allCommentList[pos].comment, System.currentTimeMillis(), allCommentList[pos].docId
            )
        )
        val smartReply = FirebaseNaturalLanguage.getInstance().smartReply
        holder.smartReply_group.removeAllViews()
        smartReply.suggestReplies(conversation)
            .addOnSuccessListener { result ->
                if (result.status == SmartReplySuggestionResult.STATUS_NOT_SUPPORTED_LANGUAGE) {
                    enterHardcodeReply(holder, pos)
                } else if (result.status == SmartReplySuggestionResult.STATUS_SUCCESS) {
                    if (result.suggestions.size > 0) {
                        for (suggestion in result.suggestions) {
                            val replyText = suggestion.text
                            val chip = Chip(holder.smartReply_group.context)
                            chip.setChipBackgroundColorResource(R.color.colorPrimaryDark)
                            chip.setChipStrokeColorResource(R.color.white)
                            chip.chipStrokeWidth = 1f
                            chip.isClickable = true
                            chip.isCheckable = true
                            chip.text = replyText
                            holder.smartReply_group.addView(chip)
                        }
                    } else {
                        enterHardcodeReply(holder, pos)
                    }
                } else if (result.status == SmartReplySuggestionResult.STATUS_NO_REPLY) {
                    enterHardcodeReply(holder, pos)
                }
            }
            .addOnFailureListener {
                enterHardcodeReply(holder, pos)
            }

        holder.smartReply_group.setOnCheckedChangeListener { chipGroup, i ->
            val chip = chipGroup.findViewById<Chip>(i)
            chip.whatIfNotNull {
                if (it.text != "")
                    if (context is CommentsAcrivity) {
                        context.addCommentInner(
                            it.text.toString(),
                            allCommentList[pos].docId,
                            allInnerCommentList.size
                        )
                    }
            }
        }
    }

    private fun enterHardcodeReply(holder: ViewHolder, pos: Int) {
        for (common in commonReply) {
            val chip = Chip(holder.smartReply_group.context)
            chip.setChipBackgroundColorResource(R.color.colorPrimaryDark)
            chip.setChipStrokeColorResource(R.color.white)
            chip.chipStrokeWidth = 1f
            chip.isClickable = true
            chip.isCheckable = true
            chip.text = common
            holder.smartReply_group.addView(chip)
        }
    }

    private fun getVotes(holder: ViewHolder, pos: Int) {
        holder.like_tv.isClickable = false
        holder.like_tv.isEnabled = false
        holder.dislike_tv.isClickable = false
        holder.dislike_tv.isEnabled = false
        getVotesFirestore
            .collection("users")
            .document(mAuth.currentUser!!.uid)
            .collection("commentVotes")
            .document(questionId)
            .collection(allCommentList[pos].docId)
            .document(allCommentList[pos].docId)
            .get()
            .addOnCanceledListener {
                if (activity is CommentsAcrivity) {
                    activity.showMessage(
                        "Something Went Wrong. The request was cancelled.", 1
                    )
                }
            }
            .addOnFailureListener { exception ->
                if (activity is CommentsAcrivity) {
                    activity.showMessage(
                        "Something Went Wrong. ${exception.localizedMessage}", 1
                    )
                }
            }
            .addOnSuccessListener { result ->
                if (result.exists()) {
                    val voteType = result.get("voteType")
                    if (voteType == 1L) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            holder.like_tv.compoundDrawables[0].setTint(
                                context
                                    .resources.getColor(R.color.light_green)
                            )
                            holder.dislike_tv.compoundDrawables[0].setTint(
                                context
                                    .resources.getColor(R.color.gray)
                            )
                        }
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            holder.like_tv.compoundDrawables[0].setTint(
                                context
                                    .resources.getColor(R.color.gray)
                            )
                            holder.dislike_tv.compoundDrawables[0].setTint(
                                context
                                    .resources.getColor(R.color.disagree_color)
                            )
                        }
                    }
                    holder.like_tv.isClickable = false
                    holder.like_tv.isEnabled = false
                    holder.dislike_tv.isClickable = false
                    holder.dislike_tv.isEnabled = false
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        holder.like_tv.compoundDrawables[0].setTint(
                            context
                                .resources.getColor(R.color.gray)
                        )
                        holder.dislike_tv.compoundDrawables[0].setTint(
                            context
                                .resources.getColor(R.color.gray)
                        )
                    }
                    holder.like_tv.isClickable = true
                    holder.like_tv.isEnabled = true
                    holder.dislike_tv.isClickable = true
                    holder.dislike_tv.isEnabled = true
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
        val smartReply_group: ChipGroup = mView.findViewById(R.id.smartReply_group)
        val horizontalSv: HorizontalScrollView = mView.findViewById(R.id.horizontalSv)
    }

}
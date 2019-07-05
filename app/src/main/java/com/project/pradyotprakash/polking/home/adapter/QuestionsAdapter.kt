package com.project.pradyotprakash.polking.home.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.project.pradyotprakash.polking.R
import com.project.pradyotprakash.polking.home.MainActivity
import com.project.pradyotprakash.polking.utility.QuestionModel
import de.hdodenhof.circleimageview.CircleImageView
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class QuestionsAdapter(
    private val allQues: List<QuestionModel>,
    private val context: Context,
    private val activity: Activity
) : RecyclerView.Adapter<QuestionsAdapter.ViewAdapter>() {

    private lateinit var votesHashMap: HashMap<String, String>
    private var mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private var firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var dataBase: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var giveVoteDb: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var getVoteDb: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var addVotesDataBase: FirebaseFirestore = FirebaseFirestore.getInstance()
    @SuppressLint("SimpleDateFormat")
    var dateFormat: SimpleDateFormat = SimpleDateFormat("yyyy/MM/dd")
    @SuppressLint("SimpleDateFormat")
    var timeFormat: SimpleDateFormat = SimpleDateFormat("HH:mm:ss")

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewAdapter {
        val view = LayoutInflater.from(p0.context).inflate(R.layout.question_layout, p0, false)
        return ViewAdapter(view)
    }

    override fun getItemCount(): Int {
        return allQues.size
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onBindViewHolder(holder: ViewAdapter, pos: Int) {

        firestore.collection("users").document(allQues[pos].askedBy).addSnapshotListener { snapshot, exception ->

            if (exception != null) {
                if (activity is MainActivity) {
                    activity.showMessage(
                        "Something Went Wrong. ${exception.localizedMessage}", 1
                    )
                }
            }

            if (snapshot != null && snapshot.exists()) {

                Glide.with(context).load(snapshot.data!!["imageUrl"].toString())
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(
                            exception: GlideException?,
                            model: Any?,
                            target: Target<Drawable>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            if (activity is MainActivity) {
                                activity.showMessage(
                                    "Something Went Wrong. ${exception!!.localizedMessage}", 1
                                )
                            }
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            return false
                        }
                    }).into(holder.profile_iv)

                holder.username_tv.text = snapshot.data!!["name"].toString()

            }

        }

        holder.question_tv.text = allQues[pos].question
        holder.profile_iv.setOnClickListener {
            if (context is MainActivity) {
                context.openProfileAct()
            }
        }

        if (votesHashMap.containsKey(allQues[pos].docId)) {
            if (votesHashMap[allQues[pos].docId] == "1") {
                holder.yes_tv.text = context.getString(R.string.you_agree) + "with " + allQues[pos].yesVote + " others."
            } else {
                holder.yes_tv.text =
                    context.getString(R.string.you_disagree) + "with " + allQues[pos].noVote + " others."
            }
        }

        val votesStr = allQues[pos].yesVote + " Voted YES and " + allQues[pos].noVote + " Voted NO"
        holder.votes_tv.text = votesStr

        holder.yes_tv.setOnClickListener {
            if (holder.yes_tv.text == context.getString(R.string.yes_question)) {
                if (context is MainActivity) {
                    context.showVotes(allQues[pos].docId)
                }
            } else {
                if (mAuth.currentUser != null) {
                    if (allQues[pos].askedBy == mAuth.currentUser!!.uid) {
                        if (context is MainActivity) {
                            context.showMessage(context.getString(R.string.we_dont_work_that_way), 1)
                        }
                    } else {
                        if (context is MainActivity) {
                            if (mAuth.currentUser != null) {
                                context.showLoading()

                                getVoteDb.collection("question").document(allQues[pos].docId)
                                    .addSnapshotListener { snapshot, exception ->

                                        if (exception != null) {
                                            context.showMessage(
                                                "Something Went Wrong. ${exception.localizedMessage}", 1
                                            )
                                            context.hideLoading()
                                        }

                                        if (snapshot != null && snapshot.exists()) {
                                            var yesVoted = snapshot.data!!["noVote"].toString().toInt()
                                            ++yesVoted

                                            val questionData = HashMap<String, Any>()
                                            questionData["yesVote"] = "$yesVoted"

                                            // Update Yes/No Votes
                                            giveVoteDb.collection("question").document(allQues[pos].docId)
                                                .update(questionData)
                                                .addOnSuccessListener {
                                                    val date = Date()
                                                    val voteData = HashMap<String, Any>()
                                                    // 1 - Yes 0 - No
                                                    voteData["voted"] = "1"
                                                    voteData["votedFor"] = allQues[pos].docId
                                                    voteData["votedOnDate"] = dateFormat.format(date)
                                                    voteData["votedOnTime"] = timeFormat.format(date)
                                                    voteData["votedBy"] = mAuth.currentUser!!.uid

                                                    // Add The Votes To The Collection Question
                                                    dataBase.collection("question").document(allQues[pos].docId)
                                                        .collection("votes")
                                                        .document().set(voteData).addOnSuccessListener {

                                                            // Add Votes To The Current User Logged In
                                                            val userVoteData = HashMap<String, Any>()
                                                            userVoteData["voted"] = "1"
                                                            userVoteData["votedFor"] = allQues[pos].docId
                                                            userVoteData["votedOnDate"] = dateFormat.format(date)
                                                            userVoteData["votedOnTime"] = timeFormat.format(date)
                                                            addVotesDataBase.collection("users")
                                                                .document(mAuth.currentUser!!.uid)
                                                                .collection("votes").add(userVoteData)
                                                                .addOnSuccessListener {
                                                                    // Successful Vote
                                                                    holder.yes_tv.text =
                                                                        context.getString(R.string.you_agree) + "with " + allQues[pos].yesVote + " others."
                                                                    context.hideLoading()
                                                                }.addOnFailureListener { exception ->
                                                                    context.showMessage(
                                                                        "Something Went Wrong. ${exception.localizedMessage}",
                                                                        1
                                                                    )
                                                                    context.hideLoading()
                                                                }.addOnCanceledListener {
                                                                    context.showMessage(
                                                                        context.getString(R.string.inable_to_vote),
                                                                        4
                                                                    )
                                                                    context.hideLoading()
                                                                }
                                                        }.addOnFailureListener { exception ->
                                                            context.showMessage(
                                                                "Something Went Wrong. ${exception.localizedMessage}",
                                                                1
                                                            )
                                                            context.hideLoading()
                                                        }.addOnCanceledListener {
                                                            context.showMessage(
                                                                context.getString(R.string.inable_to_vote),
                                                                4
                                                            )
                                                            context.hideLoading()
                                                        }
                                                }.addOnFailureListener { exception_last ->
                                                    context.showMessage(
                                                        "Something Went Wrong. ${exception_last.localizedMessage}",
                                                        1
                                                    )
                                                    context.hideLoading()
                                                }.addOnCanceledListener {
                                                    context.showMessage(
                                                        context.getString(R.string.not_uploaded_question),
                                                        4
                                                    )
                                                    context.hideLoading()
                                                }
                                        } else {
                                            context.hideLoading()
                                            context.showMessage(
                                                context.getString(R.string.that_embarrassing), 1
                                            )
                                        }

                                    }

                            }
                        }
                    }
                } else {
                    if (context is MainActivity) {
                        context.showMessage(context.getString(R.string.dont_support), 1)
                    }
                }
            }
        }

        holder.no_tv.setOnClickListener {
            if (holder.yes_tv.text == context.getString(R.string.no_question)) {
                if (context is MainActivity) {
                    context.showVotes(allQues[pos].docId)
                }
            } else {
                if (mAuth.currentUser != null) {
                    if (allQues[pos].askedBy == mAuth.currentUser!!.uid) {
                        if (context is MainActivity) {
                            context.showMessage(context.getString(R.string.we_dont_work_that_way), 1)
                        }
                    } else {
                        if (context is MainActivity) {
                            if (mAuth.currentUser != null) {
                                context.showLoading()

                                getVoteDb.collection("question").document(allQues[pos].docId)
                                    .addSnapshotListener { snapshot, exception ->

                                        if (exception != null) {
                                            context.showMessage(
                                                "Something Went Wrong. ${exception.localizedMessage}", 1
                                            )
                                            context.hideLoading()
                                        }

                                        if (snapshot != null && snapshot.exists()) {
                                            var yesVoted = snapshot.data!!["noVote"].toString().toInt()
                                            ++yesVoted

                                            val questionData = HashMap<String, Any>()
                                            questionData["yesVote"] = "$yesVoted"

                                            // Update Yes/No Votes
                                            giveVoteDb.collection("question").document(allQues[pos].docId)
                                                .update(questionData)
                                                .addOnSuccessListener {
                                                    val date = Date()
                                                    val voteData = HashMap<String, Any>()
                                                    // 1 - Yes 0 - No
                                                    voteData["voted"] = "0"
                                                    voteData["votedFor"] = allQues[pos].docId
                                                    voteData["votedOnDate"] = dateFormat.format(date)
                                                    voteData["votedOnTime"] = timeFormat.format(date)
                                                    voteData["votedBy"] = mAuth.currentUser!!.uid

                                                    // Add The Votes To The Collection Question
                                                    dataBase.collection("question").document(allQues[pos].docId)
                                                        .collection("votes")
                                                        .document().set(voteData).addOnSuccessListener {

                                                            // Add Votes To The Current User Logged In
                                                            val userVoteData = HashMap<String, Any>()
                                                            userVoteData["voted"] = "0"
                                                            userVoteData["votedFor"] = allQues[pos].docId
                                                            userVoteData["votedOnDate"] = dateFormat.format(date)
                                                            userVoteData["votedOnTime"] = timeFormat.format(date)
                                                            addVotesDataBase.collection("users")
                                                                .document(mAuth.currentUser!!.uid)
                                                                .collection("votes").add(userVoteData)
                                                                .addOnSuccessListener {
                                                                    // Successful Vote
                                                                    holder.yes_tv.text =
                                                                        context.getString(R.string.you_disagree) + "with " + allQues[pos].noVote + " others."
                                                                    context.hideLoading()
                                                                }.addOnFailureListener { exception ->
                                                                    context.showMessage(
                                                                        "Something Went Wrong. ${exception.localizedMessage}",
                                                                        1
                                                                    )
                                                                    context.hideLoading()
                                                                }.addOnCanceledListener {
                                                                    context.showMessage(
                                                                        context.getString(R.string.inable_to_vote),
                                                                        4
                                                                    )
                                                                    context.hideLoading()
                                                                }
                                                        }.addOnFailureListener { exception ->
                                                            context.showMessage(
                                                                "Something Went Wrong. ${exception.localizedMessage}",
                                                                1
                                                            )
                                                            context.hideLoading()
                                                        }.addOnCanceledListener {
                                                            context.showMessage(
                                                                context.getString(R.string.inable_to_vote),
                                                                4
                                                            )
                                                            context.hideLoading()
                                                        }
                                                }.addOnFailureListener { exception_last ->
                                                    context.showMessage(
                                                        "Something Went Wrong. ${exception_last.localizedMessage}",
                                                        1
                                                    )
                                                    context.hideLoading()
                                                }.addOnCanceledListener {
                                                    context.showMessage(
                                                        context.getString(R.string.not_uploaded_question),
                                                        4
                                                    )
                                                    context.hideLoading()
                                                }
                                        } else {
                                            context.hideLoading()
                                            context.showMessage(
                                                context.getString(R.string.that_embarrassing), 1
                                            )
                                        }

                                    }

                            }
                        }
                    }
                } else {
                    if (context is MainActivity) {
                        context.showMessage(context.getString(R.string.dont_support), 1)
                    }
                }
            }
        }

        dataBase.collection("question").document(allQues[pos].docId).collection("votes")

    }

    fun setVotesByUser(votesHashMap: java.util.HashMap<String, String>) {
        this.votesHashMap = votesHashMap
    }

    inner class ViewAdapter(context: View) : RecyclerView.ViewHolder(context) {
        val profile_iv: CircleImageView = context.findViewById(R.id.bg_iv)
        val username_tv: TextView = context.findViewById(R.id.username_tv)
        val question_tv: TextView = context.findViewById(R.id.question_tv)
        val votes_tv: TextView = context.findViewById(R.id.votes_tv)
        val yes_tv: TextView = context.findViewById(R.id.yes_tv)
        val no_tv: TextView = context.findViewById(R.id.no_tv)
    }

}
package com.project.pradyotprakash.polking.home.adapter

import android.app.Activity
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.chip.Chip
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.project.pradyotprakash.polking.R
import com.project.pradyotprakash.polking.home.MainActivity
import com.project.pradyotprakash.polking.profile.ProfileActivity
import com.project.pradyotprakash.polking.utility.QuestionModel
import de.hdodenhof.circleimageview.CircleImageView

class QuestionsAdapter(
    private val allQues: List<QuestionModel>,
    private val context: Context,
    private val activity: Activity
) : RecyclerView.Adapter<QuestionsAdapter.ViewAdapter>() {

    private var mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private var userFirestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var getVotesFirestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewAdapter {
        val view = LayoutInflater.from(p0.context).inflate(R.layout.question_layout, p0, false)
        return ViewAdapter(view)
    }

    override fun getItemCount(): Int {
        return allQues.size
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onBindViewHolder(holder: ViewAdapter, pos: Int) {

        getUserData(holder, pos)

        holder.question_tv.text = allQues[pos].question

        if (mAuth.currentUser != null) {
            if (mAuth.currentUser!!.uid == allQues[pos].askedBy) {
                holder.seeStsts_tv.text = context.getString(R.string.see_stats)
                showStats(holder, pos)
            } else {
                checkIfVoteExists(holder, pos)
            }
        } else {
            holder.seeStsts_tv.text = context.getString(R.string.please_login)
            showStats(holder, pos)
        }

        holder.profile_iv.setOnClickListener {
            if (context is MainActivity) {
                if (mAuth.currentUser != null) {
                    if (mAuth.currentUser!!.uid != allQues[pos].askedBy) {
                        context.openProfileDetails(allQues[pos].askedBy)
                    } else {
                        context.startProfileAct()
                    }
                } else {
                    context.startLogin()
                }
            }
        }

        holder.username_tv.setOnClickListener {
            if (context is MainActivity) {
                if (mAuth.currentUser != null) {
                    if (mAuth.currentUser!!.uid != allQues[pos].askedBy) {
                        context.openProfileDetails(allQues[pos].askedBy)
                    } else {
                        context.startProfileAct()
                    }
                } else {
                    context.startLogin()
                }
            }
        }

        holder.yes_tv.setOnClickListener {
            if (context is MainActivity) {
                if (mAuth.currentUser != null) {
                    if (mAuth.currentUser!!.uid != allQues[pos].askedBy) {
                        context.setVotes(1, allQues[pos].docId)
                        holder.seeStsts_tv.setChipBackgroundColorResource(R.color.agree_color)
                        showStats(holder, pos)
                    }
                } else {
                    context.startLogin()
                }
            } else if (context is ProfileActivity) {
                if (mAuth.currentUser != null) {
                    if (mAuth.currentUser!!.uid != allQues[pos].askedBy) {
                        holder.seeStsts_tv.setChipBackgroundColorResource(R.color.disagree_color)
                        context.setVotes(1, allQues[pos].docId)
                        showStats(holder, pos)
                    }
                }
            }
        }

        holder.no_tv.setOnClickListener {
            if (context is MainActivity) {
                if (mAuth.currentUser != null) {
                    if (mAuth.currentUser!!.uid != allQues[pos].askedBy) {
                        holder.seeStsts_tv.setChipBackgroundColorResource(R.color.disagree_color)
                        context.setVotes(2, allQues[pos].docId)
                        showStats(holder, pos)
                    }
                } else {
                    context.startLogin()
                }
            } else if (context is ProfileActivity) {
                if (mAuth.currentUser != null) {
                    if (mAuth.currentUser!!.uid != allQues[pos].askedBy) {
                        holder.seeStsts_tv.setChipBackgroundColorResource(R.color.disagree_color)
                        context.setVotes(2, allQues[pos].docId)
                        showStats(holder, pos)
                    }
                }
            }
        }

        holder.seeStsts_tv.setOnClickListener {
            if (context is MainActivity) {
                if (mAuth.currentUser != null) {
                    context.showStats(allQues[pos].docId)
                } else {
                    context.startLogin()
                }
            } else if (context is ProfileActivity) {
                if (mAuth.currentUser != null) {
                    context.showStats(allQues[pos].docId)
                }
            }
        }

    }

    private fun checkIfVoteExists(holder: ViewAdapter, pos: Int) {
        getVotesFirestore
            .collection("users")
            .document(mAuth.currentUser!!.uid)
            .collection("votes")
            .document(allQues[pos].docId)
            .get()
            .addOnCanceledListener {
                if (activity is MainActivity) {
                    activity.showMessage(
                        "Something Went Wrong. The request was cancelled.", 1
                    )
                }
            }
            .addOnFailureListener { exception ->
                if (activity is MainActivity) {
                    activity.showMessage(
                        "Something Went Wrong. ${exception.localizedMessage}", 1
                    )
                }
            }
            .addOnSuccessListener { result ->
                if (result.exists()) {
                    val voteType = result.get("voteType")
                    if (voteType == 1L) {
                        holder.seeStsts_tv.text = context.getString(R.string.see_stats)
                        holder.seeStsts_tv.setChipBackgroundColorResource(R.color.agree_color)
                    } else {
                        holder.seeStsts_tv.text = context.getString(R.string.see_stats)
                        holder.seeStsts_tv.setChipBackgroundColorResource(R.color.disagree_color)
                    }
                    showStats(holder, pos)
                } else {
                    hideStats(holder, pos)
                }
            }
    }

    private fun hideStats(holder: ViewAdapter, pos: Int) {
        holder.seeStsts_tv.visibility = View.GONE
        holder.yes_tv.visibility = View.VISIBLE
        holder.no_tv.visibility = View.VISIBLE
    }

    private fun showStats(holder: ViewAdapter, pos: Int) {
        holder.seeStsts_tv.visibility = View.VISIBLE
        holder.yes_tv.visibility = View.GONE
        holder.no_tv.visibility = View.GONE
    }

    private fun getUserData(holder: ViewAdapter, pos: Int) {
        userFirestore.collection("users").document(allQues[pos].askedBy)
            .addSnapshotListener { snapshot, exception ->

                if (exception != null) {
                    if (activity is MainActivity) {
                        activity.showMessage(
                            "Something Went Wrong. ${exception.localizedMessage}", 1
                        )
                    }
                }

                if (snapshot != null && snapshot.exists()) {

                    try {
                        Glide.with(context).load(snapshot.data!!["imageUrl"].toString())
                            .placeholder(R.drawable.ic_default_appcolor)
                            .listener(object : RequestListener<Drawable> {
                                override fun onLoadFailed(
                                    exception: GlideException?,
                                    model: Any?,
                                    target: Target<Drawable>?,
                                    isFirstResource: Boolean
                                ): Boolean {
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
                        holder.profile_iv.borderColor =
                            context.resources.getColor(R.color.colorPrimary)
                        holder.profile_iv.borderWidth = 2

                        holder.username_tv.text = snapshot.data!!["name"].toString()
                    } catch (exception: Exception) {
                        if (activity is MainActivity) {
                            activity.showMessage(
                                "Something Went Wrong. ${exception.localizedMessage}", 1
                            )
                        }
                    }

                }

            }
    }

    inner class ViewAdapter(context: View) : RecyclerView.ViewHolder(context) {
        val profile_iv: CircleImageView = context.findViewById(R.id.user_iv)
        val username_tv: Chip = context.findViewById(R.id.username_tv)
        val question_tv: TextView = context.findViewById(R.id.question_tv)
        val yes_tv: Chip = context.findViewById(R.id.yes_tv)
        val no_tv: Chip = context.findViewById(R.id.no_tv)
        val seeStsts_tv: Chip = context.findViewById(R.id.seeStsts_tv)
    }

}
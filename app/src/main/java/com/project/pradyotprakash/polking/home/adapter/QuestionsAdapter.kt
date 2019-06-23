package com.project.pradyotprakash.polking.home.adapter

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

class QuestionsAdapter(
    private val allQues: List<QuestionModel>,
    private val context: Context,
    private val activity: Activity
) : RecyclerView.Adapter<QuestionsAdapter.ViewAdapter>() {

    private var mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private var firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var dataBase: FirebaseFirestore = FirebaseFirestore.getInstance()

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

        val votesStr = allQues[pos].yesVote + " Voted YES and " + allQues[pos].noVote + " Voted NO"
        holder.votes_tv.text = votesStr

        holder.yes_tv.setOnClickListener {
            if (mAuth.currentUser != null) {
                if (allQues[pos].askedBy == mAuth.currentUser!!.uid) {
                    if (context is MainActivity) {
                        context.showMessage(context.getString(R.string.we_dont_work_that_way), 1)
                    }
                } else {
                    if (context is MainActivity) {
                        context.giveTheVote(allQues[pos].docId, 1)
                    }
                }
            } else {
                if (context is MainActivity) {
                    context.showMessage(context.getString(R.string.dont_support), 1)
                }
            }
        }

        holder.no_tv.setOnClickListener {
            if (mAuth.currentUser != null) {
                if (allQues[pos].askedBy == mAuth.currentUser!!.uid) {
                    if (context is MainActivity) {
                        context.showMessage(context.getString(R.string.we_dont_work_that_way), 1)
                    }
                } else {
                    if (context is MainActivity) {
                        context.giveTheVote(allQues[pos].docId, 0)
                    }
                }
            } else {
                if (context is MainActivity) {
                    context.showMessage(context.getString(R.string.dont_support), 1)
                }
            }
        }

        dataBase.collection("question").document(allQues[pos].docId).collection("votes")

    }

    inner class ViewAdapter(mView: View) : RecyclerView.ViewHolder(mView) {
        val profile_iv: CircleImageView = mView.findViewById(R.id.bg_iv)
        val username_tv: TextView = mView.findViewById(R.id.username_tv)
        val question_tv: TextView = mView.findViewById(R.id.question_tv)
        val votes_tv: TextView = mView.findViewById(R.id.votes_tv)
        val yes_tv: TextView = mView.findViewById(R.id.yes_tv)
        val no_tv: TextView = mView.findViewById(R.id.no_tv)
    }

}
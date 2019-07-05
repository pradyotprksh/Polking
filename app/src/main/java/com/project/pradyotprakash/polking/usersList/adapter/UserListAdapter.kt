package com.project.pradyotprakash.polking.usersList.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
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
import com.project.pradyotprakash.polking.utility.QuestionVotesModel
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*

class UserListAdapter(
    private val allVotesList: ArrayList<QuestionVotesModel>,
    private val context: Context,
    private val activity: Activity
) : RecyclerView.Adapter<UserListAdapter.ViewAdapter>() {

    private var mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private var firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewAdapter {
        val view = LayoutInflater.from(p0.context).inflate(R.layout.profile_list_layout, p0, false)
        return ViewAdapter(view)
    }

    override fun getItemCount(): Int {
        return allVotesList.size
    }

    @SuppressLint("NewApi")
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onBindViewHolder(holder: ViewAdapter, pos: Int) {

        firestore.collection("users").document(allVotesList[pos].votedBy).addSnapshotListener { snapshot, exception ->

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

                holder.userName_tv.text = snapshot.data!!["name"].toString()

            }

        }

        if (allVotesList[pos].voted == "1") {
            holder.userName_tv.setTextColor(context.getColor(R.color.dark_green))
        } else {
            holder.userName_tv.setTextColor(context.getColor(R.color.dark_red))
        }

        holder.request_cv.setOnClickListener {
            when {
                holder.request_tv.text == context.getString(R.string.follow) -> {
                    if (mAuth.currentUser != null) {
                        holder.request_cv.setCardBackgroundColor(context.getColor(R.color.dark_gray))
                        holder.follow_progress.visibility = View.VISIBLE

                        holder.request_tv.text = context.getString(R.string.following)
                        holder.request_tv.setTextColor(context.getColor(R.color.colorPrimaryDark))

                        firestore.collection("users").document(mAuth.currentUser!!.uid).collection("friends")
                    } else {
                        if (activity is MainActivity) {
                            activity.showMessage(context.getString(R.string.dont_support), 1)
                        }
                    }
                }
            }
        }

    }

    inner class ViewAdapter(mView: View) : RecyclerView.ViewHolder(mView) {
        val profile_iv = mView.findViewById<CircleImageView>(R.id.profile_iv)
        val userName_tv = mView.findViewById<TextView>(R.id.userName_tv)
        val request_cv = mView.findViewById<CardView>(R.id.request_cv)
        val request_tv = mView.findViewById<TextView>(R.id.request_tv)
        val follow_progress = mView.findViewById<ProgressBar>(R.id.follow_progress)
    }

}
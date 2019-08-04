package com.project.pradyotprakash.polking.usersList.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
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
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class UserListAdapter(
    private val allVotesList: ArrayList<QuestionVotesModel>,
    private val context: Context,
    private val activity: Activity
) : RecyclerView.Adapter<UserListAdapter.ViewAdapter>() {

    private lateinit var allFriendList: HashMap<String, String>
    private var mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private var firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    @SuppressLint("SimpleDateFormat")
    var dateFormat: SimpleDateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")

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

        holder.profile_iv.setOnClickListener {
            if (context is MainActivity) {
                context.openProfileAct()
            }
        }

        holder.userName_tv.setOnClickListener {
            if (context is MainActivity) {
                context.openProfileAct()
            }
        }

        if (allFriendList.containsKey(allVotesList[pos].votedBy)) {
            holder.request_tv.text = context.getString(R.string.following)
            holder.request_tv.setTextColor(context.getColor(R.color.colorPrimaryDark))
            holder.request_cv.setCardBackgroundColor(context.getColor(R.color.white))
            holder.request_cv.isEnabled = false
            holder.request_cv.isClickable = false
        } else {
            holder.request_tv.text = context.getString(R.string.follow)
            holder.request_tv.setTextColor(context.getColor(R.color.white))
            holder.request_cv.setCardBackgroundColor(context.getColor(R.color.colorPrimaryDark))
            holder.request_cv.isEnabled = true
            holder.request_cv.isClickable = true
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
                        val date = Date()
                        holder.request_cv.setCardBackgroundColor(context.getColor(R.color.dark_gray))
                        holder.follow_progress.visibility = View.VISIBLE

                        val friendMap = HashMap<String, Any>()
                        friendMap["userId"] = allVotesList[pos].votedBy
                        friendMap["madeFriendOn"] = dateFormat.format(date)
                        friendMap["madeBestFriendOn"] = ""
                        friendMap["isBestFriend"] = "false"
                        friendMap["isFriend"] = "true"

                        firestore.collection("users").document(mAuth.currentUser!!.uid)
                            .collection("friends")
                            .add(friendMap)
                            .addOnSuccessListener {
                                holder.request_tv.text = context.getString(R.string.following)
                                holder.request_tv.setTextColor(context.getColor(R.color.colorPrimaryDark))
                                holder.request_cv.setCardBackgroundColor(context.getColor(R.color.white))
                                holder.request_cv.isEnabled = false
                                holder.request_cv.isClickable = false
                                holder.follow_progress.visibility = View.GONE
                            }.addOnFailureListener { exception ->
                                if (context is MainActivity) {
                                    holder.request_tv.text = context.getString(R.string.follow)
                                    holder.request_tv.setTextColor(context.getColor(R.color.white))
                                    holder.request_cv.setCardBackgroundColor(context.getColor(R.color.colorPrimaryDark))
                                    holder.request_cv.isEnabled = true
                                    holder.request_cv.isClickable = true
                                    holder.follow_progress.visibility = View.GONE
                                    context.showMessage(
                                        "Something Went Wrong. ${exception.localizedMessage}",
                                        1
                                    )
                                }
                            }.addOnCanceledListener {
                                if (context is MainActivity) {
                                    holder.request_tv.text = context.getString(R.string.follow)
                                    holder.request_tv.setTextColor(context.getColor(R.color.white))
                                    holder.request_cv.setCardBackgroundColor(context.getColor(R.color.colorPrimaryDark))
                                    holder.request_cv.isEnabled = true
                                    holder.request_cv.isClickable = true
                                    holder.follow_progress.visibility = View.GONE
                                    context.showMessage(
                                        context.getString(R.string.inable_to_vote),
                                        4
                                    )
                                }
                            }
                    } else {
                        if (activity is MainActivity) {
                            activity.showMessage(context.getString(R.string.dont_support), 1)
                        }
                    }
                }
            }
        }

    }

    fun setFriendList(allFriendsList: java.util.HashMap<String, String>) {
        this.allFriendList = allFriendsList
    }

    inner class ViewAdapter(mView: View) : RecyclerView.ViewHolder(mView) {
        val profile_iv = mView.findViewById<CircleImageView>(R.id.profile_iv)
        val userName_tv = mView.findViewById<TextView>(R.id.userName_tv)
        val request_cv = mView.findViewById<CardView>(R.id.request_cv)
        val request_tv = mView.findViewById<TextView>(R.id.request_tv)
        val follow_progress = mView.findViewById<ProgressBar>(R.id.follow_progress)
    }

}
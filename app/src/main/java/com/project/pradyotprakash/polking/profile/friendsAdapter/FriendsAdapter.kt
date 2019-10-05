package com.project.pradyotprakash.polking.profile.friendsAdapter

import android.app.Activity
import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
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
import com.project.pradyotprakash.polking.utility.FriendsListModel
import de.hdodenhof.circleimageview.CircleImageView

class FriendsAdapter(
    private val allFriendsList: ArrayList<FriendsListModel>,
    private val context: Context,
    private val activity: Activity
) : RecyclerView.Adapter<FriendsAdapter.ViewHolder>() {

    private var mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private var userFirestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val view =
            LayoutInflater.from(p0.context).inflate(R.layout.friends_layout_adapter, p0, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return allFriendsList.size
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {

        userFirestore.collection("users").document(allFriendsList[p1].userId)
            .addSnapshotListener { snapshot, exception ->

                if (exception != null) {
                    if (activity is MainActivity) {
                        activity.showMessage(
                            "Something Went Wrong. ${exception.localizedMessage}", 1
                        )
                    }
                }

                if (snapshot != null && snapshot.exists()) {

                    p0.name_tv.text = snapshot.data!!["name"].toString()

                    Glide.with(context).load(snapshot.data!!["imageUrl"].toString())
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
                                p0.progressBar.visibility = View.GONE
                                return false
                            }
                        }).into(p0.bgImage)

                }

            }

        p0.name_tv.setOnClickListener {
            when (context) {
                is MainActivity -> if (mAuth.currentUser != null) {
                    if (mAuth.currentUser!!.uid != allFriendsList[p1].userId) {
                        context.openProfileDetails(allFriendsList[p1].userId)
                    }
                } else {
                    context.startLogin()
                }
                is ProfileActivity -> if (mAuth.currentUser != null) {
                    if (mAuth.currentUser!!.uid != allFriendsList[p1].userId) {
                        context.openProfileDetails(allFriendsList[p1].userId)
                    }
                }
            }
        }

        p0.itemView.setOnClickListener {
            when (context) {
                is MainActivity -> if (mAuth.currentUser != null) {
                    if (mAuth.currentUser!!.uid != allFriendsList[p1].userId) {
                        context.openProfileDetails(allFriendsList[p1].userId)
                    }
                } else {
                    context.startLogin()
                }
                is ProfileActivity -> if (mAuth.currentUser != null) {
                    if (mAuth.currentUser!!.uid != allFriendsList[p1].userId) {
                        context.openProfileDetails(allFriendsList[p1].userId)
                    }
                }
            }
        }

    }

    inner class ViewHolder(mView: View) : RecyclerView.ViewHolder(mView) {
        val bgImage: CircleImageView = mView.findViewById(R.id.user_iv)
        val progressBar: ProgressBar = mView.findViewById(R.id.progressBar)
        val name_tv: Chip = mView.findViewById(R.id.name_tv)
    }

}
package com.project.pradyotprakash.polking.profile.questionStats

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
import com.project.pradyotprakash.polking.utility.VotesModel
import de.hdodenhof.circleimageview.CircleImageView

class VotesAdapter(
    private val allVotesModel: ArrayList<VotesModel>,
    private val context: Context,
    private val activity: Activity
) : RecyclerView.Adapter<VotesAdapter.ViewHolder>() {

    private var mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private var userFirestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val view =
            LayoutInflater.from(p0.context).inflate(R.layout.friends_layout_adapter, p0, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return allVotesModel.size
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {

        userFirestore.collection("users").document(allVotesModel[p1].votedBy)
            .addSnapshotListener { snapshot, exception ->

                if (exception != null) {
                    if (activity is MainActivity) {
                        activity.showMessage(
                            "Something Went Wrong. ${exception.localizedMessage}", 1
                        )
                    }
                }

                if (snapshot != null && snapshot.exists()) {

                    p0.nameTv.text = snapshot.data!!["name"].toString()

                    Glide.with(context).load(snapshot.data!!["imageUrl"].toString())
                        .placeholder(R.drawable.ic_default_appcolor)
                        .listener(object : RequestListener<Drawable> {
                            override fun onLoadFailed(
                                exception: GlideException?,
                                model: Any?,
                                target: Target<Drawable>?,
                                isFirstResource: Boolean
                            ): Boolean {
                                p0.progressBar.visibility = View.GONE
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
                    p0.bgImage.borderColor = context.resources.getColor(R.color.colorPrimary)
                    p0.bgImage.borderWidth = 2

                }

            }

        p0.nameTv.setOnClickListener {
            openProfileOptions(p0, p1)
        }
        p0.itemView.setOnClickListener {
            openProfileOptions(p0, p1)
        }

    }

    private fun openProfileOptions(p0: ViewHolder, p1: Int) {
        when (context) {
            is MainActivity -> if (mAuth.currentUser != null) {
                if (mAuth.currentUser!!.uid != allVotesModel[p1].votedBy) {
                    context.openProfileDetails(allVotesModel[p1].votedBy)
                } else {
                    context.startProfileAct()
                }
            } else {
                context.startLogin()
            }
            is ProfileActivity -> if (mAuth.currentUser != null) {
                if (mAuth.currentUser!!.uid != allVotesModel[p1].votedBy) {
                    context.openProfileDetails(allVotesModel[p1].votedBy)
                }
            }
        }
    }

    inner class ViewHolder(mView: View) : RecyclerView.ViewHolder(mView) {
        val bgImage: CircleImageView = mView.findViewById(R.id.user_iv)
        val nameTv: Chip = mView.findViewById(R.id.name_tv)
        val progressBar: ProgressBar = mView.findViewById(R.id.progressBar)
    }

}
package com.project.pradyotprakash.polking.home.adapter

import android.annotation.SuppressLint
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
import com.project.pradyotprakash.polking.utility.QuestionModel
import de.hdodenhof.circleimageview.CircleImageView
import java.text.SimpleDateFormat

class QuestionsAdapter(
    private val allQues: List<QuestionModel>,
    private val context: Context,
    private val activity: Activity
) : RecyclerView.Adapter<QuestionsAdapter.ViewAdapter>() {

    private var mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private var userFirestore: FirebaseFirestore = FirebaseFirestore.getInstance()
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

        userFirestore.collection("users").document(allQues[pos].askedBy).addSnapshotListener { snapshot, exception ->

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
                } catch (exception: Exception) {
                    if (activity is MainActivity) {
                        activity.showMessage(
                            "Something Went Wrong. ${exception.localizedMessage}", 1
                        )
                    }
                }

            }

        }

        holder.question_tv.text = allQues[pos].question

        holder.profile_iv.setOnClickListener {
            if (context is MainActivity) {
                if (mAuth.currentUser!!.uid != allQues[pos].askedBy) {
                    context.openProfileDetails(allQues[pos].askedBy)
                } else {
                    context.startProfileAct()
                }
            }
        }

        holder.username_tv.setOnClickListener {
            if (context is MainActivity) {
                if (mAuth.currentUser!!.uid != allQues[pos].askedBy) {
                    context.openProfileDetails(allQues[pos].askedBy)
                } else {
                    context.startProfileAct()
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
    }

}
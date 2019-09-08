package com.project.pradyotprakash.polking.profile.notification

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
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
import com.project.pradyotprakash.polking.utility.NotificationModel
import de.hdodenhof.circleimageview.CircleImageView

class NotificationsAdapter(
    private val allNotificationsList: ArrayList<NotificationModel>,
    private val context: Context,
    private val activity: Activity
) : RecyclerView.Adapter<NotificationsAdapter.ViewHolder>() {

    private var userFirestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var notificationFirestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var mAuth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val view =
            LayoutInflater.from(p0.context).inflate(R.layout.notification_layout_adapter, p0, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return allNotificationsList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {

        p0.notification_message.text = allNotificationsList[p1].notificationMessage

        userFirestore.collection("users").document(allNotificationsList[p1].notificationMessageBy)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    (context as MainActivity).showMessage(
                        "Something Went Wrong. ${exception.localizedMessage}", 1
                    )
                }

                if (snapshot != null && snapshot.exists()) {
                    p0.name_chio.text = snapshot.data!!["name"].toString()

                    Glide.with(context).load(snapshot.data!!["imageUrl"].toString())
                        .listener(object :
                            RequestListener<Drawable> {
                            override fun onLoadFailed(
                                exception: GlideException?,
                                model: Any?,
                                target: Target<Drawable>?,
                                isFirstResource: Boolean
                            ): Boolean {
                                (context as ProfileActivity).showMessage(
                                    "Something Went Wrong. ${exception?.localizedMessage}",
                                    1
                                )
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
                        }).into(p0.user_iv)
                }

            }

        if (allNotificationsList[p1].notificationIsRead == "false") {
            val notificationData = HashMap<String, Any>()
            notificationData["notificationIsRead"] = "true"
            notificationFirestore.collection("users").document(mAuth.currentUser!!.uid)
                .collection("notifications").document(allNotificationsList[p1].docId)
                .update(notificationData).addOnFailureListener { exception ->
                    (context as MainActivity).showMessage(
                        "Something Went Wrong. ${exception.localizedMessage}", 1
                    )
                }
        }

    }

    inner class ViewHolder(mView: View) : RecyclerView.ViewHolder(mView) {
        val name_chio: Chip = mView.findViewById(R.id.name_chio)
        val user_iv: CircleImageView = mView.findViewById(R.id.user_iv)
        val notification_message: TextView = mView.findViewById(R.id.notification_message)
    }

}

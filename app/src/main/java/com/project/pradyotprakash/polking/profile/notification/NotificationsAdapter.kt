package com.project.pradyotprakash.polking.profile.notification

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.project.pradyotprakash.polking.R
import com.project.pradyotprakash.polking.home.MainActivity
import com.project.pradyotprakash.polking.utility.NotificationModel

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

        if (allNotificationsList[p1].notificationIsRead == "false") {
            p0.notification_chip.setTextColor(context.resources.getColor(R.color.white))
            p0.notification_chip.setChipBackgroundColorResource(R.color.colorPrimary)
        } else {
            p0.notification_chip.setTextColor(context.resources.getColor(R.color.black))
            p0.notification_chip.setChipBackgroundColorResource(R.color.colorAccent)
        }

        userFirestore.collection("users").document(allNotificationsList[p1].notificationMessageBy)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    (context as MainActivity).showMessage(
                        "Something Went Wrong. ${exception.localizedMessage}", 1
                    )
                }

                if (snapshot != null && snapshot.exists()) {
                    p0.notification_chip.text = snapshot.data!!["name"].toString() + " " +
                            allNotificationsList[p1].notificationMessage
                }
            }

        p0.notification_chip.setOnClickListener {
            if (allNotificationsList[p1].notificationIsRead == "false") {
                val notificationData = HashMap<String, Any>()
                notificationData["notificationIsRead"] = "true"
                notificationFirestore.collection("users").document(mAuth.currentUser!!.uid)
                    .collection("notifications").document(allNotificationsList[p1].docId)
                    .update(notificationData).addOnSuccessListener {
                        p0.notification_chip.setChipBackgroundColorResource(R.color.colorAccent)
                        p0.notification_chip.setTextColor(context.resources.getColor(R.color.black))
                    }.addOnFailureListener { exception ->
                        (context as MainActivity).showMessage(
                            "Something Went Wrong. ${exception.localizedMessage}", 1
                        )
                    }
            }
        }
    }

    inner class ViewHolder(mView: View) : RecyclerView.ViewHolder(mView) {
        val notification_chip: Chip = mView.findViewById(R.id.notification_chip)
    }

}

package com.project.pradyotprakash.polking.profile.notification

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.project.pradyotprakash.polking.R
import com.project.pradyotprakash.polking.utility.NotificationModel

class NotificationsAdapter(
    private val allNotificationsList: ArrayList<NotificationModel>,
    private val context: Context,
    private val activity: Activity
) : RecyclerView.Adapter<NotificationsAdapter.ViewHolder>() {

    private var userFirestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val view =
            LayoutInflater.from(p0.context).inflate(R.layout.notification_layout_adapter, p0, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return allNotificationsList.size
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        p0.notification_tv.text = allNotificationsList[p1].notificationMessage
    }

    inner class ViewHolder(mView: View) : RecyclerView.ViewHolder(mView) {
        val notification_tv: TextView = mView.findViewById(R.id.notification_tv)
    }

}

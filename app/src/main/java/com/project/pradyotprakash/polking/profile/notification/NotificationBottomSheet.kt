package com.project.pradyotprakash.polking.profile.notification

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.project.pradyotprakash.polking.R
import com.project.pradyotprakash.polking.message.ShowMessage
import com.project.pradyotprakash.polking.profileDetails.ProfileEditView
import com.project.pradyotprakash.polking.utility.NotificationModel
import com.project.pradyotprakash.polking.utility.RoundBottomSheet
import com.project.pradyotprakash.polking.utility.logd
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.notification_btm_sheet.view.*
import java.util.*
import javax.inject.Inject

class NotificationBottomSheet @Inject constructor() : RoundBottomSheet(), ProfileEditView {

    private lateinit var notificationFirestore: FirebaseFirestore
    private lateinit var mAuth: FirebaseAuth
    lateinit var messageBtmSheet: ShowMessage
    private val allNotification = ArrayList<NotificationModel>()
    private val allNotificationsList = ArrayList<NotificationModel>()
    private var notificationsAdapter: NotificationsAdapter? = null

    companion object {
        fun newInstance(): NotificationBottomSheet =
            NotificationBottomSheet().apply {

            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        AndroidInjection.inject(this.activity)
        val view = inflater.inflate(R.layout.notification_btm_sheet, container, false)

        activity!!.logd(getString(R.string.notificationbottomsheet))

        initView(view)

        return view
    }

    private fun initView(view: View) {
        mAuth = FirebaseAuth.getInstance()
        notificationFirestore = FirebaseFirestore.getInstance()

        view.back_tv.setOnClickListener {
            stopAct()
        }

        allNotificationsList.clear()
        notificationsAdapter = NotificationsAdapter(allNotificationsList, context!!, activity!!)
        view.notification_rv.setHasFixedSize(true)
        view.notification_rv.layoutManager =
            LinearLayoutManager(context!!, RecyclerView.VERTICAL, false)
        view.notification_rv.adapter = notificationsAdapter

        if (mAuth.currentUser != null) {
            view.progressBar6.visibility = View.VISIBLE

            notificationFirestore.collection("users").document(mAuth.currentUser!!.uid)
                .collection("notifications")
                .orderBy("notificationOn", Query.Direction.DESCENDING)
                .addSnapshotListener { snapshot, exception ->
                    if (exception != null) {
                        showMessage(
                            "Something Went Wrong. ${exception.localizedMessage}", 1
                        )
                        view.progressBar6.visibility = View.GONE
                    }

                    allNotification.clear()

                    try {
                        for (doc in snapshot!!.documentChanges) {
                            if (doc.type == DocumentChange.Type.ADDED || doc.type == DocumentChange.Type.REMOVED) {
                                val docId = doc.document.id
                                val notificationList: NotificationModel =
                                    doc.document.toObject(NotificationModel::class.java)
                                        .withId(docId)
                                allNotification.add(notificationList)
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        showMessage(e.localizedMessage, 1)
                    }

                    if (allNotification.size > 0) {
                        allNotificationsList.addAll(allNotification)
                        notificationsAdapter!!.notifyDataSetChanged()
                    }

                    view.progressBar6.visibility = View.GONE
                }
        } else {
            view.progressBar6.visibility = View.GONE
            showMessage(getString(R.string.user_not_found), 1)
            dismiss()
        }

    }

    override fun showLoading() {

    }

    override fun hideLoading() {

    }

    override fun stopAct() {
        dismiss()
    }

    override fun showMessage(message: String, type: Int) {
        messageBtmSheet = ShowMessage.newInstance()
        if (!messageBtmSheet.isAdded) {
            messageBtmSheet.show(childFragmentManager, "btmSheet")
            messageBtmSheet.setMessage(message, type)
        } else {
            messageBtmSheet.dismiss()
            Handler().postDelayed({
                if (!messageBtmSheet.isAdded) {
                    messageBtmSheet.show(childFragmentManager, "btmSheet")
                    messageBtmSheet.setMessage(message, type)
                }
            }, 1500)
        }
    }
}
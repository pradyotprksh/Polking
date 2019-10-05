package com.project.pradyotprakash.polking.profile.friends

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.project.pradyotprakash.polking.R
import com.project.pradyotprakash.polking.profile.friendsAdapter.FriendsAdapter
import com.project.pradyotprakash.polking.profileDetails.ProfileEditView
import com.project.pradyotprakash.polking.utility.FriendsListModel
import com.project.pradyotprakash.polking.utility.TransparentBottomSheet
import com.project.pradyotprakash.polking.utility.logd
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.friends_btm_sheet.view.*
import java.util.*
import javax.inject.Inject

class FriendsBottomSheet @Inject constructor() : TransparentBottomSheet(), ProfileEditView {

    private var type: Int = 1
    private lateinit var mAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private val allFriends = ArrayList<FriendsListModel>()
    private val allFriendsList = ArrayList<FriendsListModel>()
    private var friendsAdapter: FriendsAdapter? = null

    companion object {
        fun newInstance(): FriendsBottomSheet =
            FriendsBottomSheet().apply {

            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        AndroidInjection.inject(this.activity)
        val view = inflater.inflate(R.layout.friends_btm_sheet, container, false)

        activity!!.logd(getString(R.string.profilebottomsheet))

        initView(view)

        return view
    }

    private fun initView(view: View) {
        mAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        allFriendsList.clear()
        friendsAdapter = FriendsAdapter(allFriendsList, context!!, activity!!)
        view.friends_rv.setHasFixedSize(true)
        view.friends_rv.layoutManager =
            LinearLayoutManager(context!!, RecyclerView.HORIZONTAL, false)
        view.friends_rv.adapter = friendsAdapter

        view.back_tv.setOnClickListener {
            dismiss()
        }

        if (mAuth.currentUser != null) {
            showLoading()
            val from: String = if (type == 1) {
                "friends"
            } else {
                "bestfriends"
            }

            firestore.collection("users").document(mAuth.currentUser!!.uid)
                .collection(from).addSnapshotListener { snapshot, exception ->
                    if (exception != null) {
                        showMessage(
                            "Something Went Wrong. ${exception.localizedMessage}", 1
                        )
                    }

                    allFriends.clear()

                    try {
                        for (doc in snapshot!!.documentChanges) {
                            if (doc.type == DocumentChange.Type.ADDED || doc.type == DocumentChange.Type.REMOVED) {
                                val docId = doc.document.id
                                val friendList: FriendsListModel =
                                    doc.document.toObject(FriendsListModel::class.java)
                                        .withId(docId)
                                allFriends.add(friendList)
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        showMessage(e.localizedMessage, 1)
                    }

                    if (allFriends.size > 0) {
                        allFriendsList.clear()
                        allFriendsList.addAll(allFriends)
                        friendsAdapter!!.notifyDataSetChanged()
                    }
                }

        } else {
            showMessage(getString(R.string.user_not_found), 1)
        }
    }

    override fun showLoading() {

    }

    override fun hideLoading() {

    }

    override fun stopAct() {

    }

    override fun showMessage(message: String, type: Int) {

    }

    fun setType(type: Int) {
        this.type = type
    }
}
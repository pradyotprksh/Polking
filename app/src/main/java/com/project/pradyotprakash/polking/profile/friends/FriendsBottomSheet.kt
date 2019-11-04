package com.project.pradyotprakash.polking.profile.friends

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.project.pradyotprakash.polking.R
import com.project.pradyotprakash.polking.message.ShowMessage
import com.project.pradyotprakash.polking.profile.friendsAdapter.FriendsAdapter
import com.project.pradyotprakash.polking.profileDetails.ProfileEditView
import com.project.pradyotprakash.polking.utility.FriendsListModel
import com.project.pradyotprakash.polking.utility.TransparentBottomSheet
import com.project.pradyotprakash.polking.utility.logd
import com.skydoves.whatif.whatIfNotNull
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.friends_btm_sheet.view.*
import java.util.*
import javax.inject.Inject

class FriendsBottomSheet @Inject constructor() : TransparentBottomSheet(), ProfileEditView {

    private var type: Int = 1
    private lateinit var mAuth: FirebaseAuth
    lateinit var messageBtmSheet: ShowMessage
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
        initvariables()

        adapterForFriends(view)

        onClickListner(view)

        getLists(view)
    }

    private fun getLists(view: View) {
        mAuth.currentUser.whatIfNotNull(
            whatIf = {
                showLoading()
                val from: String = if (type == 1) {
                    "friends"
                } else {
                    "bestfriends"
                }

                firestore.collection("users").document(mAuth.currentUser!!.uid)
                    .collection(from).addSnapshotListener { snapshot, exception ->
                        exception.whatIfNotNull {
                            showMessage(
                                "Something Went Wrong. ${exception!!.localizedMessage}", 1
                            )
                        }

                        allFriends.clear()

                        try {
                            for (doc in snapshot!!.documentChanges) {
                                val docId = doc.document.id
                                val friendList: FriendsListModel =
                                    doc.document.toObject(FriendsListModel::class.java)
                                        .withId(docId)
                                allFriends.add(friendList)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            showMessage(e.localizedMessage, 1)
                        }

                        if (allFriends.size > 0) {
                            allFriendsList.clear()
                            allFriendsList.addAll(allFriends)
                            friendsAdapter?.updateListItems(allFriendsList)
                        }
                    }
            },
            whatIfNot = {
                showMessage(getString(R.string.user_not_found), 1)
            }
        )
    }

    private fun onClickListner(view: View) {
        view.back_tv.setOnClickListener {
            dismiss()
        }
    }

    private fun adapterForFriends(view: View) {
        allFriendsList.clear()
        friendsAdapter = FriendsAdapter(context!!, activity!!)
        view.friends_rv.setHasFixedSize(true)
        view.friends_rv.layoutManager =
            LinearLayoutManager(context!!, RecyclerView.HORIZONTAL, false)
        view.friends_rv.adapter = friendsAdapter
    }

    private fun initvariables() {
        mAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
    }

    override fun showLoading() {

    }

    override fun hideLoading() {

    }

    override fun stopAct() {

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

    fun setType(type: Int) {
        this.type = type
    }
}
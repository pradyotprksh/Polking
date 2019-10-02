package com.project.pradyotprakash.polking.usersList

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.project.pradyotprakash.polking.R
import com.project.pradyotprakash.polking.profileDetails.ProfileEditView
import com.project.pradyotprakash.polking.usersList.adapter.UserListAdapter
import com.project.pradyotprakash.polking.utility.FriendsListModel
import com.project.pradyotprakash.polking.utility.QuestionVotesModel
import com.project.pradyotprakash.polking.utility.RoundBottomSheet
import com.project.pradyotprakash.polking.utility.logd
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.users_list_btm_sheet.view.*
import java.util.*
import javax.inject.Inject
import kotlin.collections.HashMap

class UserListBtmSheet @Inject constructor() : RoundBottomSheet(), ProfileEditView {

    private lateinit var mActivity: Activity
    private lateinit var mContext: Context
    private lateinit var mAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private var docId: String = ""
    private val allVotesList = ArrayList<QuestionVotesModel>()
    private val allFriendsList = HashMap<String, String>()
    private var userListAdapter: UserListAdapter? = null

    companion object {
        fun newInstance(): UserListBtmSheet =
            UserListBtmSheet().apply {

            }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        AndroidInjection.inject(this.activity)
        val view = inflater.inflate(R.layout.users_list_btm_sheet, container, false)

        dialog!!.setOnShowListener { dialog ->
            val bottomSheetDialog: BottomSheetDialog = dialog as BottomSheetDialog
            val bottomSheetInternal = bottomSheetDialog.findViewById<FrameLayout>(R.id.design_bottom_sheet)
            if (bottomSheetInternal != null) {
                BottomSheetBehavior.from<View>(bottomSheetInternal).state = BottomSheetBehavior.STATE_EXPANDED
            }
        }

        activity!!.logd(getString(R.string.profilebottomsheet))

        initView(view)

        return view
    }

    private fun initView(view: View) {
        if (docId != "" && docId.isNotEmpty()) {

            userListAdapter = UserListAdapter(allVotesList, mContext, mActivity)
            view.userList_rv.setHasFixedSize(true)
            view.userList_rv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            view.userList_rv.adapter = userListAdapter

            firestore.collection("question").document(docId)
                .addSnapshotListener { snapshot, exception ->
                    if (exception != null) {
                        showMessage(
                            "Something Went Wrong. ${exception.localizedMessage}", 1
                        )
                        stopAct()
                    }

                    if (snapshot != null && snapshot.exists()) {
                        view.questionTv.text = snapshot.data!!["question"].toString()
                    } else {
                        stopAct()
                    }
                }

            firestore.collection("question").document(docId)
                .collection("votes").addSnapshotListener { snapshot, exception ->
                    if (exception != null) {
                        showMessage(
                            "Something Went Wrong. ${exception.localizedMessage}", 1
                        )
                        stopAct()
                    }

                    this.allVotesList.clear()

                    try {
                        for (doc in snapshot!!.documentChanges) {
                            showLoading()
                            if (doc.type == DocumentChange.Type.ADDED ||
                                doc.type == DocumentChange.Type.MODIFIED ||
                                doc.type == DocumentChange.Type.REMOVED
                            ) {
                                val docId = doc.document.id
                                val votesList: QuestionVotesModel =
                                    doc.document.toObject(QuestionVotesModel::class.java).withId(docId)
                                this.allVotesList.add(votesList)
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        showMessage(e.localizedMessage, 1)
                        stopAct()
                    }

                    userListAdapter!!.notifyDataSetChanged()

                    hideLoading()

                }

            firestore.collection("users").document(mAuth.currentUser!!.uid)
                .collection("friends").addSnapshotListener { snapshot, exception ->
                    if (exception != null) {
                        showMessage(
                            "Something Went Wrong. ${exception.localizedMessage}", 1
                        )
                        stopAct()
                    }

                    this.allFriendsList.clear()

                    try {
                        for (doc in snapshot!!.documentChanges) {
                            showLoading()
                            if (doc.type == DocumentChange.Type.ADDED ||
                                doc.type == DocumentChange.Type.MODIFIED ||
                                doc.type == DocumentChange.Type.REMOVED
                            ) {
                                val docId = doc.document.id
                                val friendList: FriendsListModel =
                                    doc.document.toObject(FriendsListModel::class.java).withId(docId)
                                allFriendsList[friendList.userId] = friendList.userId
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        showMessage(e.localizedMessage, 1)
                        stopAct()
                    }

                    userListAdapter!!.setFriendList(allFriendsList)
                    userListAdapter!!.notifyDataSetChanged()

                    hideLoading()

                }

        } else {
            stopAct()
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

    }

    fun setQuestionDocId(docId: String) {
        this.docId = docId
    }

    fun setContext(context: Context) {
        this.mContext = context
    }

    fun setActivity(activity: Activity) {
        this.mActivity = activity
    }
}
package com.project.pradyotprakash.polking.profile

import android.app.Activity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.project.pradyotprakash.polking.R
import com.project.pradyotprakash.polking.utility.BgModel
import java.util.*
import javax.inject.Inject

class ProfileActivityPresenterImpl @Inject constructor() : ProfileActivityPresenter {

    lateinit var mContext: Activity
    @Inject
    lateinit var mView: ProfileActivityView
    private lateinit var mAuth: FirebaseAuth
    private var currentUser: FirebaseUser? = null
    private lateinit var dataBase: FirebaseFirestore
    private val allBgList = ArrayList<BgModel>()

    @Inject
    internal fun ProfileActivityPresenterImpl(activity: Activity) {
        mContext = activity
        mAuth = FirebaseAuth.getInstance()
        currentUser = mAuth.currentUser
        dataBase = FirebaseFirestore.getInstance()
    }

    override fun getUserData() {
        mView.showLoading()
        if (currentUser != null) {
            dataBase.collection("users").document(currentUser!!.uid).get().addOnSuccessListener { result ->
                if (result.exists()) {
                    mView.setUserProfileImage(result.getString("imageUrl"))
                    mView.setUserName(result.getString("name"))
                    mView.hideLoading()
                } else {
                    mView.openAddProfileDetails()
                    mView.hideLoading()
                }
            }.addOnFailureListener {
                mView.showMessage(mContext.getString(R.string.something_went_wring_oops), 1)
                mView.hideLoading()
            }.addOnCanceledListener {
                mView.showMessage(mContext.getString(R.string.getting_details), 4)
                mView.hideLoading()
            }
        }
    }

    override fun getBackgroundImages() {
        if (currentUser != null) {
            mView.showLoading()
            dataBase.collection("background_images").addSnapshotListener { documentSnapshot, e ->
                for (doc in documentSnapshot!!.documentChanges) {
                    if (doc.type == DocumentChange.Type.ADDED || doc.type == DocumentChange.Type.MODIFIED) {
                        val docId = doc.document.id
                        val bgList: BgModel = doc.document.toObject<BgModel>(BgModel::class.java).withId(docId)
                        if (bgList.imageSelected) {
                            mView.setBgImage(bgList.imageUrl)
                        }
                        this.allBgList.add(bgList)
                    }
                }
                mView.setBgList(allBgList)
                mView.hideLoading()
            }
        } else {
            mView.showMessage(mContext.getString(R.string.user_not_found), 1)
        }
    }

    override fun start() {

    }

    override fun stop() {

    }

    override fun isNetworkAvailable(): Boolean {
        return true
    }
}
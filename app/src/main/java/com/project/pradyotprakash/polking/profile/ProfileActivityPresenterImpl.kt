package com.project.pradyotprakash.polking.profile

import android.app.Activity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
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
    private lateinit var bgDataBase: FirebaseFirestore
    private val allBgList = ArrayList<BgModel>()

    @Inject
    internal fun ProfileActivityPresenterImpl(activity: Activity) {
        mContext = activity
        mAuth = FirebaseAuth.getInstance()
        currentUser = mAuth.currentUser
        dataBase = FirebaseFirestore.getInstance()
        bgDataBase = FirebaseFirestore.getInstance()
    }

    override fun getUserData() {
        mView.showLoading()
        if (currentUser != null) {
            setListnerForUserData()
            getTheUserData()
        } else {
            mView.showMessage(mContext.getString(R.string.user_not_found), 1)
        }
    }

    private fun getTheUserData() {
        dataBase.collection("users").document(currentUser!!.uid).get()
            .addOnSuccessListener { result ->
                if (result.exists()) {

                    bgDataBase.collection("background_images")
                        .document(result.getString("bg_option")!!).get()
                        .addOnSuccessListener { resultBg ->
                            mView.showLoading()
                            if (result.exists()) {
                                mView.setBgImage(resultBg.getString("imageUrl")!!, resultBg.id)
                                mView.hideLoading()
                            } else {
                                mView.showMessage(mContext.getString(R.string.not_found_bg), 1)
                                mView.hideLoading()
                            }
                        }.addOnFailureListener { exception ->
                            mView.showMessage(
                                "Something Went Wrong. ${exception.localizedMessage}",
                                1
                            )
                            mView.hideLoading()
                        }.addOnCanceledListener {
                            mView.showMessage(mContext.getString(R.string.loading_image_cancel), 4)
                            mView.hideLoading()
                        }

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

    private fun setListnerForUserData() {
        dataBase.collection("users").document(currentUser!!.uid)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    mView.showMessage(
                        "Something Went Wrong. ${exception.localizedMessage}", 1
                    )
                }

                if (snapshot != null && snapshot.exists()) {
                    mView.setUserDetails(
                        snapshot.data!!["questions"].toString(),
                        snapshot.data!!["friends"].toString(),
                        snapshot.data!!["best_friends"].toString()
                    )
                    mView.setUserProfileImage(snapshot.data!!["imageUrl"].toString())
                    mView.setUserName(snapshot.data!!["name"].toString())
                    mView.hideLoading()
                } else {
                    mView.openAddProfileDetails()
                    mView.hideLoading()
                }
            }
    }

    override fun getBackgroundImages() {
        if (currentUser != null) {
            mView.showLoading()
            dataBase.collection("background_images").addSnapshotListener { documentSnapshot, e ->

                if (e != null) {
                    mView.showMessage("Something went wrong. ${e.localizedMessage}", 1)
                    return@addSnapshotListener
                }

                this.allBgList.clear()

                for (doc in documentSnapshot!!) {
                    val docId = doc.id
                    val bgList: BgModel = doc.toObject<BgModel>(BgModel::class.java).withId(docId)
                    this.allBgList.add(bgList)
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

    override fun changeBgId(bgDocId: String) {
        mView.showLoading()
        val userData = HashMap<String, Any>()
        userData["bg_option"] = bgDocId
        dataBase.collection("users")
            .document(mAuth.currentUser!!.uid)
            .update(userData).addOnSuccessListener {
                mView.hideLoading()
                mView.stopAct()
            }.addOnFailureListener { exception ->
                mView.hideLoading()
                mView.showMessage(
                    "Something Went Wrong. ${exception.localizedMessage}",
                    1
                )
            }.addOnCanceledListener {
                mView.hideLoading()
                mView.showMessage(mContext.getString(R.string.not_uploaded), 4)
            }
    }

}
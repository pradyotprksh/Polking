package com.project.pradyotprakash.polking.otherProfileOptions

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.firebase.firestore.FirebaseFirestore
import com.project.pradyotprakash.polking.R
import com.project.pradyotprakash.polking.profileDetails.ProfileEditView
import com.project.pradyotprakash.polking.utility.TransparentBottomSheet
import com.project.pradyotprakash.polking.utility.logd
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.other_profile_options_btm_sheet.*
import kotlinx.android.synthetic.main.other_profile_options_btm_sheet.view.*
import javax.inject.Inject

class OtherProfileOptions @Inject constructor() : TransparentBottomSheet(), ProfileEditView {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var askedBy: String

    companion object {
        fun newInstance(): OtherProfileOptions =
            OtherProfileOptions().apply {

            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        AndroidInjection.inject(this.activity)
        val view = inflater.inflate(R.layout.other_profile_options_btm_sheet, container, false)

        activity!!.logd(getString(R.string.otherprofiledetailsbottomsheet))

        initView(view)

        return view
    }

    private fun initView(view: View) {

        firestore = FirebaseFirestore.getInstance()

        getUserData(view)

        view.back_tv.setOnClickListener {
            dismiss()
        }

    }

    private fun getUserData(view: View) {
        view.progressBar5.visibility = View.VISIBLE
        if (askedBy.isEmpty()) {
            view.progressBar5.visibility = View.GONE
            dismiss()
        } else {
            firestore.collection("users").document(askedBy)
                .addSnapshotListener { snapshot, exception ->
                    if (exception != null) {
                        showMessage(
                            "Something Went Wrong. ${exception.localizedMessage}", 1
                        )
                    }

                    if (snapshot != null && snapshot.exists()) {
                        if (snapshot.data!!["questions"].toString().isNotEmpty()) {
                            view.questionVal_tv.text = snapshot.data!!["questions"].toString()
                        }
                        if (snapshot.data!!["friends"].toString().isNotEmpty()) {
                            view.friendsVal_tv.text = snapshot.data!!["friends"].toString()
                        }
                        if (snapshot.data!!["best_friends"].toString().isNotEmpty()) {
                            view.bestFrndVal_tv.text = snapshot.data!!["best_friends"].toString()
                        }
                        view.userNameTv.text = snapshot.data!!["name"].toString()

                        Glide.with(this).load(snapshot.data!!["imageUrl"].toString())
                            .listener(object :
                                RequestListener<Drawable> {
                                override fun onLoadFailed(
                                    exception: GlideException?,
                                    model: Any?,
                                    target: Target<Drawable>?,
                                    isFirstResource: Boolean
                                ): Boolean {
                                    view.progressBar5.visibility = View.GONE
                                    showMessage(
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
                                    view.progressBar5.visibility = View.GONE
                                    return false
                                }
                            }).into(user_iv)

                        view.progressBar5.visibility = View.GONE
                    } else {
                        hideLoading()
                        view.progressBar5.visibility = View.GONE
                    }
                }
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

    fun setUserId(askedBy: String) {
        this.askedBy = askedBy
    }
}
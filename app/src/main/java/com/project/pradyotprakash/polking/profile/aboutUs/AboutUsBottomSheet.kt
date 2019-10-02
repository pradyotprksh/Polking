package com.project.pradyotprakash.polking.profile.aboutUs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.project.pradyotprakash.polking.R
import com.project.pradyotprakash.polking.profileDetails.ProfileEditView
import com.project.pradyotprakash.polking.utility.RoundBottomSheet
import com.project.pradyotprakash.polking.utility.logd
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.question_details_btm_sheet.view.*
import javax.inject.Inject

class AboutUsBottomSheet @Inject constructor() : RoundBottomSheet(), ProfileEditView {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    companion object {
        fun newInstance(): AboutUsBottomSheet =
            AboutUsBottomSheet().apply {

            }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        AndroidInjection.inject(this.activity)
        val view = inflater.inflate(R.layout.about_us_btm_sheet, container, false)

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
        mAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        view.back_tv.setOnClickListener {
            stopAct()
        }

        if (mAuth.currentUser != null) {
            showLoading()

            firestore.collection("about_us").document("about_us")
                .addSnapshotListener { snapshot, exception ->
                    if (exception != null) {
                        showMessage(
                            "Something Went Wrong. ${exception.localizedMessage}", 1
                        )
                    }

                    if (snapshot != null && snapshot.exists()) {
                        view.aboutUs_tv.text = snapshot.data!!["about_us"].toString()
                    } else {
                        view.aboutUs_tv.text = getString(R.string.about_us_txt)
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
        dismiss()
    }

    override fun showMessage(message: String, type: Int) {

    }
}
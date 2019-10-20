package com.project.pradyotprakash.polking.profile.aboutUs

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.project.pradyotprakash.polking.R
import com.project.pradyotprakash.polking.message.ShowMessage
import com.project.pradyotprakash.polking.profileDetails.ProfileEditView
import com.project.pradyotprakash.polking.utility.TransparentBottomSheet
import com.project.pradyotprakash.polking.utility.logd
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.about_us_btm_sheet.view.*
import kotlinx.android.synthetic.main.question_details_btm_sheet.view.aboutUs_tv
import kotlinx.android.synthetic.main.question_details_btm_sheet.view.back_tv
import javax.inject.Inject

class AboutUsBottomSheet @Inject constructor() : TransparentBottomSheet(), ProfileEditView {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    lateinit var messageBtmSheet: ShowMessage

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
        initVariables()

        setOnClickListners(view)

        getAboutData(view)

        getVersionNumber(view)
    }

    private fun getVersionNumber(view: View) {
        try {
            val pInfo = context!!.packageManager.getPackageInfo(context!!.packageName, 0)
            val version = pInfo.versionName
            view.version_tv.text = "v$version"
        } catch (e: PackageManager.NameNotFoundException) {
            view.version_tv.text = "v1.0.0"
            e.printStackTrace()
        }
    }

    private fun getAboutData(view: View) {
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
                        view.aboutUs_tv.visibility = View.VISIBLE
                        view.aboutUs_tv.text = snapshot.data!!["about_us"].toString()
                    } else {
                        view.aboutUs_tv.text = getString(R.string.about_us_txt)
                        view.aboutUs_tv.visibility = View.INVISIBLE
                    }
                }

        } else {
            showMessage(getString(R.string.user_not_found), 1)
        }
    }

    private fun setOnClickListners(view: View) {
        view.back_tv.setOnClickListener {
            stopAct()
        }

        view.share_tv.setOnClickListener {
            shareApplication()
        }
    }

    private fun shareApplication() {
        val playStoreLink =
            "Share Polking Between Your Friends \n LINK: " + getString(R.string.link_play_store)
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_TEXT, playStoreLink)
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Share Polking Between Your Friends")
        startActivity(Intent.createChooser(shareIntent, "Share Polking..."))
    }

    private fun initVariables() {
        mAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
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
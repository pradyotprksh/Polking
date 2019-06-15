package com.project.pradyotprakash.polking.faq.questionDetails

import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.BottomSheetDialog
import android.support.design.widget.BottomSheetDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.project.pradyotprakash.polking.R
import com.project.pradyotprakash.polking.profileDetails.ProfileEditView
import com.project.pradyotprakash.polking.utility.logd
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.question_details_btm_sheet.*
import kotlinx.android.synthetic.main.question_details_btm_sheet.view.*
import javax.inject.Inject

class QuestionDetailsBtmSheet @Inject constructor() : BottomSheetDialogFragment(), ProfileEditView {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private var docId: String? = null

    companion object {
        fun newInstance(): QuestionDetailsBtmSheet =
            QuestionDetailsBtmSheet().apply {

            }
    }

    fun docId(docId: String) {
        this.docId = docId
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        AndroidInjection.inject(this.activity)
        val view = inflater.inflate(R.layout.question_details_btm_sheet, container, false)

        dialog.setOnShowListener { dialog ->
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

        if (docId != null) {
            if (mAuth.currentUser != null) {
                showLoading()
                firestore.collection("faqs").document(docId!!).get().addOnSuccessListener { result ->
                    if (result.exists()) {
                        when {
                            result.getString("type") == "queRes" -> {
                                view.type_tv.text = getString(R.string.how_to)
                            }
                            result.getString("type") == "friendBestFriend" -> {
                                view.type_tv.text = getString(R.string.friends_txt)
                            }
                            result.getString("type") == "blockReport" -> {
                                view.type_tv.text = getString(R.string.block_report)
                            }
                        }
                        if (result.getString("isTopQuestion") == "true") {
                            view.lottieAnimationView.visibility = View.VISIBLE
                        } else {
                            view.lottieAnimationView.visibility = View.GONE
                        }
                        view.question_tv.text = result.getString("question")!!
                        view.answer_tv.text = result.getString("answer")!!
                        hideLoading()
                    } else {
                        showMessage(getString(R.string.something_went_wring_oops), 1)
                        hideLoading()
                    }
                }.addOnFailureListener { exception ->
                    showMessage(
                        "Something Went Wrong. ${exception.localizedMessage}",
                        1
                    )
                    hideLoading()
                }.addOnCanceledListener {
                    showMessage(getString(R.string.loading_image_cancel), 4)
                    hideLoading()
                }
            } else {
                showMessage(getString(R.string.user_not_found), 1)
            }
        }
    }

    override fun showLoading() {
        if (mainProgressBar != null) {
            mainProgressBar.visibility = View.VISIBLE
        }
    }

    override fun hideLoading() {
        if (mainProgressBar != null) {
            mainProgressBar.visibility = View.GONE
        }
    }

    override fun stopAct() {
        dismiss()
    }

    override fun showMessage(message: String, type: Int) {

    }

}
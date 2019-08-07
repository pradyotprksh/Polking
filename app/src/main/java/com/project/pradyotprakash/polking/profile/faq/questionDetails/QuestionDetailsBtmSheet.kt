package com.project.pradyotprakash.polking.profile.faq.questionDetails

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.project.pradyotprakash.polking.R
import com.project.pradyotprakash.polking.profileDetails.ProfileEditView
import com.project.pradyotprakash.polking.utility.Utility
import com.project.pradyotprakash.polking.utility.logd
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.question_details_btm_sheet.*
import kotlinx.android.synthetic.main.question_details_btm_sheet.view.*
import java.util.*
import javax.inject.Inject

class QuestionDetailsBtmSheet @Inject constructor() : BottomSheetDialogFragment(), ProfileEditView {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private var docId: String? = null
    private var helpFullYes: Float = 0.0f
    private var helpFullNo: Float = 0.0f
    private var yesPercent: Double = 0.00
    private var noPercent: Double = 0.00

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

        activity!!.logd(getString(R.string.questionbottomsheet))

        initView(view)
        return view
    }

    @SuppressLint("SetTextI18n")
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

                        view.question_tv.text = result.getString("question")!!
                        view.aboutUs_tv.text = result.getString("answer")!!

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

                firestore.collection("faqs").document(docId!!)
                    .addSnapshotListener { snapshot, exception ->
                        if (exception != null) {
                            showMessage(
                                "Something Went Wrong. ${exception.localizedMessage}", 1
                            )
                        }

                        if (snapshot != null && snapshot.exists()) {

                            if (snapshot.getString("isTopQuestion") == "true") {
                                view.lottieAnimationView.visibility = View.VISIBLE
                                view.lottieAnimationView.startAnimation(Utility().inFromDownAnimation())
                            } else {
                                view.lottieAnimationView.startAnimation(Utility().outToDownAnimation())
                                view.lottieAnimationView.visibility = View.GONE
                            }

                            helpFullYes = snapshot.getString("helpFullYes")!!.toFloat()
                            helpFullNo = snapshot.getString("helpFullNo")!!.toFloat()

                            yesPercent = snapshot.getString("yesPercent")!!.toDouble()
                            noPercent = snapshot.getString("noPercent")!!.toDouble()

                            view.helpful_tv.text =
                                """${activity!!.getString(R.string.was_it_helpful)}  ${Utility().roundOffDecimal(
                                    yesPercent
                                )}% Agreed & $noPercent% Disagreed"""

                        } else {
                            view.helpful_tv.text =
                                """${activity!!.getString(R.string.was_it_helpful)}  0.0% Agreed & 0.0% Disagreed"""
                        }
                    }
            } else {
                showMessage(activity!!.getString(R.string.user_not_found), 1)
            }
        }

        view.yes_cl.setOnClickListener {
            if (view.no_cl.visibility == View.VISIBLE) {
                view.no_cl.startAnimation(Utility().outToRightAnimation())
                view.no_cl.visibility = View.GONE
                view.iv_happy.visibility = View.VISIBLE

                val questionData = HashMap<String, Any>()
                questionData["helpFullYes"] = (++helpFullYes).toString()
                questionData["helpFullNo"] = helpFullNo.toString()

                firestore.collection("faqs").document(docId!!).update(questionData).addOnFailureListener { exception ->
                    showMessage(
                        "Something Went Wrong. ${exception.localizedMessage}",
                        1
                    )
                }.addOnCanceledListener {
                    showMessage(getString(R.string.not_uploaded_question), 4)
                }
            }
        }

        view.no_cl.setOnClickListener {
            if (view.yes_cl.visibility == View.VISIBLE) {
                view.yes_cl.startAnimation(Utility().outToLeftAnimation())
                view.yes_cl.visibility = View.GONE
                view.iv_sad.visibility = View.VISIBLE

                val questionData = HashMap<String, Any>()
                questionData["helpFullYes"] = helpFullYes.toString()
                questionData["helpFullNo"] = (++helpFullNo).toString()

                firestore.collection("faqs").document(docId!!).update(questionData).addOnFailureListener { exception ->
                    showMessage(
                        "Something Went Wrong. ${exception.localizedMessage}",
                        1
                    )
                }.addOnCanceledListener {
                    showMessage(getString(R.string.not_uploaded_question), 4)
                }
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
package com.project.pradyotprakash.polking.faq.askQuestion

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.BottomSheetDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.project.pradyotprakash.polking.R
import com.project.pradyotprakash.polking.profileDetails.ProfileEditView
import com.project.pradyotprakash.polking.utility.RoundBottomSheet
import com.project.pradyotprakash.polking.utility.logd
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.add_faq_question_btm_sheet.*
import kotlinx.android.synthetic.main.add_faq_question_btm_sheet.view.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class AskFaqQuestionBtmSheet @Inject constructor() : RoundBottomSheet(), ProfileEditView {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private var type: String = "queRes"
    @SuppressLint("SimpleDateFormat")
    var dateFormat: DateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")

    companion object {
        fun newInstance(): AskFaqQuestionBtmSheet =
            AskFaqQuestionBtmSheet().apply {

            }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        AndroidInjection.inject(this.activity)
        val view = inflater.inflate(R.layout.add_faq_question_btm_sheet, container, false)

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
        view.optionOne_Cb.isChecked = true

        view.optionOne_Cb.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                view.optionOne_Cb.isChecked = true
                view.optionTwo_Cb.isChecked = false
                view.optionThree_Cb.isChecked = false
                type = "queRes"
            }
        }

        view.optionTwo_Cb.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                view.optionOne_Cb.isChecked = false
                view.optionTwo_Cb.isChecked = true
                view.optionThree_Cb.isChecked = false
                type = "friendBestFriend"
            }
        }

        view.optionThree_Cb.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                view.optionOne_Cb.isChecked = false
                view.optionTwo_Cb.isChecked = false
                view.optionThree_Cb.isChecked = true
                type = "blockReport"
            }
        }

        view.saveTv.setOnClickListener {
            if (mAuth.currentUser != null) {
                if (view.optionOne_Cb.isChecked || view.optionTwo_Cb.isChecked || view.optionThree_Cb.isChecked) {
                    if (!view.addQuestion_et.text.toString().isBlank()) {
                        showLoading()
                        val date = Date()
                        val questionData = HashMap<String, Any>()
                        questionData["question"] = view.addQuestion_et.text.toString()
                        questionData["answer"] = view.addQuestion_et3.text.toString()
                        questionData["askedBy"] = mAuth.currentUser!!.uid
                        questionData["askedOn"] = dateFormat.format(date)
                        questionData["openedBy"] = "0"
                        questionData["helpFullYes"] = "0.0"
                        questionData["helpFullNo"] = "0.0"
                        questionData["isTopQuestion"] = "false"
                        questionData["type"] = type

                        firestore.collection("faqs").add(questionData).addOnSuccessListener {
                            hideLoading()
                            view.addQuestion_et.setText("")
                            view.addQuestion_et3.setText("")
                        }.addOnFailureListener { exception ->
                            showMessage(
                                "Something Went Wrong. ${exception.localizedMessage}",
                                1
                            )
                            hideLoading()
                        }.addOnCanceledListener {
                            showMessage(getString(R.string.not_uploaded_question), 4)
                            hideLoading()
                        }

                    } else {
                        showMessage(getString(R.string.ask_doubt), 1)
                    }
                } else {
                    showMessage(getString(R.string.select_category), 1)
                }
            } else {
                showMessage(getString(R.string.user_not_found), 1)
            }
        }
    }

    override fun showLoading() {
        mainProgressBar.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        mainProgressBar.visibility = View.GONE
    }

    override fun stopAct() {
        dismiss()
    }

    override fun showMessage(message: String, type: Int) {

    }

}
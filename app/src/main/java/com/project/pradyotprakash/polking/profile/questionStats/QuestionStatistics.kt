package com.project.pradyotprakash.polking.profile.questionStats

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.project.pradyotprakash.polking.R
import com.project.pradyotprakash.polking.profileDetails.ProfileEditView
import com.project.pradyotprakash.polking.utility.TransparentBottomSheet
import com.project.pradyotprakash.polking.utility.logd
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.question_stats_btm_sheet.view.*
import javax.inject.Inject

class QuestionStatistics @Inject constructor() : TransparentBottomSheet(), ProfileEditView {

    private lateinit var questionId: String
    private lateinit var mAuth: FirebaseAuth
    private lateinit var getQuestionFirestore: FirebaseFirestore
    private lateinit var getQuestionUserFirestore: FirebaseFirestore

    companion object {
        fun newInstance(): QuestionStatistics =
            QuestionStatistics().apply {

            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        AndroidInjection.inject(this.activity)
        val view = inflater.inflate(R.layout.question_stats_btm_sheet, container, false)

        dialog!!.setOnShowListener { dialog ->
            val bottomSheetDialog: BottomSheetDialog = dialog as BottomSheetDialog
            val bottomSheetInternal =
                bottomSheetDialog.findViewById<FrameLayout>(R.id.design_bottom_sheet)
            if (bottomSheetInternal != null) {
                BottomSheetBehavior.from<View>(bottomSheetInternal).state =
                    BottomSheetBehavior.STATE_EXPANDED
            }
        }

        activity!!.logd(getString(R.string.profilebottomsheet))

        initView(view)

        return view
    }

    private fun initView(view: View) {
        initVariables()

        setOnClickListners(view)

        getQuestionData(view)
    }

    private fun getQuestionData(view: View) {
        if (context != null && questionId != "") {
            getQuestionFirestore.collection("question").document(questionId)
                .addSnapshotListener { snapshot, exception ->

                    if (exception != null) {
                        showMessage(
                            "Something Went Wrong. ${exception.localizedMessage}", 1
                        )
                    }

                    if (snapshot != null && snapshot.exists()) {
                        try {
                            val askedBy = snapshot.data!!["askedBy"].toString()
                            val askedOn = snapshot.data!!["askedOn"].toString()
                            val noVote = snapshot.data!!["noVote"].toString()
                            val question = snapshot.data!!["question"].toString()
                            val yesVote = snapshot.data!!["yesVote"].toString()
                            view.question_tv.text = question

                            getUserData(askedBy, view)
                        } catch (exception: Exception) {
                            showMessage(
                                "Something Went Wrong. ${exception.localizedMessage}", 1
                            )
                        }

                    }

                }
        } else {
            stopAct()
        }
    }

    private fun getUserData(askedBy: String, view: View) {
        getQuestionUserFirestore.collection("users").document(askedBy)
            .addSnapshotListener { snapshot, exception ->

                if (exception != null) {
                    showMessage(
                        "Something Went Wrong. ${exception.localizedMessage}", 1
                    )
                }

                if (snapshot != null && snapshot.exists()) {

                    try {
                        setUserData(snapshot, view)
                    } catch (exception: Exception) {
                        showMessage(
                            "Something Went Wrong. ${exception.localizedMessage}", 1
                        )
                    }

                }

            }
    }

    private fun setUserData(snapshot: DocumentSnapshot, view: View) {
        Glide.with(context!!).load(snapshot.data!!["imageUrl"].toString())
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    exception: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    showMessage(
                        "Something Went Wrong. ${exception!!.localizedMessage}",
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
                    return false
                }
            }).into(view.user_iv)
        view.user_iv.borderColor =
            context!!.resources.getColor(R.color.colorPrimary)
        view.user_iv.borderWidth = 2

        view.username_tv.text = snapshot.data!!["name"].toString()
    }

    private fun setOnClickListners(view: View) {
        view.back_tv.setOnClickListener {
            dismiss()
        }
    }

    private fun initVariables() {
        mAuth = FirebaseAuth.getInstance()
        getQuestionFirestore = FirebaseFirestore.getInstance()
        getQuestionUserFirestore = FirebaseFirestore.getInstance()
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
        this.questionId = docId
    }
}
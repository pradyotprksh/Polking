package com.project.pradyotprakash.polking.profile.reviewUs

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.project.pradyotprakash.polking.R
import com.project.pradyotprakash.polking.profileDetails.ProfileEditView
import com.project.pradyotprakash.polking.utility.TransparentBottomSheet
import com.project.pradyotprakash.polking.utility.Utility
import com.project.pradyotprakash.polking.utility.logd
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.review_us_btm_sheet.*
import kotlinx.android.synthetic.main.review_us_btm_sheet.view.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.HashMap

class ReviewUsBtmSheet @Inject constructor() : TransparentBottomSheet(), ProfileEditView {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    @SuppressLint("SimpleDateFormat")
    var dateFormat: SimpleDateFormat = SimpleDateFormat("yyyy/MM/dd")
    @SuppressLint("SimpleDateFormat")
    var timeFormat: SimpleDateFormat = SimpleDateFormat("HH:mm:ss")

    companion object {
        fun newInstance(): ReviewUsBtmSheet =
            ReviewUsBtmSheet().apply {

            }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        AndroidInjection.inject(this.activity)
        val view = inflater.inflate(R.layout.review_us_btm_sheet, container, false)

        activity!!.logd(getString(R.string.reviewbottomsheet))

        initView(view)

        return view
    }

    private fun initView(view: View) {

        initVariables()

        setOnClickListners(view)
    }

    private fun setOnClickListners(view: View) {
        view.back_tv.setOnClickListener {
            stopAct()
        }

        view.feedback0_tv.setOnClickListener {
            showReviewSections(view)
            view.val_tv.text = getString(R.string.its_making_us_sad)
        }

        view.feedback1_tv.setOnClickListener {
            showReviewSections(view)
            view.val_tv.text = getString(R.string.its_sad)
        }

        view.feedback2_tv.setOnClickListener {
            showReviewSections(view)
            view.val_tv.text = getString(R.string.tell_us)
        }

        view.feedback3_tv.setOnClickListener {
            showReviewSections(view)
            view.val_tv.text = getString(R.string.thanks)
        }

        view.feedback4_tv.setOnClickListener {
            showReviewSections(view)
            view.val_tv.text = getString(R.string.very_much_thanks)
        }

        view.post_Tv.setOnClickListener {
            if (mAuth.currentUser != null) {
                showLoading()
                val date = Date()
                val reviewData = HashMap<String, Any>()
                /*
                0 1 2 3 4 are the stars value
                 */
                when {
                    view.val_tv.text == getString(R.string.its_making_us_sad) -> reviewData["chooseOption"] =
                        "0"
                    view.val_tv.text == getString(R.string.its_sad) -> reviewData["chooseOption"] =
                        "1"
                    view.val_tv.text == getString(R.string.tell_us) -> reviewData["chooseOption"] =
                        "2"
                    view.val_tv.text == getString(R.string.thanks) -> reviewData["chooseOption"] =
                        "3"
                    view.val_tv.text == getString(R.string.very_much_thanks) -> reviewData["chooseOption"] =
                        "4"
                }
                reviewData["givenOnDate"] = dateFormat.format(date)
                reviewData["givenOnTime"] = timeFormat.format(date)
                reviewData["givenBy"] = mAuth.currentUser!!.uid
                reviewData["feedback"] = if (!addFeed_et3.text.toString().isEmpty()) {
                    addFeed_et3.text.toString()
                } else {
                    "Nothing To Say"
                }

                saveDataToDatabase(view, reviewData)
            } else {
                showMessage(getString(R.string.user_not_found), 1)
            }
        }
    }

    private fun saveDataToDatabase(
        view: View,
        reviewData: HashMap<String, Any>
    ) {
        firestore.collection("reviews").document().set(reviewData).addOnSuccessListener {
            hideLoading()
            showMessage(
                "Thanks For Sharing Your Information",
                3
            )

            Utility().expandCollapse(view.addFeed_et3)
            Utility().expandCollapse(view.val_tv)
            Utility().expandCollapse(view.post_Tv)

            Utility().hideSoftKeyboard(view.addFeed_et3)
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
    }

    private fun showReviewSections(view: View) {
        if (addFeed_et3.visibility == View.GONE) {
            Utility().expandCollapse(view.addFeed_et3)
            Utility().expandCollapse(view.post_Tv)
            Utility().expandCollapse(view.val_tv)
        }
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

    }
}
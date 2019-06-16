package com.project.pradyotprakash.polking.profile.reviewUs

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
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
import com.project.pradyotprakash.polking.utility.Utility
import com.project.pradyotprakash.polking.utility.logd
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.review_us_btm_sheet.*
import kotlinx.android.synthetic.main.review_us_btm_sheet.view.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.HashMap

class ReviewUsBtmSheet @Inject constructor() : RoundBottomSheet(), ProfileEditView {

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

        dialog.setOnShowListener { dialog ->
            val bottomSheetDialog: BottomSheetDialog = dialog as BottomSheetDialog
            val bottomSheetInternal = bottomSheetDialog.findViewById<FrameLayout>(R.id.design_bottom_sheet)
            if (bottomSheetInternal != null) {
                BottomSheetBehavior.from<View>(bottomSheetInternal).state = BottomSheetBehavior.STATE_EXPANDED
            }
        }

        activity!!.logd(getString(R.string.reviewbottomsheet))

        initView(view)

        return view
    }

    private fun initView(view: View) {
        mAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        view.back_tv.setOnClickListener {
            stopAct()
        }

        view.feedback0_tv.setOnClickListener {
            if (view.feedback1_tv.visibility != View.GONE) {
                view.feedback1_tv.startAnimation(Utility().outToRightAnimation())
                view.feedback2_tv.startAnimation(Utility().outToRightAnimation())
                view.feedback3_tv.startAnimation(Utility().outToRightAnimation())
                view.feedback4_tv.startAnimation(Utility().outToRightAnimation())
                view.feedback1_tv.visibility = View.INVISIBLE
                view.feedback2_tv.visibility = View.INVISIBLE
                view.feedback3_tv.visibility = View.INVISIBLE
                view.feedback4_tv.visibility = View.INVISIBLE
                Handler().postDelayed({
                    view.feedback1_tv.visibility = View.GONE
                    view.feedback2_tv.visibility = View.GONE
                    view.feedback3_tv.visibility = View.GONE
                    view.feedback4_tv.visibility = View.GONE

                    Utility().expandCollapse(view.addFeed_et3)
                    Utility().expandCollapse(view.post_Tv)
                    Utility().expandCollapse(view.val_tv)
                    view.val_tv.text = getString(R.string.its_making_us_sad)
                }, 300)
            } else {
                view.feedback1_tv.visibility = View.VISIBLE
                view.feedback2_tv.visibility = View.VISIBLE
                view.feedback3_tv.visibility = View.VISIBLE
                view.feedback4_tv.visibility = View.VISIBLE
                view.feedback1_tv.startAnimation(Utility().inFromLeftAnimation())
                view.feedback2_tv.startAnimation(Utility().inFromLeftAnimation())
                view.feedback3_tv.startAnimation(Utility().inFromLeftAnimation())
                view.feedback4_tv.startAnimation(Utility().inFromLeftAnimation())

                Utility().expandCollapse(view.addFeed_et3)
                Utility().expandCollapse(view.val_tv)
                Utility().expandCollapse(view.post_Tv)
            }
        }

        view.feedback1_tv.setOnClickListener {
            if (view.feedback0_tv.visibility != View.GONE) {
                view.feedback0_tv.startAnimation(Utility().outToRightAnimation())
                view.feedback2_tv.startAnimation(Utility().outToRightAnimation())
                view.feedback3_tv.startAnimation(Utility().outToRightAnimation())
                view.feedback4_tv.startAnimation(Utility().outToRightAnimation())
                view.feedback0_tv.visibility = View.INVISIBLE
                view.feedback2_tv.visibility = View.INVISIBLE
                view.feedback3_tv.visibility = View.INVISIBLE
                view.feedback4_tv.visibility = View.INVISIBLE
                Handler().postDelayed({
                    view.feedback0_tv.visibility = View.GONE
                    view.feedback2_tv.visibility = View.GONE
                    view.feedback3_tv.visibility = View.GONE
                    view.feedback4_tv.visibility = View.GONE

                    Utility().expandCollapse(view.addFeed_et3)
                    Utility().expandCollapse(view.post_Tv)
                    Utility().expandCollapse(view.val_tv)
                    view.val_tv.text = getString(R.string.its_sad)
                }, 300)
            } else {
                view.feedback0_tv.visibility = View.VISIBLE
                view.feedback2_tv.visibility = View.VISIBLE
                view.feedback3_tv.visibility = View.VISIBLE
                view.feedback4_tv.visibility = View.VISIBLE
                view.feedback0_tv.startAnimation(Utility().inFromLeftAnimation())
                view.feedback2_tv.startAnimation(Utility().inFromLeftAnimation())
                view.feedback3_tv.startAnimation(Utility().inFromLeftAnimation())
                view.feedback4_tv.startAnimation(Utility().inFromLeftAnimation())

                Utility().expandCollapse(view.addFeed_et3)
                Utility().expandCollapse(view.val_tv)
                Utility().expandCollapse(view.post_Tv)
            }
        }

        view.feedback2_tv.setOnClickListener {
            if (view.feedback0_tv.visibility != View.GONE) {
                view.feedback1_tv.startAnimation(Utility().outToRightAnimation())
                view.feedback0_tv.startAnimation(Utility().outToRightAnimation())
                view.feedback3_tv.startAnimation(Utility().outToRightAnimation())
                view.feedback4_tv.startAnimation(Utility().outToRightAnimation())
                view.feedback1_tv.visibility = View.INVISIBLE
                view.feedback0_tv.visibility = View.INVISIBLE
                view.feedback3_tv.visibility = View.INVISIBLE
                view.feedback4_tv.visibility = View.INVISIBLE
                Handler().postDelayed({
                    view.feedback1_tv.visibility = View.GONE
                    view.feedback0_tv.visibility = View.GONE
                    view.feedback3_tv.visibility = View.GONE
                    view.feedback4_tv.visibility = View.GONE

                    Utility().expandCollapse(view.addFeed_et3)
                    Utility().expandCollapse(view.val_tv)
                    Utility().expandCollapse(view.post_Tv)
                    view.val_tv.text = getString(R.string.tell_us)
                }, 300)
            } else {
                view.feedback1_tv.visibility = View.VISIBLE
                view.feedback0_tv.visibility = View.VISIBLE
                view.feedback3_tv.visibility = View.VISIBLE
                view.feedback4_tv.visibility = View.VISIBLE
                view.feedback1_tv.startAnimation(Utility().inFromLeftAnimation())
                view.feedback0_tv.startAnimation(Utility().inFromLeftAnimation())
                view.feedback3_tv.startAnimation(Utility().inFromLeftAnimation())
                view.feedback4_tv.startAnimation(Utility().inFromLeftAnimation())

                Utility().expandCollapse(view.addFeed_et3)
                Utility().expandCollapse(view.val_tv)
                Utility().expandCollapse(view.post_Tv)
            }
        }

        view.feedback3_tv.setOnClickListener {
            if (view.feedback0_tv.visibility != View.GONE) {
                view.feedback1_tv.startAnimation(Utility().outToRightAnimation())
                view.feedback2_tv.startAnimation(Utility().outToRightAnimation())
                view.feedback0_tv.startAnimation(Utility().outToRightAnimation())
                view.feedback4_tv.startAnimation(Utility().outToRightAnimation())
                view.feedback1_tv.visibility = View.INVISIBLE
                view.feedback2_tv.visibility = View.INVISIBLE
                view.feedback0_tv.visibility = View.INVISIBLE
                view.feedback4_tv.visibility = View.INVISIBLE
                Handler().postDelayed({
                    view.feedback1_tv.visibility = View.GONE
                    view.feedback2_tv.visibility = View.GONE
                    view.feedback0_tv.visibility = View.GONE
                    view.feedback4_tv.visibility = View.GONE

                    Utility().expandCollapse(view.addFeed_et3)
                    Utility().expandCollapse(view.val_tv)
                    Utility().expandCollapse(view.post_Tv)
                    view.val_tv.text = getString(R.string.thanks)
                }, 300)
            } else {
                view.feedback1_tv.visibility = View.VISIBLE
                view.feedback2_tv.visibility = View.VISIBLE
                view.feedback0_tv.visibility = View.VISIBLE
                view.feedback4_tv.visibility = View.VISIBLE
                view.feedback1_tv.startAnimation(Utility().inFromLeftAnimation())
                view.feedback2_tv.startAnimation(Utility().inFromLeftAnimation())
                view.feedback0_tv.startAnimation(Utility().inFromLeftAnimation())
                view.feedback4_tv.startAnimation(Utility().inFromLeftAnimation())

                Utility().expandCollapse(view.addFeed_et3)
                Utility().expandCollapse(view.val_tv)
                Utility().expandCollapse(view.post_Tv)
            }
        }

        view.feedback4_tv.setOnClickListener {
            if (view.feedback0_tv.visibility != View.GONE) {
                view.feedback1_tv.startAnimation(Utility().outToRightAnimation())
                view.feedback2_tv.startAnimation(Utility().outToRightAnimation())
                view.feedback3_tv.startAnimation(Utility().outToRightAnimation())
                view.feedback0_tv.startAnimation(Utility().outToRightAnimation())
                view.feedback1_tv.visibility = View.INVISIBLE
                view.feedback2_tv.visibility = View.INVISIBLE
                view.feedback3_tv.visibility = View.INVISIBLE
                view.feedback0_tv.visibility = View.INVISIBLE
                Handler().postDelayed({
                    view.feedback1_tv.visibility = View.GONE
                    view.feedback2_tv.visibility = View.GONE
                    view.feedback3_tv.visibility = View.GONE
                    view.feedback0_tv.visibility = View.GONE

                    Utility().expandCollapse(view.addFeed_et3)
                    Utility().expandCollapse(view.val_tv)
                    Utility().expandCollapse(view.post_Tv)
                    view.val_tv.text = getString(R.string.very_much_thanks)
                }, 300)
            } else {
                view.feedback1_tv.visibility = View.VISIBLE
                view.feedback2_tv.visibility = View.VISIBLE
                view.feedback3_tv.visibility = View.VISIBLE
                view.feedback0_tv.visibility = View.VISIBLE
                view.feedback1_tv.startAnimation(Utility().inFromLeftAnimation())
                view.feedback2_tv.startAnimation(Utility().inFromLeftAnimation())
                view.feedback3_tv.startAnimation(Utility().inFromLeftAnimation())
                view.feedback0_tv.startAnimation(Utility().inFromLeftAnimation())

                Utility().expandCollapse(view.addFeed_et3)
                Utility().expandCollapse(view.val_tv)
                Utility().expandCollapse(view.post_Tv)
            }
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
                    view.feedback0_tv.visibility == View.VISIBLE -> reviewData["chooseOption"] = "0"
                    view.feedback1_tv.visibility == View.VISIBLE -> reviewData["chooseOption"] = "1"
                    view.feedback2_tv.visibility == View.VISIBLE -> reviewData["chooseOption"] = "2"
                    view.feedback3_tv.visibility == View.VISIBLE -> reviewData["chooseOption"] = "3"
                    view.feedback4_tv.visibility == View.VISIBLE -> reviewData["chooseOption"] = "4"
                }
                reviewData["givenOnDate"] = dateFormat.format(date)
                reviewData["givenOnTime"] = timeFormat.format(date)
                reviewData["givenBy"] = mAuth.currentUser!!.uid
                reviewData["feedback"] = if (!addFeed_et3.text.toString().isEmpty()) {
                    addFeed_et3.text.toString()
                } else {
                    "Nothing To Say"
                }

                firestore.collection("reviews").document().set(reviewData).addOnSuccessListener {
                    hideLoading()
                    showMessage(
                        "Thanks For Sharing Your Information",
                        3
                    )
                    view.feedback1_tv.visibility = View.VISIBLE
                    view.feedback2_tv.visibility = View.VISIBLE
                    view.feedback3_tv.visibility = View.VISIBLE
                    view.feedback0_tv.visibility = View.VISIBLE
                    view.feedback4_tv.visibility = View.VISIBLE

                    Utility().expandCollapse(view.addFeed_et3)
                    Utility().expandCollapse(view.val_tv)
                    Utility().expandCollapse(view.post_Tv)
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
                showMessage(getString(R.string.user_not_found), 1)
            }
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
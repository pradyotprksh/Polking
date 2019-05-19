package com.project.pradyotprakash.polking.profileDetails

import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.BottomSheetDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.project.pradyotprakash.polking.R
import com.project.pradyotprakash.polking.utility.RoundBottomSheet
import kotlinx.android.synthetic.main.profile_edit_btm_sheet.*
import kotlinx.android.synthetic.main.profile_edit_btm_sheet.view.*
import javax.inject.Inject

class ProfileEditBtmSheet @Inject constructor() : RoundBottomSheet() {

    @Inject
    lateinit var presenter: ProfileEditPresenter

    companion object {
        fun newInstance(): ProfileEditBtmSheet =
            ProfileEditBtmSheet().apply {

            }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.profile_edit_btm_sheet, container, false)

        dialog.setOnShowListener { dialog ->
            val bottomSheetDialog: BottomSheetDialog = dialog as BottomSheetDialog
            val bottomSheetInternal = bottomSheetDialog.findViewById<FrameLayout>(R.id.design_bottom_sheet)
            if (bottomSheetInternal != null) {
                BottomSheetBehavior.from<View>(bottomSheetInternal).state = BottomSheetBehavior.STATE_EXPANDED
            }
        }

        initView(view)
        return view
    }

    private fun initView(view: View) {
        view.profile_iv.setOnClickListener {

        }

        view.male_tv.setOnClickListener {
            male_tv.setTextColor(resources.getColor(R.color.colorPrimaryDark))
            female_tv.setTextColor(resources.getColor(R.color.black))
            other_tv.setTextColor(resources.getColor(R.color.black))
        }

        view.female_tv.setOnClickListener {
            male_tv.setTextColor(resources.getColor(R.color.black))
            female_tv.setTextColor(resources.getColor(R.color.colorPrimaryDark))
            other_tv.setTextColor(resources.getColor(R.color.black))
        }

        view.other_tv.setOnClickListener {
            male_tv.setTextColor(resources.getColor(R.color.black))
            female_tv.setTextColor(resources.getColor(R.color.black))
            other_tv.setTextColor(resources.getColor(R.color.colorPrimaryDark))
        }
    }


}
package com.project.pradyotprakash.polking.message

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.project.pradyotprakash.polking.R
import com.project.pradyotprakash.polking.profileDetails.ProfileEditView
import com.project.pradyotprakash.polking.utility.TransparentBottomSheet
import com.project.pradyotprakash.polking.utility.logd
import com.skydoves.whatif.whatIfNotNull
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.show_msg_btm_sheet.view.*
import javax.inject.Inject

class ShowMessage @Inject constructor() : TransparentBottomSheet(), ProfileEditView {

    private var type: Int = 1
    private var message: String = ""

    companion object {
        fun newInstance(): ShowMessage =
            ShowMessage().apply {

            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        AndroidInjection.inject(this.activity)
        val view = inflater.inflate(R.layout.show_msg_btm_sheet, container, false)

        dialog!!.setOnShowListener { dialog ->
            val bottomSheetDialog: BottomSheetDialog = dialog as BottomSheetDialog
            val bottomSheetInternal =
                bottomSheetDialog.findViewById<FrameLayout>(R.id.design_bottom_sheet)
            bottomSheetInternal.whatIfNotNull {
                BottomSheetBehavior.from<View>(bottomSheetInternal).state =
                    BottomSheetBehavior.STATE_EXPANDED
            }
        }

        activity!!.logd(getString(R.string.profilebottomsheet))

        initView(view)

        return view
    }

    private fun dismissAfterOnePointFiveSec(view: View) {
        Handler().postDelayed({
            if (!isDetached && isVisible) {
                dismiss()
            }
        }, 2500)
    }

    private fun initView(view: View) {
        view.message_tv.text = message
        when (type) {
            1 -> {
                view.error_animation.visibility = View.VISIBLE
                view.information_animation.visibility = View.GONE
                view.success_animation.visibility = View.GONE
                view.no_network_animation.visibility = View.GONE
                dismissAfterOnePointFiveSec(view)
            }
            2 -> {
                view.error_animation.visibility = View.GONE
                view.information_animation.visibility = View.VISIBLE
                view.success_animation.visibility = View.GONE
                view.no_network_animation.visibility = View.GONE
            }
            3 -> {
                view.error_animation.visibility = View.GONE
                view.information_animation.visibility = View.GONE
                view.success_animation.visibility = View.VISIBLE
                view.no_network_animation.visibility = View.GONE
                dismissAfterOnePointFiveSec(view)
            }
            else -> {
                view.error_animation.visibility = View.GONE
                view.information_animation.visibility = View.GONE
                view.success_animation.visibility = View.GONE
                view.no_network_animation.visibility = View.VISIBLE
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

    fun setMessage(message: String, type: Int) {
        this.message = message
        this.type = type
    }
}
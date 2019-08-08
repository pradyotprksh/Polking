package com.project.pradyotprakash.polking.utility

import android.app.Dialog
import android.os.Bundle
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.project.pradyotprakash.polking.R

open class TransparentBottomSheet : BottomSheetDialogFragment() {

    override fun getTheme(): Int = R.style.TransparentBottomSheetDialogTheme

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog = BottomSheetDialog(requireContext(), theme)

}
package com.project.pradyotprakash.polking.profileDetails

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.BottomSheetDialog
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.project.pradyotprakash.polking.R
import com.project.pradyotprakash.polking.utility.AppConstants
import com.project.pradyotprakash.polking.utility.RoundBottomSheet
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.profile_edit_btm_sheet.*
import kotlinx.android.synthetic.main.profile_edit_btm_sheet.view.*
import javax.inject.Inject

class ProfileEditBtmSheet @Inject constructor() : RoundBottomSheet(), ProfileEditView {

    @Inject lateinit var presenter: ProfileEditPresenter
    private var count = 0
    private var count1 = 0

    companion object {
        fun newInstance(): ProfileEditBtmSheet =
            ProfileEditBtmSheet().apply {

            }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        AndroidInjection.inject(this.activity)
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
        presenter.attachView(this)
        view.profile_iv.setOnClickListener {
            if (presenter.checkReadPermission() && presenter.checkWritePermission()) {
                openCamera()
            }
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

    override fun showLoading() {

    }

    override fun hideLoading() {

    }

    override fun stopAct() {

    }

    override fun showMessage(message: String, type: Int) {

    }

    private fun openCamera() {
        if (activity != null) {
            CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).setAspectRatio(1, 1).start(activity!!)
        } else {
            showMessage("Unable To Start. Check Permission. Please.", 1)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            AppConstants.PERMISSIONS_REQUEST_READ_STORAGE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(
                            context!!,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        openCamera()
                    } else {
                        presenter.checkReadPermission()
                    }
                } else {
                    presenter.checkReadPermission()
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (!shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                            count1++
                            if (count1 > 1) {
                                showMessageOKCancel(
                                    getString(R.string.readStoragePermission)
                                ) { _, _ ->
                                    val intent = Intent()
                                    intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                                    val uri = Uri.fromParts("package", context!!.packageName, null)
                                    intent.data = uri
                                    startActivity(intent)
                                }
                            }
                        }
                    }
                }
                return
            }

            AppConstants.PERMISSIONS_REQUEST_WRITE_STORAGE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(
                            context!!,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        openCamera()
                    } else {
                        presenter.checkWritePermission()
                    }
                } else {
                    presenter.checkWritePermission()
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (!shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                            count++
                            if (count > 1) {
                                showMessageOKCancel(
                                    getString(R.string.writeStoragePermission)
                                ) { _, _ ->
                                    val intent = Intent()
                                    intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                                    val uri = Uri.fromParts("package", context!!.packageName, null)
                                    intent.data = uri
                                    startActivity(intent)
                                }
                            }
                        }
                    }
                }
                return
            }
        }
    }

    private fun showMessageOKCancel(message: String, okListener: (Any, Any) -> Unit) {
        if (context != null) {
            AlertDialog.Builder(context!!)
                .setMessage(message).setPositiveButton(getString(R.string.ok_string), okListener)
                .setNegativeButton(getString(R.string.cancel_string), null).create().show()
        }
    }

}
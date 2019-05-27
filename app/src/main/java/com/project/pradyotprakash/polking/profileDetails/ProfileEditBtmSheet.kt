package com.project.pradyotprakash.polking.profileDetails

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.BottomSheetDialog
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.project.pradyotprakash.polking.R
import com.project.pradyotprakash.polking.utility.AppConstants
import com.project.pradyotprakash.polking.utility.RoundBottomSheet
import com.project.pradyotprakash.polking.utility.logd
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.profile_edit_btm_sheet.*
import kotlinx.android.synthetic.main.profile_edit_btm_sheet.view.*
import javax.inject.Inject

class ProfileEditBtmSheet @Inject constructor() : RoundBottomSheet(), ProfileEditView {

    private var count = 0
    private var count1 = 0
    private var userMainImageURI: Uri? = null
    private lateinit var mAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    /*
    0 - Male
    1 - Female
    2 - Others
     */
    private var genderType = -1

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

        activity!!.logd("ProfileEditBtmSheet onCreateView")

        initView(view)
        return view
    }

    private fun initView(view: View) {
        mAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        view.profile_iv.setOnClickListener {
            if (checkReadPermission() && checkWritePermission()) {
                openCamera()
            }
        }

        view.male_tv.setOnClickListener {
            male_tv.setTextColor(resources.getColor(R.color.colorPrimaryDark))
            female_tv.setTextColor(resources.getColor(R.color.black))
            other_tv.setTextColor(resources.getColor(R.color.black))
            genderType = 0
        }

        view.female_tv.setOnClickListener {
            male_tv.setTextColor(resources.getColor(R.color.black))
            female_tv.setTextColor(resources.getColor(R.color.colorPrimaryDark))
            other_tv.setTextColor(resources.getColor(R.color.black))
            genderType = 1
        }

        view.other_tv.setOnClickListener {
            male_tv.setTextColor(resources.getColor(R.color.black))
            female_tv.setTextColor(resources.getColor(R.color.black))
            other_tv.setTextColor(resources.getColor(R.color.colorPrimaryDark))
            genderType = 2
        }

        view.saveTv.setOnClickListener {
            if (userMainImageURI!=null) {
                if (view.name_et.text.toString().length > 3) {
                    if (view.age_et.text.toString().isNotEmpty()) {
                        if (view.age_et.text.toString().toInt() > 13) {
                            if (genderType != -1) {

                            } else {
                                showMessage("Please Select A Gender.", 1)
                            }
                        } else {
                            showMessage("To Use This App You Must Be 13+. Sorry.", 1)
                        }
                    } else {
                        showMessage("Please Enter Your Age.", 1)
                    }
                } else {
                    showMessage("Your Name Must Be Greater Than 3 Characters.", 1)
                }
            }
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

    fun getImageUri(imageUri: Uri?) {
        this.userMainImageURI = imageUri
        profile_iv.setImageURI(userMainImageURI)
        profile_iv.borderColor = resources.getColor(R.color.colorPrimary)
        profile_iv.borderWidth = 4
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
                        checkReadPermission()
                    }
                } else {
                    checkReadPermission()
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
                        checkWritePermission()
                    }
                } else {
                    checkWritePermission()
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

    private fun checkReadPermission() : Boolean {
        return if (ContextCompat.checkSelfPermission(context!!,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity!!, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(
                    activity!!,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    AppConstants.PERMISSIONS_REQUEST_READ_STORAGE
                )
            } else {
                ActivityCompat.requestPermissions(
                    activity!!,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    AppConstants.PERMISSIONS_REQUEST_READ_STORAGE
                )
            }
            false
        } else {
            true
        }
    }

    private fun checkWritePermission() : Boolean {
        return if (ContextCompat.checkSelfPermission(activity!!,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity!!, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(
                    activity!!,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    AppConstants.PERMISSIONS_REQUEST_WRITE_STORAGE
                )
            } else {
                ActivityCompat.requestPermissions(
                    activity!!,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    AppConstants.PERMISSIONS_REQUEST_WRITE_STORAGE
                )
            }
            false
        } else {
            true
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
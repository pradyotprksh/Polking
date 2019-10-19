package com.project.pradyotprakash.polking.profileDetails

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.project.pradyotprakash.polking.R
import com.project.pradyotprakash.polking.message.ShowMessage
import com.project.pradyotprakash.polking.utility.*
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.profile_edit_btm_sheet.*
import kotlinx.android.synthetic.main.profile_edit_btm_sheet.view.*
import javax.inject.Inject

class ProfileEditBtmSheet @Inject constructor() : TransparentBottomSheet(), ProfileEditView {

    private var questions: String? = "0"
    private var friends: String? = "0"
    private var best_friends: String? = "0"
    private var bg_option: String? = "bg_one"
    private var count = 0
    private var count1 = 0
    private var imageUrl: String? = null
    private var userMainImageURI: Uri? = null
    private lateinit var mAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    lateinit var messageBtmSheet: ShowMessage

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

        dialog!!.setOnShowListener { dialog ->
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
        initVariables()

        getUserData(view)

        addDatePickerListner(view)

        addOnClickListners(view)

        isCancalableCase(view)
    }

    private fun isCancalableCase(view: View) {
        if (isCancelable) {
            view.back_tv.visibility = View.VISIBLE
        } else {
            view.back_tv.visibility = View.GONE
        }
    }

    private fun addOnClickListners(view: View) {
        view.user_iv.setOnClickListener {
            if (checkReadPermission() && checkWritePermission()) {
                openCamera()
            } else {
                showMessage(getString(R.string.permission_not_granted), 2)
            }
        }

        view.back_tv.setOnClickListener {
            dismiss()
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
            saveDataToDatabase(view)
        }
    }

    private fun saveDataToDatabase(view: View) {
        if (mAuth.currentUser != null) {
            if (imageUrl == null) {
                if (allDataIsThere(view)) {
                    addInitalDataToDatabase(view)
                }
            } else {
                addDataToDatabase(view)
            }
        } else {
            showMessage(getString(R.string.user_not_found), 1)
        }
    }

    private fun addInitalDataToDatabase(view: View) {
        view.mainProgressBar.visibility = View.VISIBLE

        val storage = FirebaseStorage.getInstance().reference
        val imagePath: StorageReference =
            storage.child("user_profile_image").child("${mAuth.currentUser!!.uid}.jpg")
        imagePath.putFile(userMainImageURI!!)
            .continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                if (!task.isSuccessful) {
                    task.exception?.let { exception ->
                        showMessage(
                            "Something Went Wrong. ${exception.localizedMessage}",
                            1
                        )
                        view.mainProgressBar.visibility = View.GONE
                        throw exception
                    }
                }
                return@Continuation imagePath.downloadUrl
            }).addOnCanceledListener {
                showMessage(getString(R.string.not_uploaded), 4)
                view.mainProgressBar.visibility = View.GONE
            }.addOnFailureListener { exception ->
                showMessage("Something Went Wrong. ${exception.localizedMessage}", 1)
                view.mainProgressBar.visibility = View.GONE
            }.addOnCompleteListener { task ->

                uploadTheImageUrlWithData(view, task, imagePath)

            }.addOnSuccessListener {
                showMessage(getString(R.string.save_properly), 3)
            }
    }

    private fun uploadTheImageUrlWithData(
        view: View,
        task: Task<Uri>,
        imagePath: StorageReference
    ) {
        if (task.isComplete) {
            if (task.isSuccessful) {

                imagePath.downloadUrl.addOnSuccessListener { uri ->

                    imageUrl = uri.toString()

                    if (imageUrl != null) {

                        addDataToDatabase(view)

                    } else {
                        showMessage(getString(R.string.not_uploaded), 1)
                        view.mainProgressBar.visibility = View.GONE
                    }

                }.addOnFailureListener { exception ->
                    showMessage(
                        "Something Went Wrong. ${exception.localizedMessage}",
                        1
                    )
                    view.mainProgressBar.visibility = View.GONE
                }.addOnCanceledListener {
                    showMessage(getString(R.string.not_uploaded), 1)
                    view.mainProgressBar.visibility = View.GONE
                }

            } else if (task.isCanceled) {
                showMessage(getString(R.string.not_uploaded), 1)
                view.mainProgressBar.visibility = View.GONE
            }
        } else {
            showMessage(getString(R.string.something_went_wrong), 1)
            view.mainProgressBar.visibility = View.GONE
        }
    }

    private fun addDataToDatabase(view: View) {
        val userData = HashMap<String, Any>()
        userData["imageUrl"] = imageUrl!!
        userData["name"] = view.addQuestion_et.text.toString()
        userData["age"] =
            Utility().getAge(view.age_et.text.toString())
        userData["birthDay"] = view.age_et.text.toString()
        userData["gender"] = genderType.toString()
        userData["questions"] = questions!!
        userData["friends"] = friends!!
        userData["best_friends"] =
            best_friends!!
        userData["bg_option"] = bg_option!!

        firestore.collection("users")
            .document(mAuth.currentUser!!.uid)
            .set(userData).addOnSuccessListener {

                showMessage(
                    "Data Uploaded Successfully.",
                    3
                )
                view.mainProgressBar.visibility = View.GONE
                stopAct()

            }.addOnFailureListener { exception ->
                showMessage(
                    "Something Went Wrong. ${exception.localizedMessage}",
                    1
                )
                view.mainProgressBar.visibility = View.GONE
            }.addOnCanceledListener {
                showMessage(getString(R.string.not_uploaded), 4)
                view.mainProgressBar.visibility = View.GONE
            }
    }

    private fun allDataIsThere(view: View): Boolean {
        if (userMainImageURI != null) {
            if (view.addQuestion_et.text.toString().length > 3) {
                if (view.age_et.text.toString().isNotEmpty()) {
                    if (Utility().getAge(view.age_et.text.toString()) > 13) {
                        if (genderType != -1) {
                            return true
                        } else {
                            showMessage(getString(R.string.select_gender), 1)
                        }
                    } else {
                        showMessage(getString(R.string.above_thirteen_msg), 1)
                    }
                } else {
                    showMessage(getString(R.string.enter_birthdate), 1)
                }
            } else {
                showMessage(getString(R.string.name_length_restriction), 1)
            }
        } else {
            showMessage(getString(R.string.select_picture), 1)
        }
        return false
    }

    private fun addDatePickerListner(view: View) {
        DatePicker(view.age_et).listen()
    }

    private fun initVariables() {
        mAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
    }

    private fun getUserData(view: View) {
        view.mainProgressBar.visibility = View.VISIBLE
        view.imagePrgBsr.visibility = View.VISIBLE
        firestore.collection("users").document(mAuth.currentUser!!.uid).get().addOnSuccessListener { result ->

            if (result.exists()) {

                imageUrl = result.getString("imageUrl")

                Glide.with(this).load(result.getString("imageUrl")).listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        exception: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        view.imagePrgBsr.visibility = View.GONE
                        showMessage("Something Went Wrong. ${exception?.localizedMessage}", 1)
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        view.imagePrgBsr.visibility = View.GONE
                        return false
                    }
                }).into(view.user_iv)

                view.user_iv.borderColor = resources.getColor(R.color.colorPrimary)
                view.user_iv.borderWidth = 4

                view.addQuestion_et.setText(result.getString("name"))
                view.age_et.setText(result.getString("birthDay"))

                questions = result.getString("questions")
                friends = result.getString("friends")
                best_friends = result.getString("best_friends")
                bg_option = result.getString("bg_option")

                /*
                    0 - Male
                    1 - Female
                    2 - Others
                */
                when {
                    Integer.parseInt(result.get("gender").toString()) == 0 -> {
                        male_tv.setTextColor(resources.getColor(R.color.colorPrimaryDark))
                        female_tv.setTextColor(resources.getColor(R.color.black))
                        other_tv.setTextColor(resources.getColor(R.color.black))
                        genderType = 0
                    }
                    Integer.parseInt(result.get("gender").toString()) == 1 -> {
                        male_tv.setTextColor(resources.getColor(R.color.black))
                        female_tv.setTextColor(resources.getColor(R.color.colorPrimaryDark))
                        other_tv.setTextColor(resources.getColor(R.color.black))
                        genderType = 1
                    }
                    Integer.parseInt(result.get("gender").toString()) == 2 -> {
                        male_tv.setTextColor(resources.getColor(R.color.black))
                        female_tv.setTextColor(resources.getColor(R.color.black))
                        other_tv.setTextColor(resources.getColor(R.color.colorPrimaryDark))
                        genderType = 2
                    }
                }

                view.mainProgressBar.visibility = View.GONE

            } else {
                view.mainProgressBar.visibility = View.GONE
                view.imagePrgBsr.visibility = View.GONE
            }

        }.addOnFailureListener { exception ->
            showMessage("Something Went Wrong. {${exception.localizedMessage}}", 1)
            view.mainProgressBar.visibility = View.GONE
        }.addOnCanceledListener {
            showMessage(getString(R.string.getting_details), 4)
            view.mainProgressBar.visibility = View.GONE
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
        messageBtmSheet = ShowMessage.newInstance()
        if (!messageBtmSheet.isAdded) {
            messageBtmSheet.show(childFragmentManager, "btmSheet")
            messageBtmSheet.setMessage(message, type)
        } else {
            messageBtmSheet.dismiss()
            Handler().postDelayed({
                if (!messageBtmSheet.isAdded) {
                    messageBtmSheet.show(childFragmentManager, "btmSheet")
                    messageBtmSheet.setMessage(message, type)
                }
            }, 1500)
        }
    }

    fun getImageUri(imageUri: Uri?) {
        this.userMainImageURI = imageUri
        user_iv.setImageURI(userMainImageURI)
        user_iv.borderColor = resources.getColor(R.color.colorPrimary)
        user_iv.borderWidth = 4
        imageUrl = null
    }

    private fun openCamera() {
        if (activity != null) {
            CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).setAspectRatio(1, 1).start(activity!!)
        } else {
            showMessage(getString(R.string.check_permission), 1)
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
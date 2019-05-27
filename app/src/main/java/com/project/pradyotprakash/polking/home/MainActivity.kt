package com.project.pradyotprakash.polking.home

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import com.project.pradyotprakash.polking.R
import com.project.pradyotprakash.polking.profile.ProfileActivity
import com.project.pradyotprakash.polking.profileDetails.ProfileEditBtmSheet
import com.project.pradyotprakash.polking.signin.SignInActivity
import com.project.pradyotprakash.polking.utility.logd
import com.project.pradyotprakash.polking.utility.openActivity
import com.theartofdev.edmodo.cropper.CropImage
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

class MainActivity : AppCompatActivity(), MainActivityView {

    @Inject
    lateinit var presenter: MainActivityPresenter
    lateinit var profileEditBtmSheet: ProfileEditBtmSheet

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)

        // Make full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        setContentView(R.layout.activity_main)

        logd("Create")
        initialize()
    }

    private fun initialize() {
        presenter.start()

        bg_iv.setOnClickListener {
            presenter.isLoggedIn()
        }

        profileEditBtmSheet = ProfileEditBtmSheet.newInstance()
        profileEditBtmSheet.isCancelable = false
    }

    override fun onResume() {
        super.onResume()
        logd("Resume")
        presenter.getProfileData()
    }

    override fun startProfileAct() {
        hideLoading()
        openActivity(ProfileActivity::class.java)
    }

    override fun startLogin() {
        hideLoading()
        openActivity(SignInActivity::class.java)
    }

    override fun showLoading() {
        progressBar.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        progressBar.visibility = View.GONE
    }

    override fun stopAct() {
        finish()
    }

    override fun openAddProfileDetails() {
        if (!profileEditBtmSheet.isAdded) {
            profileEditBtmSheet.show(supportFragmentManager, "btmSheet")
        }
    }

    override fun showMessage(message: String, type: Int) {

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == Activity.RESULT_OK) {
                if (profileEditBtmSheet.isAdded) {
                    profileEditBtmSheet.getImageUri(result.uri)
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                showMessage("Something Went Wrong While Uploading Image. Please Try Again.", 1)
            }
        }
    }
}

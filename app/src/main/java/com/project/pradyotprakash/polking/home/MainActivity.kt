package com.project.pradyotprakash.polking.home

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
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

class MainActivity : AppCompatActivity(), MainActivityView {

    @Inject
    lateinit var presenter: MainActivityPresenter
    @Inject
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
            profileEditBtmSheet = ProfileEditBtmSheet.newInstance()
            profileEditBtmSheet.isCancelable = false
            profileEditBtmSheet.show(supportFragmentManager, "btmSheet")
        }
    }

    override fun showMessage(message: String, type: Int) {

    }
}

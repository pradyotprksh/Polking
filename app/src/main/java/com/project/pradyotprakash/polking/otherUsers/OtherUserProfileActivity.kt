package com.project.pradyotprakash.polking.otherUsers

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Window
import android.view.WindowManager
import com.project.pradyotprakash.polking.R
import com.project.pradyotprakash.polking.utility.logd
import dagger.android.AndroidInjection
import javax.inject.Inject

class OtherUserProfileActivity : AppCompatActivity(), OtherUserProfileView {

    @Inject
    lateinit var presenter: OtherUserProfilePresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)

        // Make full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        setContentView(R.layout.activity_other_user_profile)

        logd(getString(R.string.create))
        initialize()
    }

    private fun initialize() {

    }

    override fun showLoading() {

    }

    override fun hideLoading() {

    }

    override fun stopAct() {

    }

    override fun showMessage(message: String, type: Int) {

    }

}

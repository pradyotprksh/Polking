package com.project.pradyotprakash.polking.verifyOTP

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import com.project.pradyotprakash.polking.R
import com.project.pradyotprakash.polking.utility.logd
import dagger.android.AndroidInjection
import javax.inject.Inject

class VerifyOTPActivity : AppCompatActivity(), VerifyOTPView {

    @Inject
    lateinit var presenter: VerifyOTPPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)

        // Make full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        setContentView(R.layout.activity_verify_otp)

        logd("Create")
        initialize()
    }

    private fun initialize() {
        presenter.start()
    }

    override fun showLoading() {

    }

    override fun hideLoading() {

    }

    override fun stopAct() {

    }

}

package com.project.pradyotprakash.polking.profile

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import com.project.pradyotprakash.polking.R
import com.project.pradyotprakash.polking.utility.logd
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_profile.*
import javax.inject.Inject

class ProfileActivity : AppCompatActivity(), ProfileActivityView {

    @Inject
    lateinit var presenter: ProfileActivityPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)

        // Make full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        setContentView(R.layout.activity_profile)

        logd("Create")
        initialize()
    }

    private fun initialize() {
        options_tv.setOnClickListener {
            optionList_cl.visibility = View.VISIBLE
        }

        iv_close.setOnClickListener {
            optionList_cl.visibility = View.GONE
        }

        back_tv.setOnClickListener {
            finish()
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
}

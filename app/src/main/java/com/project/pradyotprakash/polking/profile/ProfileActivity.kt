package com.project.pradyotprakash.polking.profile

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import com.project.pradyotprakash.polking.R
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Make full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        setContentView(R.layout.activity_profile)

        initilise()
    }

    private fun initilise() {
        options_tv.setOnClickListener {
            optionList_cl.visibility = View.VISIBLE
        }

        iv_close.setOnClickListener {
            optionList_cl.visibility = View.GONE
        }
    }
}

package com.project.pradyotprakash.polking.signin

import android.annotation.SuppressLint
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.project.pradyotprakash.polking.R
import com.project.pradyotprakash.polking.utility.openActivity
import com.project.pradyotprakash.polking.verifyOTP.VerifyOTPActivity
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_sign_in.*
import javax.inject.Inject

class SignInActivity : AppCompatActivity(), SignInView {

    @Inject
    lateinit var presenter: SignInPresenter
    private var phoneNumber: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        initialize()
    }

    private fun initialize() {
        presenter.start()

        zeroDial.setOnClickListener {
            changePhoneNumber("0")
        }

        oneDial.setOnClickListener {
            changePhoneNumber("1")
        }

        twoDial.setOnClickListener {
            changePhoneNumber("2")
        }

        threeDial.setOnClickListener {
            changePhoneNumber("3")
        }

        fourDial.setOnClickListener {
            changePhoneNumber("4")
        }

        fiveDial.setOnClickListener {
            changePhoneNumber("5")
        }

        sixDial.setOnClickListener {
            changePhoneNumber("6")
        }

        sevenDial.setOnClickListener {
            changePhoneNumber("7")
        }

        eightDial.setOnClickListener {
            changePhoneNumber("8")
        }

        nineDial.setOnClickListener {
            changePhoneNumber("9")
        }

        backSpaceTv.setOnClickListener {
            var phoneNum = otpTv.text.toString()
            if (phoneNum.isNotEmpty()) {
                phoneNum = phoneNum.substring(0, phoneNum.length - 1)
            }
            otpTv.setText(phoneNum)
        }

        continueTv.setOnClickListener {
            openActivity(VerifyOTPActivity::class.java)
        }

        googleSignInCl.setOnClickListener {

        }
    }

    @SuppressLint("SetTextI18n")
    private fun changePhoneNumber(num: String) {
        otpTv.setText("${otpTv.text}$num")
    }

    override fun showLoading() {

    }

    override fun hideLoading() {

    }

    override fun stopAct() {

    }
}

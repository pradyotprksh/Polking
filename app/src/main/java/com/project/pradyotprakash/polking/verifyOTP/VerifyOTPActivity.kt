package com.project.pradyotprakash.polking.verifyOTP

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.Window
import android.view.WindowManager
import com.google.firebase.auth.PhoneAuthProvider
import com.project.pradyotprakash.polking.R
import com.project.pradyotprakash.polking.message.ShowMessage
import com.project.pradyotprakash.polking.utility.InternetActivity
import com.project.pradyotprakash.polking.utility.logd
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_verify_otp.*
import javax.inject.Inject

class VerifyOTPActivity : InternetActivity(), VerifyOTPView {

    @Inject
    lateinit var presenter: VerifyOTPPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)

        // Make full screen
        // set theme for notch if sdk is P
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            )
            window.decorView.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        } else {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        setContentView(R.layout.activity_verify_otp)

        logd(getString(R.string.create))
        initialize()
    }

    private fun initialize() {
        presenter.start()

        callPhoneNumberValidation()

        setOnClickListners()

        showLoading()
    }

    private fun setOnClickListners() {
        saveTv.setOnClickListener {
            if (progressBar2.visibility == View.GONE) {
                if (presenter.getStoredVerificationId() != null) {
                    val code =
                        otp1Tv.text.toString() + "" + otp2Tv.text.toString() + "" +
                                otp3Tv.text.toString() + "" + otp4Tv.text.toString() + "" +
                                otp5Tv.text.toString() + "" + otp6Tv.text.toString()
                    val credential =
                        PhoneAuthProvider.getCredential(presenter.getStoredVerificationId()!!, code)
                    presenter.signInWithPhoneAuthCredential(credential)
                }
            }
        }

        zeroDial.setOnClickListener {
            enterOTP("0")
        }

        oneDial.setOnClickListener {
            enterOTP("1")
        }

        twoDial.setOnClickListener {
            enterOTP("2")
        }

        threeDial.setOnClickListener {
            enterOTP("3")
        }

        fourDial.setOnClickListener {
            enterOTP("4")
        }

        fiveDial.setOnClickListener {
            enterOTP("5")
        }

        sixDial.setOnClickListener {
            enterOTP("6")
        }

        sevenDial.setOnClickListener {
            enterOTP("7")
        }

        eightDial.setOnClickListener {
            enterOTP("8")
        }

        nineDial.setOnClickListener {
            enterOTP("9")
        }

        backSpaceTv.setOnClickListener {
            when {
                otp6Tv.text.toString() != "" -> {
                    otp6Tv.setText("")
                }
                otp5Tv.text.toString() != "" -> {
                    otp5Tv.setText("")
                }
                otp4Tv.text.toString() != "" -> {
                    otp4Tv.setText("")
                }
                otp3Tv.text.toString() != "" -> {
                    otp3Tv.setText("")
                }
                otp2Tv.text.toString() != "" -> {
                    otp2Tv.setText("")
                }
                otp1Tv.text.toString() != "" -> {
                    otp1Tv.setText("")
                }
            }
        }

        cancleTv.setOnClickListener {
            if (progressBar2.visibility == View.GONE) {
                finish()
            }
        }
    }

    private fun callPhoneNumberValidation() {
        if (intent != null && intent.getBundleExtra("phoneBundle") != null) {
            val phoneNumber = intent.getBundleExtra("phoneBundle")
                .getString("phoneNumber")!!
            if (phoneNumber.isNotEmpty()) {
                presenter.otpCallBacks(phoneNumber)
            }
        }
    }

    private fun enterOTP(s: String) {
        when {
            otp1Tv.text.toString() == "" -> {
                otp1Tv.setText(s)
            }
            otp2Tv.text.toString() == "" -> {
                otp2Tv.setText(s)
            }
            otp3Tv.text.toString() == "" -> {
                otp3Tv.setText(s)
            }
            otp4Tv.text.toString() == "" -> {
                otp4Tv.setText(s)
            }
            otp5Tv.text.toString() == "" -> {
                otp5Tv.setText(s)
            }
            otp6Tv.text.toString() == "" -> {
                otp6Tv.setText(s)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        logd("Resume")
    }

    override fun showLoading() {
        progressBar2.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        progressBar2.visibility = View.GONE
    }

    override fun stopAct() {
        finish()
    }

    override fun showMessage(message: String, type: Int) {
        messageBtmSheet = ShowMessage.newInstance()
        if (!messageBtmSheet.isAdded) {
            messageBtmSheet.show(supportFragmentManager, "btmSheet")
            messageBtmSheet.setMessage(message, type)
        } else {
            messageBtmSheet.dismiss()
            Handler().postDelayed({
                if (!messageBtmSheet.isAdded) {
                    messageBtmSheet.show(supportFragmentManager, "btmSheet")
                    messageBtmSheet.setMessage(message, type)
                }
            }, 1500)
        }
    }

}

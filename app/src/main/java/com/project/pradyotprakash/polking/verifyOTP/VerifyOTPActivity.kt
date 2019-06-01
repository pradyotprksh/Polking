package com.project.pradyotprakash.polking.verifyOTP

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.Window
import android.view.WindowManager
import com.google.firebase.auth.PhoneAuthProvider
import com.project.pradyotprakash.polking.R
import com.project.pradyotprakash.polking.utility.AppConstants
import com.project.pradyotprakash.polking.utility.logd
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_verify_otp.*
import javax.inject.Inject

class VerifyOTPActivity : AppCompatActivity(), VerifyOTPView {

    @Inject
    lateinit var presenter: VerifyOTPPresenter

    private var count = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)

        // Make full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        setContentView(R.layout.activity_verify_otp)

        logd(getString(R.string.create))
        initialize()
    }

    private fun initialize() {
        presenter.start()

        callPhoneNumberValidation()

        saveTv.setOnClickListener {
            if (presenter.getStoredVerificationId()!=null) {
                val code = otp1Tv.text.toString() + "" + otp2Tv.text.toString() + "" + otp3Tv.text.toString() + "" + otp4Tv.text.toString() + "" + otp5Tv.text.toString() + "" + otp6Tv.text.toString()
                val credential = PhoneAuthProvider.getCredential(presenter.getStoredVerificationId()!!, code)
                presenter.signInWithPhoneAuthCredential(credential)
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

        closeIv.setOnClickListener {
            finish()
        }

    }

    private fun callPhoneNumberValidation() {
        if (presenter.checkForSMSPermission()) {
            if (intent!=null && intent.getBundleExtra("phoneBundle")!=null) {
                val phoneNumber = intent.getBundleExtra("phoneBundle").getString("phoneNumber")!!
                if (phoneNumber.isNotEmpty()) {
                    presenter.otpCallBacks(phoneNumber)
                }
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
        presenter.checkForSMSPermission()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            AppConstants.PERMISSIONS_REQUEST_READ_SMS -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.READ_SMS
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        callPhoneNumberValidation()
                    } else {
                        presenter.checkForSMSPermission()
                    }
                } else {
                    presenter.checkForSMSPermission()
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (!shouldShowRequestPermissionRationale(Manifest.permission.READ_SMS)) {
                            count++
                            if (count > 1) {
                                showMessageOKCancel(
                                    getString(R.string.readSMSPermission)
                                ) { _, _ ->
                                    val intent = Intent()
                                    intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                                    val uri = Uri.fromParts("package", packageName, null)
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

    private fun showMessageOKCancel(message: String, okListener: (Any, Any) -> Unit) {
        AlertDialog.Builder(this)
            .setMessage(message).setPositiveButton(getString(R.string.ok_string), okListener)
            .setNegativeButton(getString(R.string.cancel_string), null).create().show()
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

    }

}

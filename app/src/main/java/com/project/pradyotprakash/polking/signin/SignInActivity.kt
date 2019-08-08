package com.project.pradyotprakash.polking.signin

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.project.pradyotprakash.polking.R
import com.project.pradyotprakash.polking.utility.AppConstants.Companion.PERMISSIONS_REQUEST_CONTACT
import com.project.pradyotprakash.polking.utility.AppConstants.Companion.RC_SIGN_IN
import com.project.pradyotprakash.polking.utility.isValidPhone
import com.project.pradyotprakash.polking.utility.logd
import com.project.pradyotprakash.polking.utility.openActivity
import com.project.pradyotprakash.polking.verifyOTP.VerifyOTPActivity
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_sign_in.*
import javax.inject.Inject

class SignInActivity : AppCompatActivity(), SignInView {

    @Inject
    lateinit var presenter: SignInPresenter
    private var count = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)

        // Make full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        setContentView(R.layout.activity_sign_in)
        logd(getString(R.string.create))
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

        phoneEt.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {
                if (s.isNotEmpty() && s.toString().isValidPhone()) {
                    saveTv.visibility = View.VISIBLE
                } else {
                    saveTv.visibility = View.GONE
                }
            }
        })

        backSpaceTv.setOnClickListener {
            var phoneNum = phoneEt.text.toString()
            if (phoneNum.isNotEmpty()) {
                phoneNum = phoneNum.substring(0, phoneNum.length - 1)
            }
            phoneEt.setText(phoneNum)
        }

        saveTv.setOnClickListener {
            val phoneNum = phoneEt.text.toString()
            if (phoneNum.isNotEmpty() && phoneNum.isValidPhone()) {
                openOTPScreen()
            } else {
                showMessage(getString(R.string.enter_valid_phone), 1)
            }
        }

        closeIv.setOnClickListener {
            finish()
        }

        googleSignInCl.setOnClickListener {
            if (presenter.checkContactForPermission()) {
                presenter.askForGoogleSignIn()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSIONS_REQUEST_CONTACT -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.READ_SMS
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        presenter.askForGoogleSignIn()
                    } else {
                        presenter.checkContactForPermission()
                    }
                } else {
                    presenter.checkContactForPermission()
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (!shouldShowRequestPermissionRationale(Manifest.permission.READ_SMS)) {
                            count++
                            if (count > 1) {
                                showMessageOKCancel(
                                    getString(R.string.contactPermission)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                RC_SIGN_IN -> {
                    val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                    try {
                        val account = task.getResult(ApiException::class.java)
                        presenter.firebaseAuthWithGoogle(account!!)
                    } catch (e: ApiException) {
                        showMessage("Google sign in failed", 1)
                    }
                }
            }
        }
    }

    private fun showMessageOKCancel(message: String, okListener: (Any, Any) -> Unit) {
        AlertDialog.Builder(this)
            .setMessage(message).setPositiveButton(getString(R.string.ok_string), okListener)
            .setNegativeButton(getString(R.string.cancel_string), null).create().show()
    }

    private fun openOTPScreen() {
        val bundle = Bundle()
        bundle.putString("phoneNumber", "+91" + phoneEt.text.toString())
        openActivity(VerifyOTPActivity::class.java, "phoneBundle", bundle)
        finish()
    }

    @SuppressLint("SetTextI18n")
    private fun changePhoneNumber(num: String) {
        phoneEt.setText("${phoneEt.text}$num")
    }

    override fun showLoading() {
        progressBar3.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        progressBar3.visibility = View.GONE
    }

    override fun stopAct() {
        finish()
    }

    override fun showMessage(message: String, type: Int) {

    }

}

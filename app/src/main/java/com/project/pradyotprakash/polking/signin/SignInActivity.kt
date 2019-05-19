package com.project.pradyotprakash.polking.signin

import android.annotation.SuppressLint
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.Window
import android.view.WindowManager
import com.project.pradyotprakash.polking.R
import com.project.pradyotprakash.polking.utility.isValidPhone
import com.project.pradyotprakash.polking.utility.logd
import com.project.pradyotprakash.polking.utility.openActivity
import com.project.pradyotprakash.polking.verifyOTP.VerifyOTPActivity
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_sign_in.*
import kotlinx.android.synthetic.main.activity_sign_in.backSpaceTv
import kotlinx.android.synthetic.main.activity_sign_in.closeIv
import kotlinx.android.synthetic.main.activity_sign_in.saveTv
import kotlinx.android.synthetic.main.activity_sign_in.eightDial
import kotlinx.android.synthetic.main.activity_sign_in.fiveDial
import kotlinx.android.synthetic.main.activity_sign_in.fourDial
import kotlinx.android.synthetic.main.activity_sign_in.nineDial
import kotlinx.android.synthetic.main.activity_sign_in.oneDial
import kotlinx.android.synthetic.main.activity_sign_in.sevenDial
import kotlinx.android.synthetic.main.activity_sign_in.sixDial
import kotlinx.android.synthetic.main.activity_sign_in.threeDial
import kotlinx.android.synthetic.main.activity_sign_in.twoDial
import kotlinx.android.synthetic.main.activity_sign_in.zeroDial
import javax.inject.Inject

class SignInActivity : AppCompatActivity(), SignInView {

    @Inject
    lateinit var presenter: SignInPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)

        // Make full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        setContentView(R.layout.activity_sign_in)
        logd("Create")
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
                showMessage("Enter a valid phone number. This is the lest you can do for us.", 1)
            }
        }

        closeIv.setOnClickListener {
            finish()
        }

        googleSignInCl.setOnClickListener {

        }
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

    }

    override fun showMessage(message: String, type: Int) {

    }

}

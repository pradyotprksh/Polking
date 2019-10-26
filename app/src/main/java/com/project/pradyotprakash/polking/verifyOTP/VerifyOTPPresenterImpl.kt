package com.project.pradyotprakash.polking.verifyOTP

import android.app.Activity
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.project.pradyotprakash.polking.R
import com.skydoves.whatif.whatIfNotNull
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class VerifyOTPPresenterImpl @Inject constructor() : VerifyOTPPresenter {

    lateinit var mContext: Activity
    @Inject
    lateinit var mView: VerifyOTPView

    lateinit var mCallbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    private var storedVerificationId: String? = null
    private var resendToken: PhoneAuthProvider.ForceResendingToken? = null
    private lateinit var mAuth: FirebaseAuth

    @Inject
    internal fun VerifyOTPPresenterImpl(activity: Activity) {
        mContext = activity
        mAuth = FirebaseAuth.getInstance()
    }

    override fun start() {

    }

    override fun stop() {

    }

    override fun getStoredVerificationId(): String? {
        return storedVerificationId
    }

    override fun isNetworkAvailable(): Boolean {
        return true
    }

    override fun otpCallBacks(phoneNumber: String) {
        mCallbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                if (e is FirebaseAuthInvalidCredentialsException) {
                    mView.showMessage(mContext.getString(R.string.invalid_creds), 1)
                } else if (e is FirebaseTooManyRequestsException) {
                    mView.showMessage(mContext.getString(R.string.too_many_req), 1)
                }
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                storedVerificationId = verificationId
                resendToken = token
            }

            override fun onCodeAutoRetrievalTimeOut(p0: String) {
                mView.showMessage(mContext.getString(R.string.time_out), 1)
            }
        }

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            phoneNumber,
            60,
            TimeUnit.SECONDS,
            mContext,
            mCallbacks)
    }

    override fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential?) {
        mView.showLoading()
        credential.whatIfNotNull {
            mAuth.signInWithCredential(credential!!).addOnCompleteListener(mContext) { task ->
                if (task.isSuccessful) {
                    mView.hideLoading()
                    mView.stopAct()
                } else {
                    mView.hideLoading()
                    mView.showMessage(mContext.getString(R.string.something_went_wring_oops), 1)
                }
            }.addOnFailureListener { exception ->
                mView.hideLoading()
                mView.showMessage("Oops Something Went Wrong. ${exception.localizedMessage}", 1)
            }
        }
    }
}
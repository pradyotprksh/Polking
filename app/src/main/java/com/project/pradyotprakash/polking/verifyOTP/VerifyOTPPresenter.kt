package com.project.pradyotprakash.polking.verifyOTP

import com.google.firebase.auth.PhoneAuthCredential
import com.project.pradyotprakash.polking.BasePresenter

interface VerifyOTPPresenter: BasePresenter {

    // otp call backs
    fun otpCallBacks(phoneNumber: String)

    // add user to firebase
    fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential?)

    // get verification id
    fun getStoredVerificationId(): String?
}
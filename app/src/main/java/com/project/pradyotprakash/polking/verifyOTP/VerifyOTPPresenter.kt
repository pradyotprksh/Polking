package com.project.pradyotprakash.polking.verifyOTP

import com.google.firebase.auth.PhoneAuthCredential
import com.project.pradyotprakash.polking.BasePresenter

interface VerifyOTPPresenter: BasePresenter {

    // check for sms read permission
    fun checkForSMSPermission(): Boolean

    // otp call backs
    fun otpCallBacks(phoneNumber: String)

    // add user to firebase
    fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential?)

    // get verification id
    fun getStoredVerificationId(): String?
}
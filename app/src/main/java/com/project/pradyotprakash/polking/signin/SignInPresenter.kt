package com.project.pradyotprakash.polking.signin

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.project.pradyotprakash.polking.BasePresenter

interface SignInPresenter: BasePresenter {

    // ask for google sign method
    fun askForGoogleSignIn()

    // add the data from the google sign to firestore
    fun updateUi(account: GoogleSignInAccount)

    // sign in to firebase
    fun firebaseAuthWithGoogle(account: GoogleSignInAccount)
}
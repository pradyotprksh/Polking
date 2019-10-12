package com.project.pradyotprakash.polking.signin

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.project.pradyotprakash.polking.R
import com.project.pradyotprakash.polking.utility.AppConstants
import javax.inject.Inject

class SignInPresenterImpl @Inject constructor() : SignInPresenter {

    lateinit var mContext: Activity
    @Inject lateinit var mView: SignInView
    lateinit var googleSignInClient: GoogleSignInClient
    lateinit var gso: GoogleSignInOptions
    private lateinit var mAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var getfirestore: FirebaseFirestore

    @Inject
    internal fun SignInPresenterImpl(activity: Activity) {
        mContext = activity
        firestore = FirebaseFirestore.getInstance()
        getfirestore = FirebaseFirestore.getInstance()
        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(mContext.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(mContext, gso)
        mAuth = FirebaseAuth.getInstance()
    }

    override fun checkContactForPermission(): Boolean {
        return if (ContextCompat.checkSelfPermission(
                mContext,
                Manifest.permission.READ_CONTACTS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(mContext, Manifest.permission.READ_CONTACTS)) {
                ActivityCompat.requestPermissions(
                    mContext,
                    arrayOf(Manifest.permission.READ_CONTACTS),
                    AppConstants.PERMISSIONS_REQUEST_CONTACT
                )
            } else {
                ActivityCompat.requestPermissions(
                    mContext,
                    arrayOf(Manifest.permission.READ_CONTACTS),
                    AppConstants.PERMISSIONS_REQUEST_CONTACT
                )
            }
            false
        } else {
            true
        }
    }

    override fun askForGoogleSignIn() {
        val signInIntent = googleSignInClient.signInIntent
        mContext.startActivityForResult(signInIntent, AppConstants.RC_SIGN_IN)
    }

    override fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        mView.showLoading()
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        mAuth.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                updateUi(account)
            } else {
                mView.hideLoading()
                mView.showMessage(
                    "Something Went Wrong.",
                    1
                )
            }
        }.addOnFailureListener { exception ->
            mView.showMessage(
                "Something Went Wrong. ${exception.localizedMessage}",
                1
            )
            mView.hideLoading()
        }
    }

    override fun updateUi(account: GoogleSignInAccount) {
        if (mAuth.currentUser != null) {
            getfirestore.collection("users")
                .document(mAuth.currentUser!!.uid).get().addOnSuccessListener { result ->
                    if (result.exists()) {
                        mView.hideLoading()
                        mView.stopAct()
                    } else {
                        addDataNewUser(account)
                    }
                }.addOnFailureListener { exception ->
                    mView.showMessage(
                        "Something Went Wrong. ${exception.localizedMessage}",
                        1
                    )
                    mView.hideLoading()
                }.addOnCanceledListener {
                    mView.showMessage(mContext.getString(R.string.not_uploaded), 4)
                    mView.hideLoading()
                }
        }
    }

    private fun addDataNewUser(account: GoogleSignInAccount) {
        val userData = HashMap<String, Any>()
        userData["name"] = account.displayName!!
        userData["age"] = 18
        userData["birthDay"] = ""
        userData["gender"] = "-1"
        userData["questions"] = "0"
        userData["friends"] = "0"
        userData["best_friends"] = "0"
        userData["bg_option"] = "bg_one"
        firestore.collection("users")
            .document(mAuth.currentUser!!.uid)
            .set(userData).addOnSuccessListener {
                mView.hideLoading()
                mView.stopAct()
            }.addOnFailureListener { exception ->
                mView.showMessage(
                    "Something Went Wrong. ${exception.localizedMessage}",
                    1
                )
                mView.hideLoading()
            }.addOnCanceledListener {
                mView.showMessage(mContext.getString(R.string.not_uploaded), 4)
                mView.hideLoading()
            }
    }

    override fun start() {

    }

    override fun stop() {

    }

    override fun isNetworkAvailable(): Boolean {
        return true
    }

}
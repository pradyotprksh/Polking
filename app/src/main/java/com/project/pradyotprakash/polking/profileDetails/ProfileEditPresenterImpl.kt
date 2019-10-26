package com.project.pradyotprakash.polking.profileDetails

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.project.pradyotprakash.polking.utility.AppConstants
import javax.inject.Inject

class ProfileEditPresenterImpl @Inject constructor() : ProfileEditPresenter {

    lateinit var mContext: Activity
    lateinit var mView: ProfileEditBtmSheet
    private lateinit var mAuth: FirebaseAuth
    private var currentUser: FirebaseUser? = null
    private lateinit var dataBase: FirebaseFirestore

    @Inject
    internal fun ProfileEditPresenterImpl(activity: Activity) {
        mContext = activity
        mAuth = FirebaseAuth.getInstance()
        currentUser = mAuth.currentUser
        dataBase = FirebaseFirestore.getInstance()
    }

    override fun attachView(view: ProfileEditBtmSheet) {
        this.mView = view
    }

    override fun start() {

    }

    override fun stop() {

    }

    override fun isNetworkAvailable(): Boolean {
        return true
    }

    override fun checkReadPermission() : Boolean {
        return if (ContextCompat.checkSelfPermission(mContext,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    mContext,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            ) {
                ActivityCompat.requestPermissions(
                    mContext,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    AppConstants.PERMISSIONS_REQUEST_READ_STORAGE
                )
            } else {
                ActivityCompat.requestPermissions(
                    mContext,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    AppConstants.PERMISSIONS_REQUEST_READ_STORAGE
                )
            }
            false
        } else {
            true
        }
    }

    override fun checkWritePermission() : Boolean {
        return if (ContextCompat.checkSelfPermission(mContext,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    mContext,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            ) {
                ActivityCompat.requestPermissions(
                    mContext,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    AppConstants.PERMISSIONS_REQUEST_WRITE_STORAGE
                )
            } else {
                ActivityCompat.requestPermissions(
                    mContext,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    AppConstants.PERMISSIONS_REQUEST_WRITE_STORAGE
                )
            }
            false
        } else {
            true
        }
    }


}
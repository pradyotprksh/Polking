package com.project.pradyotprakash.polking.utility

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.project.pradyotprakash.polking.BuildConfig

// Start Activity Extension
fun <T> Context.openActivity(it: Class<T>, bundleKey: String? = null, bundle: Bundle? = null) {
    val intent = Intent(this, it)
    intent.putExtra(bundleKey, bundle)
    startActivity(intent)
}

// Log Extension
fun Activity.logd(message: String){
    if (BuildConfig.DEBUG) Log.d(this::class.java.simpleName, message)
}
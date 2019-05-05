package com.project.pradyotprakash.polking.utility

import android.content.Context
import android.content.Intent
import android.os.Bundle

fun <T> Context.openActivity(it: Class<T>, bundleKey: String? = null, bundle: Bundle? = null) {
    val intent = Intent(this, it)
    intent.putExtra(bundleKey, bundle)
    startActivity(intent)
}
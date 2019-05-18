package com.project.pradyotprakash.polking.utility

import android.util.Patterns

// check for phone validation
fun String.isValidPhone(): Boolean
        = this.isNotEmpty() && Patterns.PHONE.matcher(this).matches()
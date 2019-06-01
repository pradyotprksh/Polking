package com.project.pradyotprakash.polking.utility

import android.annotation.SuppressLint
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class Utility {

    @SuppressLint("SimpleDateFormat")
    fun getAge(dobString: String): Int {

        var date: Date? = null
        val sdf = SimpleDateFormat("dd/MM/yyyy")
        try {
            date = sdf.parse(dobString)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        if (date == null) return 0

        val dob = Calendar.getInstance()
        val today = Calendar.getInstance()

        dob.time = date

        val year = dob.get(Calendar.YEAR)
        val month = dob.get(Calendar.MONTH)
        val day = dob.get(Calendar.DAY_OF_MONTH)

        dob.set(year, month + 1, day)

        var age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR)

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
            age--
        }
        return age
    }

    fun getDayMessage(): String {
        val c = Calendar.getInstance()
        return when (c.get(Calendar.HOUR_OF_DAY)) {
            in 0..11 -> "Good Morning"
            in 12..15 -> "Good Afternoon"
            in 16..20 -> "Good Evening"
            in 21..23 -> "Good Night"
            else -> "Hello"
        }
    }

}
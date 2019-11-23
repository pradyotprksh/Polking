package com.project.pradyotprakash.polking.utility

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.Animation
import android.view.animation.Transformation
import android.view.animation.TranslateAnimation
import android.view.inputmethod.InputMethodManager
import androidx.core.view.animation.PathInterpolatorCompat
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class Utility {

    @SuppressLint("SimpleDateFormat")
    internal fun getAge(dobString: String): Int {

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

    internal fun getDayMessage(): String {
        val c = Calendar.getInstance()
        return when (c.get(Calendar.HOUR_OF_DAY)) {
            in 0..11 -> "Good Morning"
            in 12..15 -> "Good Afternoon"
            in 16..20 -> "Good Evening"
            in 21..23 -> "Good Night"
            else -> "Hello"
        }
    }

    internal fun outToRightAnimation(): Animation {
        val ottoRight = TranslateAnimation(
            Animation.RELATIVE_TO_PARENT, 0.0f,
            Animation.RELATIVE_TO_PARENT, +1.0f,
            Animation.RELATIVE_TO_PARENT, 0.0f,
            Animation.RELATIVE_TO_PARENT, 0.0f
        )
        ottoRight.duration = 300
        ottoRight.interpolator = AccelerateInterpolator()
        return ottoRight
    }

    internal fun inFromRightAnimation(): Animation {

        val inFromRight = TranslateAnimation(
            Animation.RELATIVE_TO_PARENT, +1.0f,
            Animation.RELATIVE_TO_PARENT, 0.0f,
            Animation.RELATIVE_TO_PARENT, 0.0f,
            Animation.RELATIVE_TO_PARENT, 0.0f
        )
        inFromRight.duration = 300
        inFromRight.interpolator = AccelerateInterpolator()
        return inFromRight
    }

    internal fun inFromLeftAnimation(): Animation {
        val inFromLeft = TranslateAnimation(
            Animation.RELATIVE_TO_PARENT, -1.0f,
            Animation.RELATIVE_TO_PARENT, 0.0f,
            Animation.RELATIVE_TO_PARENT, 0.0f,
            Animation.RELATIVE_TO_PARENT, 0.0f
        )
        inFromLeft.duration = 300
        inFromLeft.interpolator = AccelerateInterpolator()
        return inFromLeft
    }

    internal fun outToLeftAnimation(): Animation {
        val outtoLeft = TranslateAnimation(
            Animation.RELATIVE_TO_PARENT, 0.0f,
            Animation.RELATIVE_TO_PARENT, -1.0f,
            Animation.RELATIVE_TO_PARENT, 0.0f,
            Animation.RELATIVE_TO_PARENT, 0.0f
        )
        outtoLeft.duration = 300
        outtoLeft.interpolator = AccelerateInterpolator()
        return outtoLeft
    }

    internal fun inFromDownAnimation(): Animation {
        val inFromDown = TranslateAnimation(
            Animation.RELATIVE_TO_PARENT, 0.0f,
            Animation.RELATIVE_TO_PARENT, 0.0f,
            Animation.RELATIVE_TO_PARENT, 1.0f,
            Animation.RELATIVE_TO_PARENT, 0.0f
        )
        inFromDown.duration = 300
        inFromDown.interpolator = AccelerateInterpolator()
        return inFromDown
    }

    internal fun outToDownAnimation(): Animation {
        val outtoDown = TranslateAnimation(
            Animation.RELATIVE_TO_PARENT, 0.0f,
            Animation.RELATIVE_TO_PARENT, 0.0f,
            Animation.RELATIVE_TO_PARENT, 0.0f,
            Animation.RELATIVE_TO_PARENT, 1.0f
        )
        outtoDown.duration = 300
        outtoDown.interpolator = AccelerateInterpolator()
        return outtoDown
    }

    internal fun hideSoftKeyboard(view: View) {
        try {
            val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        } catch (e: Exception) {

        }
    }

    internal fun showSoftKeyboard(view: View) {
        try {
            val inputMethodManager = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.toggleSoftInputFromWindow(view.applicationWindowToken, InputMethodManager.SHOW_FORCED, 0)
        } catch (e: Exception) {

        }
    }

    internal fun expandCollapse(view: View) {
        val expand = view.visibility == View.GONE
        val easeInOutQuart = PathInterpolatorCompat.create(0.77f, 0f, 0.175f, 1f)
        view.measure(
            View.MeasureSpec.makeMeasureSpec((view.parent as View).width, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )
        val height = view.measuredHeight
        val duration = (height / view.context.resources.displayMetrics.density).toInt()
        val animation = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                if (expand) {
                    view.layoutParams.height = 1
                    view.visibility = View.VISIBLE
                    if (interpolatedTime == 1f) {
                        view.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
                    } else {
                        view.layoutParams.height = (height * interpolatedTime).toInt()
                    }
                    view.requestLayout()
                } else {
                    if (interpolatedTime == 1f) {
                        view.visibility = View.GONE
                    } else {
                        view.layoutParams.height = height - (height * interpolatedTime).toInt()
                        view.requestLayout()
                    }
                }
            }

            override fun willChangeBounds(): Boolean {
                return true
            }
        }
        animation.interpolator = easeInOutQuart
        animation.duration = duration.toLong()
        view.startAnimation(animation)
    }

    internal fun roundOffDecimal(number: Double): Double? {
        val df = DecimalFormat("#.##")
        df.roundingMode = RoundingMode.CEILING
        return df.format(number).toDouble()
    }

}
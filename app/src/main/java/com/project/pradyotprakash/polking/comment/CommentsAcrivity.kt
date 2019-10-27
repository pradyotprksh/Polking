package com.project.pradyotprakash.polking.comment

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.Window
import android.view.WindowManager
import com.project.pradyotprakash.polking.R
import com.project.pradyotprakash.polking.message.ShowMessage
import com.project.pradyotprakash.polking.profile.questionStats.QuestionStatistics
import com.project.pradyotprakash.polking.signin.SignInActivity
import com.project.pradyotprakash.polking.utility.InternetActivity
import com.project.pradyotprakash.polking.utility.Utility
import com.project.pradyotprakash.polking.utility.logd
import com.project.pradyotprakash.polking.utility.openActivity
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_comments_acrivity.*
import javax.inject.Inject

class CommentsAcrivity : InternetActivity(), CommentsActivityView {

    @Inject
    lateinit var presenter: CommentsActivityPresenter
    lateinit var questionStatistics: QuestionStatistics

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)

        // Make full screen
        // set theme for notch if sdk is P
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            )
            window.decorView.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        } else {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        setContentView(R.layout.activity_comments_acrivity)

        logd(getString(R.string.create))

        initView()
    }

    private fun initView() {
        showKeyboard()

        initVariables()

        setOnClickListner()
    }

    private fun initVariables() {
        questionStatistics = QuestionStatistics.newInstance()
    }

    private fun setOnClickListner() {
        question_info.setOnClickListener {
            presenter.showStats("YspdXlYiylhJtaJVLo2h")
        }

        close_iv.setOnClickListener {
            onBackPressed()
        }
    }

    private fun showKeyboard() {
        commentVal_rt.requestFocus()
        Utility().showSoftKeyboard(commentVal_rt)
    }

    override fun showQuestionStats(docId: String) {
        runOnUiThread {
            if (!isFinishing) {
                if (!questionStatistics.isAdded) {
                    questionStatistics.show(supportFragmentManager, "btmSheet")
                    questionStatistics.setQuestionDocId(docId)
                } else {
                    questionStatistics.dismiss()
                    if (!questionStatistics.isAdded) {
                        questionStatistics.show(supportFragmentManager, "btmSheet")
                        questionStatistics.setQuestionDocId(docId)
                    }
                }
            }
        }
    }

    override fun showLoading() {
        progressBar9.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        progressBar9.visibility = View.GONE
    }

    override fun onBackPressed() {
        super.onBackPressed()
        Utility().hideSoftKeyboard(commentVal_rt)
        overridePendingTransition(R.anim.nothing, R.anim.slide_out_bottom)
        finish()
    }

    override fun stopAct() {
        onBackPressed()
    }

    override fun openLoginAct() {
        openActivity(SignInActivity::class.java)
    }

    override fun showMessage(message: String, type: Int) {
        messageBtmSheet = ShowMessage.newInstance()
        if (!messageBtmSheet.isAdded) {
            messageBtmSheet.show(supportFragmentManager, "btmSheet")
            messageBtmSheet.setMessage(message, type)
        } else {
            messageBtmSheet.dismiss()
            Handler().postDelayed({
                if (!messageBtmSheet.isAdded) {
                    messageBtmSheet.show(supportFragmentManager, "btmSheet")
                    messageBtmSheet.setMessage(message, type)
                }
            }, 1500)
        }
    }

}

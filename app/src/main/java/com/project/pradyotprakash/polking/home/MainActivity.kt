package com.project.pradyotprakash.polking.home

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.project.pradyotprakash.polking.R
import com.project.pradyotprakash.polking.home.adapter.QuestionsAdapter
import com.project.pradyotprakash.polking.message.ShowMessage
import com.project.pradyotprakash.polking.otherProfileOptions.OtherProfileOptions
import com.project.pradyotprakash.polking.profile.ProfileActivity
import com.project.pradyotprakash.polking.profile.questionStats.QuestionStatistics
import com.project.pradyotprakash.polking.profileDetails.ProfileEditBtmSheet
import com.project.pradyotprakash.polking.signin.SignInActivity
import com.project.pradyotprakash.polking.utility.*
import com.theartofdev.edmodo.cropper.CropImage
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import javax.inject.Inject

class MainActivity : InternetActivity(), MainActivityView {

    @Inject
    lateinit var presenter: MainActivityPresenter
    lateinit var profileEditBtmSheet: ProfileEditBtmSheet
    lateinit var otherProfileOptions: OtherProfileOptions
    lateinit var questionStatistics: QuestionStatistics
    private var questionsAdapter: QuestionsAdapter? = null
    private val allQues = ArrayList<QuestionModel>()

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

        setContentView(R.layout.activity_main)

        logd(getString(R.string.create))
        initialize()
    }

    private fun initialize() {
        presenter.start()

        initVariables()

        setOnClickListners()

        textChangeListners()

        adapters()
    }

    private fun getQuestions() {
        presenter.getQuestions()
    }

    private fun adapters() {
        questionsAdapter = QuestionsAdapter(allQues, this, this)
        recentQ_rv.setHasFixedSize(true)
        recentQ_rv.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        recentQ_rv.adapter = questionsAdapter
    }

    private fun textChangeListners() {
        addQuestion_et.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s != null) {
                    if (s.length > 5) {
                        if (post_Tv.visibility == View.GONE) {
                            post_Tv.startAnimation(Utility().inFromRightAnimation())
                        }
                        post_Tv.visibility = View.VISIBLE
                    } else {
                        if (post_Tv.visibility != View.GONE) {
                            post_Tv.startAnimation(Utility().outToRightAnimation())
                            post_Tv.visibility = View.GONE
                        }
                    }
                } else {
                    if (post_Tv.visibility != View.GONE) {
                        post_Tv.startAnimation(Utility().outToRightAnimation())
                        post_Tv.visibility = View.GONE
                    }
                }
            }
        })
    }

    override fun setNotificationIcon(notificationCount: String) {
        if (notificationCount == "0") {
            notification_iv.visibility = View.GONE
        } else {
            notification_iv.visibility = View.VISIBLE
        }
    }

    private fun setOnClickListners() {
        user_iv.setOnClickListener {
            presenter.isLoggedIn()
        }

        post_Tv.setOnClickListener {
            presenter.uploadQuestion(addQuestion_et.text.toString())
        }
    }

    private fun initVariables() {
        profileEditBtmSheet = ProfileEditBtmSheet.newInstance()
        otherProfileOptions = OtherProfileOptions.newInstance()
        questionStatistics = QuestionStatistics.newInstance()
        profileEditBtmSheet.isCancelable = false
    }

    override fun onResume() {
        super.onResume()
        logd(getString(R.string.resume))
        presenter.addAuthStateListener()
        presenter.getProfileData()
        getQuestions()
    }

    override fun startProfileAct() {
        hideLoading()
        openActivity(ProfileActivity::class.java)
    }

    override fun startLogin() {
        hideLoading()
        openActivity(SignInActivity::class.java)
    }

    override fun showLoading() {
        progressBar.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        progressBar.visibility = View.GONE
    }

    override fun stopAct() {
        finish()
    }

    override fun showUploadedSuccess() {
        addQuestion_et.setText("")
        Utility().hideSoftKeyboard(addQuestion_et)
    }

    override fun setUserProfileImage(imageUrl: String?) {
        if (imageUrl != null) {
            imageProgressBar.visibility = View.VISIBLE
            Glide.with(this).load(imageUrl)
                .placeholder(R.drawable.ic_default_appcolor)
                .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    exception: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    imageProgressBar.visibility = View.GONE
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    imageProgressBar.visibility = View.GONE
                    return false
                }
            }).into(user_iv)
            user_iv.borderWidth = 2
            user_iv.borderColor = resources.getColor(R.color.colorPrimary)
        } else {
            showMessage(getString(R.string.something_went_wrong), 1)
        }
    }

    @SuppressLint("SetTextI18n")
    override fun setUserName(name: String) {
        addQuestion_et.hint = getString(R.string.enter_your_question) + " $name"
    }

    override fun openAddProfileDetails() {
        if (!profileEditBtmSheet.isAdded) {
            profileEditBtmSheet.show(supportFragmentManager, "btmSheet")
            profileEditBtmSheet.isCancelable = false
        }
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == Activity.RESULT_OK) {
                if (profileEditBtmSheet.isAdded) {
                    profileEditBtmSheet.getImageUri(result.uri)
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                showMessage(getString(R.string.went_wrong_image), 1)
            }
        }
    }

    override fun hideOptions() {

    }

    override fun showOptions() {

    }

    override fun loadQuestions(allQuestionList: ArrayList<QuestionModel>) {
        this.allQues.clear()
        if (allQuestionList.size > 0) {
            recentQ_rv.visibility = View.VISIBLE
            allQues.addAll(allQuestionList)
            questionsAdapter!!.notifyDataSetChanged()
        } else {
            recentQ_rv.visibility = View.GONE
        }
    }

    override fun onStop() {
        super.onStop()
        logd(getString(R.string.stop))
        presenter.removeListener()
    }

    fun openProfileDetails(askedBy: String) {
        if (!otherProfileOptions.isAdded) {
            otherProfileOptions.show(supportFragmentManager, "btmSheet")
            otherProfileOptions.setUserId(askedBy)
        }
    }

    override fun setVotes(voteType: Int, docId: String) {
        presenter.setVote(voteType, docId)
    }

    override fun showStats(docId: String) {
        showLoading()
        presenter.showStats(docId)
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

}

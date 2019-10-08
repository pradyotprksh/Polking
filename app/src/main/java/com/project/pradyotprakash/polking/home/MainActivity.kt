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
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.project.pradyotprakash.polking.R
import com.project.pradyotprakash.polking.home.adapter.QuestionsAdapter
import com.project.pradyotprakash.polking.otherProfileOptions.OtherProfileOptions
import com.project.pradyotprakash.polking.profile.ProfileActivity
import com.project.pradyotprakash.polking.profileDetails.ProfileEditBtmSheet
import com.project.pradyotprakash.polking.signin.SignInActivity
import com.project.pradyotprakash.polking.usersList.UserListBtmSheet
import com.project.pradyotprakash.polking.utility.QuestionModel
import com.project.pradyotprakash.polking.utility.Utility
import com.project.pradyotprakash.polking.utility.logd
import com.project.pradyotprakash.polking.utility.openActivity
import com.theartofdev.edmodo.cropper.CropImage
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import javax.inject.Inject

class MainActivity : AppCompatActivity(), MainActivityView {

    @Inject
    lateinit var presenter: MainActivityPresenter
    lateinit var profileEditBtmSheet: ProfileEditBtmSheet
    lateinit var otherProfileOptions: OtherProfileOptions
    lateinit var userListBtmSheet: UserListBtmSheet
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

        reloadPage()

        getQuestions()
    }

    private fun getQuestions() {
        presenter.getQuestions()
    }

    private fun reloadPage() {
        reloadQuestions.setOnClickListener {
            presenter.getQuestions()
            Handler().postDelayed({
                reloadQuestions.visibility = View.GONE
            }, 1000)
        }
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
        profileEditBtmSheet.isCancelable = false
        userListBtmSheet = UserListBtmSheet.newInstance()
    }

    override fun onResume() {
        super.onResume()
        logd(getString(R.string.resume))
        presenter.addAuthStateListener()
        presenter.getProfileData()
    }

    override fun showReloadOption() {
        if (reloadQuestions.visibility != View.VISIBLE) {
            reloadQuestions.visibility = View.VISIBLE
        }
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
    }

    override fun setUserProfileImage(imageUrl: String?) {
        if (imageUrl != null) {
            imageProgressBar.visibility = View.VISIBLE
            Glide.with(this).load(imageUrl).listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    exception: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    imageProgressBar.visibility = View.GONE
                    showMessage("Something Went Wrong. ${exception?.localizedMessage}", 1)
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
            profileEditBtmSheet.setCloseTitle(getString(R.string.can_t_close_this), true)
        }
    }

    override fun showMessage(message: String, type: Int) {

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

}

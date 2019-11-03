package com.project.pradyotprakash.polking.profile

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.MotionEvent
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.core.view.MotionEventCompat
import androidx.recyclerview.widget.LinearLayoutManager
import coil.Coil
import coil.api.load
import coil.request.Request
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.project.pradyotprakash.polking.R
import com.project.pradyotprakash.polking.comment.CommentsAcrivity
import com.project.pradyotprakash.polking.message.ShowMessage
import com.project.pradyotprakash.polking.otherProfileOptions.OtherProfileOptions
import com.project.pradyotprakash.polking.profile.aboutUs.AboutUsBottomSheet
import com.project.pradyotprakash.polking.profile.backgroundAdapter.BackgroundAdapter
import com.project.pradyotprakash.polking.profile.friends.FriendsBottomSheet
import com.project.pradyotprakash.polking.profile.notification.NotificationBottomSheet
import com.project.pradyotprakash.polking.profile.questions.QuestionsBottomSheet
import com.project.pradyotprakash.polking.profile.reviewUs.ReviewUsBtmSheet
import com.project.pradyotprakash.polking.profileDetails.ProfileEditBtmSheet
import com.project.pradyotprakash.polking.questionStats.QuestionStatistics
import com.project.pradyotprakash.polking.utility.*
import com.skydoves.whatif.whatIfNotNull
import com.theartofdev.edmodo.cropper.CropImage
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_profile.*
import java.util.*
import javax.inject.Inject

class ProfileActivity : InternetActivity(), ProfileActivityView {

    @Inject
    lateinit var presenter: ProfileActivityPresenter
    private var allBgAdapter: BackgroundAdapter? = null
    private val allBgList = ArrayList<BgModel>()
    private var bgDocId: String? = null
    private lateinit var profileEditBtmSheet: ProfileEditBtmSheet
    private lateinit var aboutUsBottomSheet: AboutUsBottomSheet
    private lateinit var reviewUsBottomSheet: ReviewUsBtmSheet
    private lateinit var questionBottomSheet: QuestionsBottomSheet
    private lateinit var friendsBottomSheet: FriendsBottomSheet
    private lateinit var notificationBottomSheet: NotificationBottomSheet
    private lateinit var otherProfileOptions: OtherProfileOptions
    private lateinit var questionStatistics: QuestionStatistics

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

        setContentView(R.layout.activity_profile)

        logd(getString(R.string.create))

        initialize()
    }

    private fun initialize() {

        setOnClickListners()

        initvariables()

        setAdapters()

        getIntentData()
    }

    private fun setBehaviorListner() {
        notificationBottomSheet.view.whatIfNotNull {
            val bottomSheetBehavior = BottomSheetBehavior.from(notificationBottomSheet.view)

            bottomSheetBehavior.setBottomSheetCallback(object :
                BottomSheetBehavior.BottomSheetCallback() {
                override fun onSlide(p0: View, slideOffset: Float) {

                }

                override fun onStateChanged(p0: View, newState: Int) {
                    if (BottomSheetBehavior.STATE_COLLAPSED == newState) {
                        presenter.callNotificationIsReadMethod()
                    }
                }
            })
        }
    }

    private fun getIntentData() {
        intent.whatIfNotNull {
            if (intent.getStringExtra("openBestFriend") != null
                && intent.getStringExtra("openBestFriend") != ""
            ) {
                if (intent.getStringExtra("openBestFriend") == "yes") {
                    openFriendSheet(2)
                }
            }
        }
    }

    private fun setOnClickListners() {
        options_tv.setOnClickListener {
            optionList_cl.visibility = View.VISIBLE
            optionList_cl.startAnimation(Utility().inFromDownAnimation())
        }

        profileEditChip.setOnClickListener {
            openAddProfileDetails()
        }

        edit_tv.setOnClickListener {
            openAddProfileDetails()
        }

        iv_close.setOnClickListener {
            optionList_cl.startAnimation(Utility().outToDownAnimation())
            optionList_cl.visibility = View.GONE
        }

        review_tv.setOnClickListener {
            openReviewUsSheet()
        }

        questionVal_tv.setOnClickListener {
            openQuestionSheet()
        }

        question_tv.setOnClickListener {
            openQuestionSheet()
        }

        yourPosts_tv.setOnClickListener {
            openQuestionSheet()
        }

        bestFrndVal_tv.setOnClickListener {
            openFriendSheet(2)
        }

        bestFrnd_tv.setOnClickListener {
            openFriendSheet(2)
        }

        yourBestFrnd_tview17.setOnClickListener {
            openFriendSheet(2)
        }

        friendsVal_tv.setOnClickListener {
            openFriendSheet(1)
        }

        friends_tv.setOnClickListener {
            openFriendSheet(1)
        }

        yourFrnd_tv.setOnClickListener {
            openFriendSheet(1)
        }

        faq_tv.setOnClickListener {
            showMessage("We are working on this.", 2)
        }

        aboutUs_tv.setOnClickListener {
            openAboutUsSheet()
        }

        back_tv.setOnClickListener {
            if (!bgDocId.isNullOrEmpty()) {
                presenter.changeBgId(bgDocId!!)
            }
        }

        notificationchip.setOnClickListener {
            openNotificationBtmSheet()
        }

        notification_tv.setOnClickListener {
            openNotificationBtmSheet()
        }
    }

    private fun initvariables() {
        profileEditBtmSheet = ProfileEditBtmSheet.newInstance()
        aboutUsBottomSheet = AboutUsBottomSheet.newInstance()
        reviewUsBottomSheet = ReviewUsBtmSheet.newInstance()
        questionBottomSheet = QuestionsBottomSheet.newInstance()
        friendsBottomSheet = FriendsBottomSheet.newInstance()
        notificationBottomSheet = NotificationBottomSheet.newInstance()
        otherProfileOptions = OtherProfileOptions.newInstance()
        questionStatistics = QuestionStatistics.newInstance()
    }

    private fun setAdapters() {
        allBgAdapter = BackgroundAdapter(allBgList, this, this)
        rv_bgOption.setHasFixedSize(true)
        rv_bgOption.layoutManager = LinearLayoutManager(
            this, LinearLayoutManager.HORIZONTAL, false
        )
        rv_bgOption.adapter = allBgAdapter
    }

    private fun openNotificationBtmSheet() {
        if (!notificationBottomSheet.isAdded) {
            notificationBottomSheet.show(supportFragmentManager, "btmSheet")
            setBehaviorListner()
            Handler().postDelayed({
                presenter.callNotificationIsReadMethod()
            }, 3500)
        }
    }

    private fun openFriendSheet(type: Int) {
        if (!friendsBottomSheet.isAdded) {
            if (type == 1) {
                if (friendsVal_tv.text != "0") {
                    friendsBottomSheet.show(supportFragmentManager, "btmSheet")
                    friendsBottomSheet.setType(type)
                } else {
                    showMessage(getString(R.string.no_friend), 2)
                }
            } else {
                if (bestFrndVal_tv.text != "0") {
                    friendsBottomSheet.show(supportFragmentManager, "btmSheet")
                    friendsBottomSheet.setType(type)
                } else {
                    showMessage(getString(R.string.no_best_friend), 2)
                }
            }
        }
    }

    private fun openQuestionSheet() {
        if (!questionBottomSheet.isAdded) {
            if (questionVal_tv.text != "0") {
                questionBottomSheet.show(supportFragmentManager, "btmSheet")
            } else {
                showMessage(getString(R.string.no_question_added), 2)
            }
        }
    }

    private fun openReviewUsSheet() {
        if (!reviewUsBottomSheet.isAdded) {
            reviewUsBottomSheet.show(supportFragmentManager, "btmSheet")
        }
    }

    private fun openAboutUsSheet() {
        if (!aboutUsBottomSheet.isAdded) {
            aboutUsBottomSheet.show(supportFragmentManager, "btmSheet")
        }
    }

    override fun onBackPressed() {
        if (optionList_cl.visibility == View.VISIBLE) {
            optionList_cl.startAnimation(Utility().outToDownAnimation())
            optionList_cl.visibility = View.GONE
            return
        } else {
            if (!bgDocId.isNullOrEmpty()) {
                presenter.changeBgId(bgDocId!!)
            }
            super.onBackPressed()
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {

        return if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_UP) {
//            openNotificationBtmSheet()
            true
        } else {
            super.onTouchEvent(event)
        }
    }

    override fun setUserProfileImage(imageUrl: String?) {
        imageUrl.whatIfNotNull(
            whatIf = {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    if (!this.isDestroyed) {
                        profile_iv.load(imageUrl,
                            Coil.loader(),
                            builder = {
                                this.listener(object : Request.Listener {
                                    override fun onError(data: Any, throwable: Throwable) {
                                        profile_iv.load(R.drawable.ic_default_appcolor)
                                    }

                                    override fun onSuccess(
                                        data: Any,
                                        source: coil.decode.DataSource
                                    ) {
                                        super.onSuccess(data, source)
                                        profile_iv.borderWidth = 2
                                        profile_iv.borderColor =
                                            resources.getColor(R.color.colorPrimary)
                                    }
                                })
                            })
                    }
                }
            },
            whatIfNot = {
                showMessage(getString(R.string.something_went_wrong), 1)
            }
        )
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

    @SuppressLint("SetTextI18n")
    override fun setUserName(name: String?) {
        if (!name.isNullOrEmpty()) {
            welcome_tv.text = "${Utility().getDayMessage()}, $name. Welcome to ${getString(
                R.string.app_name
            )}."
        }
    }

    override fun openAddProfileDetails() {
        if (!profileEditBtmSheet.isAdded) {
            profileEditBtmSheet.show(supportFragmentManager, "btmSheet")
            profileEditBtmSheet.isCancelable = true
        }
    }

    override fun setBgList(allBgList: ArrayList<BgModel>) {
        this.allBgList.clear()
        if (allBgList.size > 0) {
            bgOption_cl.visibility = View.VISIBLE
            this.allBgList.addAll(allBgList)
            allBgAdapter?.notifyDataSetChanged()
        } else {
            bgOption_cl.visibility = View.GONE
        }
    }

    override fun onResume() {
        super.onResume()
        logd(getString(R.string.resume))
        presenter.getUserData()
        presenter.getBackgroundImages()
    }

    override fun hideBackGroundOption() {
        bgOption_cl.visibility = View.GONE
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

    override fun setUserDetails(question: String?, friends: String?, bestFriends: String?) {
        if (!question.isNullOrEmpty()) {
            if (question == "0") {
                yourPosts_tv.visibility = View.GONE
            } else {
                yourPosts_tv.visibility = View.VISIBLE
            }
            questionVal_tv.text = question
        }
        if (!friends.isNullOrEmpty()) {
            if (friends == "0") {
                yourFrnd_tv.visibility = View.GONE
            } else {
                yourFrnd_tv.visibility = View.VISIBLE
            }
            friendsVal_tv.text = friends
        }
        if (!bestFriends.isNullOrEmpty()) {
            if (bestFriends == "0") {
                yourBestFrnd_tview17.visibility = View.GONE
            } else {
                yourBestFrnd_tview17.visibility = View.VISIBLE
            }
            bestFrndVal_tv.text = bestFriends
        }
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun setBgImage(imageUrl: String, docId: String) {
        if (!this.isDestroyed) {
            user_iv.load(imageUrl,
                Coil.loader(),
                builder = {
                    placeholder(R.drawable.pbg_two)
                })
            bgDocId = docId
        }
    }

    fun openProfileDetails(notificationMessageBy: String) {
        if (!otherProfileOptions.isAdded) {
            otherProfileOptions.show(supportFragmentManager, "btmSheet")
            otherProfileOptions.setUserId(notificationMessageBy)
        }
    }

    @SuppressLint("SetTextI18n")
    override fun setNotificationIcon(
        notificationCount: String,
        notificaitonMsg: String
    ) {
        if (notificationCount == "0") {
            notification_tv.visibility = View.GONE
        } else {
            notification_tv.visibility = View.VISIBLE
            notification_tv.text = "You have got $notificaitonMsg notification."
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
            if (!questionStatistics.isAdded) {
                questionStatistics.show(supportFragmentManager, "btmSheet")
                questionStatistics.setQuestionDocId(docId)
            }
        }
    }

    fun openCommentList(notificationQuestionId: String, notificationCommentId: String) {
        val bundle = Bundle()
        bundle.putString("questionId", notificationQuestionId)
        bundle.putString("notificationCommentId", notificationCommentId)
        openActivity(CommentsAcrivity::class.java, "questionId", bundle)
    }
}

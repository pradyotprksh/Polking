package com.project.pradyotprakash.polking.comment

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.*
import android.view.inputmethod.EditorInfo
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.Coil
import coil.api.load
import coil.request.Request
import com.project.pradyotprakash.polking.R
import com.project.pradyotprakash.polking.comment.adapter.MainCommentsAdpater
import com.project.pradyotprakash.polking.message.ShowMessage
import com.project.pradyotprakash.polking.otherProfileOptions.OtherProfileOptions
import com.project.pradyotprakash.polking.profile.ProfileActivity
import com.project.pradyotprakash.polking.questionStats.QuestionStatistics
import com.project.pradyotprakash.polking.signin.SignInActivity
import com.project.pradyotprakash.polking.utility.*
import com.skydoves.whatif.whatIfNotNull
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_comments_acrivity.*
import java.util.*
import javax.inject.Inject

class CommentsAcrivity : InternetActivity(), CommentsActivityView,
    PopupMenu.OnMenuItemClickListener {

    @Inject
    lateinit var presenter: CommentsActivityPresenter
    lateinit var questionStatistics: QuestionStatistics
    private var mainCommentAdapter: MainCommentsAdpater? = null
    private var questionId: String? = ""
    private var notificationCommentId: String? = ""
    lateinit var otherProfileOptions: OtherProfileOptions

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
        getIntentData()

        initVariables()

        adaptersInit()

        setOnClickListner()

        getComments()

        listnerForSendComment()

        getDataForUser()
    }

    private fun adaptersInit() {
        mainCommentAdapter = MainCommentsAdpater(this, this)
        comment_rv.setHasFixedSize(true)
        comment_rv.layoutManager = LinearLayoutManager(
            this,
            RecyclerView.VERTICAL, false
        )
        comment_rv.adapter = mainCommentAdapter
    }

    private fun getComments() {
        presenter.getComments(questionId, 1)
    }

    override fun loadAllComments(allCommentList: ArrayList<CommentModel>) {
        commentCount_tv.text = "${allCommentList.size}"
        if (allCommentList.size > 0) {
            mainCommentAdapter?.setQuestionId(questionId!!)
            mainCommentAdapter?.updateListItems(allCommentList)

            Handler().postDelayed({
                if (notificationCommentId != "") {
                    for (comment in allCommentList) {
                        if (comment.docId == notificationCommentId) {
                            comment_rv.scrollToPosition(allCommentList.indexOf(comment))
                        }
                    }
                }
            }, 600)
        }
    }

    override fun successfullyAddedComment() {
        commentVal_rt.setText("")
        Utility().hideSoftKeyboard(commentVal_rt)
    }

    private fun getDataForUser() {
        presenter.getProfileData()
    }

    private fun listnerForSendComment() {
        commentVal_rt.setOnEditorActionListener { v, actionId, event ->
            return@setOnEditorActionListener when (actionId) {
                EditorInfo.IME_ACTION_SEND -> {
                    if (commentVal_rt.text.toString().isNotEmpty()) {
                        showLoading()
                        presenter.addComment(commentVal_rt.text.toString(), questionId)
                    }
                    true
                }
                else -> false
            }
        }
    }

    private fun getIntentData() {
        intent.whatIfNotNull {
            intent.getBundleExtra("questionId").whatIfNotNull {
                it.getString("questionId").whatIfNotNull {
                    this.questionId = intent!!
                        .getBundleExtra("questionId")!!
                        .getString("questionId")
                }

                it.getString("notificationCommentId").whatIfNotNull {
                    this.notificationCommentId = intent!!
                        .getBundleExtra("questionId")!!
                        .getString("notificationCommentId")
                }
            }
        }
    }

    private fun initVariables() {
        otherProfileOptions = OtherProfileOptions.newInstance()
        questionStatistics = QuestionStatistics.newInstance()
    }

    private fun setOnClickListner() {
        question_info.setOnClickListener {
            questionId.whatIfNotNull {
                if (questionId != "")
                    presenter.showStats(questionId!!)
            }
        }

        close_iv.setOnClickListener {
            onBackPressed()
        }

        user_iv.setOnClickListener {
            presenter.isLoggedIn()
        }

        filterComments_iv.setOnClickListener {
            showPopUpMenu(filterComments_iv)
        }
    }

    private fun showPopUpMenu(v: View) {
        PopupMenu(this, v, Gravity.END).apply {
            setOnMenuItemClickListener(this@CommentsAcrivity)
            inflate(R.menu.sort_comment)
            show()
        }
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.newestItem -> {
                presenter.getComments(questionId, 1)
                true
            }
            R.id.likesItem -> {
                presenter.getComments(questionId, 2)
                true
            }
            R.id.dislikesItem -> {
                presenter.getComments(questionId, 3)
                true
            }
            else -> false
        }
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

    override fun setUserProfileImage(imageUrl: String) {
        imageUrl.whatIfNotNull(
            whatIf = {
                imageProgressBar.visibility = View.VISIBLE
                user_iv.load(imageUrl,
                    Coil.loader(),
                    builder = {
                        this.listener(object : Request.Listener {
                            override fun onError(data: Any, throwable: Throwable) {
                                imageProgressBar.visibility = View.GONE
                            }

                            override fun onSuccess(
                                data: Any,
                                source: coil.decode.DataSource
                            ) {
                                super.onSuccess(data, source)
                                imageProgressBar.visibility = View.GONE
                                user_iv.borderWidth = 2
                                user_iv.borderColor = resources.getColor(R.color.white)
                            }
                        })
                    })
            },
            whatIfNot = {
                showMessage(getString(R.string.something_went_wrong), 1)
            }
        )
    }

    override fun setNotificationIcon(notificationCount: String) {
        if (notificationCount == "0" || notificationCount == "null") {
            notification_iv.visibility = View.GONE
        } else {
            notification_iv.visibility = View.VISIBLE
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

    override fun startLogin() {
        hideLoading()
        openActivity(SignInActivity::class.java)
    }

    override fun startProfileAct() {
        hideLoading()
        openActivity(ProfileActivity::class.java)
    }

    override fun showMessage(message: String, type: Int) {
        try {
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
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun addCommentInner(
        commnetVal: String,
        commentId: String,
        repliesCount: Int
    ) {
        presenter.addInnerComment(commnetVal, commentId, questionId!!, repliesCount)
    }

    fun openProfileDetails(givenBy: String) {
        if (!otherProfileOptions.isAdded) {
            otherProfileOptions.show(supportFragmentManager, "btmSheet")
            otherProfileOptions.setUserId(givenBy)
        }
    }

    fun addReviewForComment(
        voteType: Int,
        commnetId: String,
        voteCount: String
    ) {
        presenter.setVoteForComment(voteType, commnetId, questionId, voteCount)
    }
}

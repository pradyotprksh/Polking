package com.project.pradyotprakash.polking.home

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.appbar.AppBarLayout
import com.irozon.sneaker.Sneaker
import com.irozon.sneaker.interfaces.OnSneakerClickListener
import com.irozon.sneaker.interfaces.OnSneakerDismissListener
import com.project.pradyotprakash.polking.R
import com.project.pradyotprakash.polking.home.adapter.QuestionsAdapter
import com.project.pradyotprakash.polking.otherProfileOptions.OtherProfileOptions
import com.project.pradyotprakash.polking.profile.ProfileActivity
import com.project.pradyotprakash.polking.profile.notification.NotificationBottomSheet
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
import kotlin.math.abs

class MainActivity : AppCompatActivity(), MainActivityView, OnSneakerClickListener,
    OnSneakerDismissListener {

    @Inject
    lateinit var presenter: MainActivityPresenter
    lateinit var profileEditBtmSheet: ProfileEditBtmSheet
    lateinit var otherProfileOptions: OtherProfileOptions
    lateinit var notificationBottomSheet: NotificationBottomSheet
    lateinit var userListBtmSheet: UserListBtmSheet
    private var questionsAdapter: QuestionsAdapter? = null
    private val allQues = ArrayList<QuestionModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)

        // Make full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        setContentView(R.layout.activity_main)

        logd(getString(R.string.create))
        initialize()
    }

    private fun initialize() {
        presenter.start()

        user_iv.setOnClickListener {
            presenter.isLoggedIn()
        }

        profileEditBtmSheet = ProfileEditBtmSheet.newInstance()
        otherProfileOptions = OtherProfileOptions.newInstance()
        notificationBottomSheet = NotificationBottomSheet.newInstance()
        profileEditBtmSheet.isCancelable = false
        userListBtmSheet = UserListBtmSheet.newInstance()

        appBarLayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout: AppBarLayout, i: Int ->
            when {
                abs(i) == appBarLayout.totalScrollRange -> {
                    if (addQuestion_et2.visibility != View.VISIBLE) {
                        addQuestion_et2.visibility = View.VISIBLE
                        addQuestion_et2.startAnimation(Utility().inFromDownAnimation())
                    }
                }
                i == 0 -> {
                    addQuestion_et2.visibility = View.GONE
                }
                else -> {
                    addQuestion_et2.visibility = View.GONE
                }
            }
        })

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

        addQuestion_et2.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s != null) {
                    if (s.length > 5) {
                        if (post_Tv2.visibility == View.GONE) {
                            post_Tv2.startAnimation(Utility().inFromRightAnimation())
                        }
                        post_Tv2.visibility = View.VISIBLE
                    } else {
                        if (post_Tv2.visibility != View.GONE) {
                            post_Tv2.startAnimation(Utility().outToRightAnimation())
                            post_Tv2.visibility = View.GONE
                        }
                    }
                } else {
                    if (post_Tv2.visibility != View.GONE) {
                        post_Tv2.startAnimation(Utility().outToRightAnimation())
                        post_Tv2.visibility = View.GONE
                    }
                }
            }
        })

        post_Tv.setOnClickListener {
            presenter.uploadQuestion(addQuestion_et.text.toString())
        }

        post_Tv2.setOnClickListener {
            presenter.uploadQuestion(addQuestion_et2.text.toString())
        }

        questionsAdapter = QuestionsAdapter(allQues, this, this)
        recentQ_rv.setHasFixedSize(true)
        recentQ_rv.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        recentQ_rv.adapter = questionsAdapter
    }

    override fun onResume() {
        super.onResume()
        logd(getString(R.string.resume))
        this.allQues.clear()
        presenter.addAuthStateListener()
        presenter.getProfileData()
        presenter.getBestFrndQuestions()
        presenter.getQuestions()
        presenter.getNotificationCount()
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

    override fun showNotificationView(notificationCount: String) {
        if (notificationCount != "0" && notificationCount.isNotEmpty()) {

            val message: String = when (notificationCount) {
                "1" -> getString(R.string.you_have_got) + " " + notificationCount + " notification."
                "100" -> getString(R.string.you_have_got) + "  99+ notifications."
                else -> getString(R.string.you_have_got) + " " + notificationCount + " notifications."
            }

            val typeface = ResourcesCompat.getFont(this, R.font.muli_bold)

            Sneaker.with(this)
                .setTitle("Notification", R.color.white)
                .setMessage(message, R.color.white)
                .setDuration(15000)
                .autoHide(true)
                .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                .setIcon(R.mipmap.ic_launcher, true)
                .setTypeface(typeface!!)
                .setOnSneakerClickListener(this)
                .setOnSneakerDismissListener(this)
                .setCornerRadius(5, 0)
                .sneak(R.color.colorAccent)
        }
    }

    override fun onSneakerClick(view: View) {
        if (!notificationBottomSheet.isAdded) {
            notificationBottomSheet.show(supportFragmentManager, "btmSheet")
        }
    }

    override fun onDismiss() {
        logd("message dismissed")
    }

    override fun hideNotificationView() {
        if (notificationBottomSheet.isAdded) {
            notificationBottomSheet.dismiss()
        }
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
        } else {
            showMessage(getString(R.string.something_went_wrong), 1)
        }
    }

    @SuppressLint("SetTextI18n")
    override fun setUserName(name: String) {
        welcome_tv.text = "Welcome, $name"
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
        if (allQuestionList.size > 0) {
            recentQ_rv.visibility = View.VISIBLE
            recent_tv.visibility = View.VISIBLE
            allQues.addAll(allQuestionList)
            questionsAdapter!!.notifyDataSetChanged()
        } else {
            recentQ_rv.visibility = View.GONE
            recent_tv.visibility = View.GONE
        }
    }

    override fun onStop() {
        super.onStop()
        logd(getString(R.string.stop))
        presenter.removeListener()
    }

    fun showVotes(docId: String) {
        if (docId.isNotEmpty()) {
            if (!userListBtmSheet.isAdded) {
                userListBtmSheet.show(supportFragmentManager, "btmSheet")
                userListBtmSheet.setQuestionDocId(docId)
                userListBtmSheet.setContext(this)
                userListBtmSheet.setActivity(this)
            }
        }
    }

    fun openProfileDetails(askedBy: String) {
        if (!otherProfileOptions.isAdded) {
            otherProfileOptions.show(supportFragmentManager, "btmSheet")
            otherProfileOptions.setUserId(askedBy)
        }
    }

}

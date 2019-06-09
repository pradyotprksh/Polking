package com.project.pradyotprakash.polking.profile

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.view.Window
import android.view.WindowManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.project.pradyotprakash.polking.R
import com.project.pradyotprakash.polking.faq.FAQsActvity
import com.project.pradyotprakash.polking.profile.backgroundAdapter.BackgroundAdapter
import com.project.pradyotprakash.polking.profileDetails.ProfileEditBtmSheet
import com.project.pradyotprakash.polking.utility.BgModel
import com.project.pradyotprakash.polking.utility.Utility
import com.project.pradyotprakash.polking.utility.logd
import com.project.pradyotprakash.polking.utility.openActivity
import com.theartofdev.edmodo.cropper.CropImage
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_profile.*
import java.util.*
import javax.inject.Inject

class ProfileActivity : AppCompatActivity(), ProfileActivityView {

    @Inject
    lateinit var presenter: ProfileActivityPresenter
    private var allBgAdapter: BackgroundAdapter? = null
    private val allBgList = ArrayList<BgModel>()
    private var bgDocId: String? = null
    lateinit var profileEditBtmSheet: ProfileEditBtmSheet

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)

        // Make full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        setContentView(R.layout.activity_profile)

        logd(getString(R.string.create))
        initialize()
    }

    private fun initialize() {

        allBgAdapter = BackgroundAdapter(allBgList, this, this)
        rv_bgOption.setHasFixedSize(true)
        rv_bgOption.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rv_bgOption.adapter = allBgAdapter

        options_tv.setOnClickListener {
            optionList_cl.visibility = View.VISIBLE
            optionList_cl.startAnimation(Utility().inFromDownAnimation())
        }

        edit_tv.setOnClickListener {
            openAddProfileDetails()
        }

        iv_close.setOnClickListener {
            optionList_cl.startAnimation(Utility().outToDownAnimation())
            optionList_cl.visibility = View.GONE
        }

        review_tv.setOnClickListener {

        }

        faq_tv.setOnClickListener {
            openActivity(FAQsActvity::class.java)
        }

        aboutUs_tv.setOnClickListener {

        }

        back_tv.setOnClickListener {
            if (!bgDocId.isNullOrEmpty()) {
                presenter.changeBgId(bgDocId!!)
            }
        }

        profileEditBtmSheet = ProfileEditBtmSheet.newInstance()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (!bgDocId.isNullOrEmpty()) {
            presenter.changeBgId(bgDocId!!)
        }
    }

    override fun setUserProfileImage(imageUrl: String?) {
        if (imageUrl != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                if (!this.isDestroyed) {
                    Glide.with(this).load(imageUrl).listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(
                            exception: GlideException?,
                            model: Any?,
                            target: Target<Drawable>?,
                            isFirstResource: Boolean
                        ): Boolean {
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
                            return false
                        }
                    }).into(profile_iv)
                }
            }
        } else {
            showMessage(getString(R.string.something_went_wrong), 1)
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

    @SuppressLint("SetTextI18n")
    override fun setUserName(name: String?) {
        if (!name.isNullOrEmpty()) {
            welcome_tv.text = "${Utility().getDayMessage()}, $name. Welcome to ${getString(R.string.app_name)}."
        }
    }

    override fun openAddProfileDetails() {
        if (!profileEditBtmSheet.isAdded) {
            profileEditBtmSheet.show(supportFragmentManager, "btmSheet")
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

    }

    override fun setUserDetails(question: String?, friends: String?, bestFriends: String?) {
        if (!question.isNullOrEmpty()) {
            questionVal_tv.text = question
        }
        if (!friends.isNullOrEmpty()) {
            friendsVal_tv.text = friends
        }
        if (!bestFriends.isNullOrEmpty()) {
            bestFrndVal_tv.text = bestFriends
        }
    }

    override fun setBgImage(imageUrl: String, docId: String) {
        Glide.with(this).load(imageUrl).listener(object : RequestListener<Drawable> {
            override fun onLoadFailed(
                exception: GlideException?,
                model: Any?,
                target: Target<Drawable>?,
                isFirstResource: Boolean
            ): Boolean {
                return false
            }

            override fun onResourceReady(
                resource: Drawable?,
                model: Any?,
                target: Target<Drawable>?,
                dataSource: DataSource?,
                isFirstResource: Boolean
            ): Boolean {
                return false
            }
        }).into(bg_iv)
        bgDocId = docId
    }
}

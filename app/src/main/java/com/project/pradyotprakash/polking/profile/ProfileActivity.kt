package com.project.pradyotprakash.polking.profile

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
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
import com.project.pradyotprakash.polking.profile.backgroundAdapter.BackgroundAdapter
import com.project.pradyotprakash.polking.utility.BgModel
import com.project.pradyotprakash.polking.utility.Utility
import com.project.pradyotprakash.polking.utility.logd
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_profile.*
import java.util.*
import javax.inject.Inject

class ProfileActivity : AppCompatActivity(), ProfileActivityView {

    @Inject
    lateinit var presenter: ProfileActivityPresenter
    var allBgAdapter: BackgroundAdapter? = null
    val allBgList = ArrayList<BgModel>()

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
        presenter.getUserData()

        allBgAdapter = BackgroundAdapter(allBgList, this, this)
        rv_bgOption.setHasFixedSize(true)
        rv_bgOption.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rv_bgOption.adapter = allBgAdapter

        options_tv.setOnClickListener {
            optionList_cl.visibility = View.VISIBLE
        }

        iv_close.setOnClickListener {
            optionList_cl.visibility = View.GONE
        }

        back_tv.setOnClickListener {
            finish()
        }
    }

    override fun setUserProfileImage(imageUrl: String?) {
        if (imageUrl != null) {
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
        } else {
            showMessage(getString(R.string.something_went_wrong), 1)
        }
    }

    @SuppressLint("SetTextI18n")
    override fun setUserName(name: String?) {
        if (!name.isNullOrEmpty()) {
            welcome_tv.text = "${Utility().getDayMessage()}, $name. Welcome to ${getString(R.string.app_name)}."
        }
    }

    override fun openAddProfileDetails() {

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
        presenter.getBackgroundImages()
    }

    override fun hideBackGroundOption() {
        bgOption_cl.visibility = View.GONE
    }

    override fun showLoading() {

    }

    override fun hideLoading() {

    }

    override fun stopAct() {

    }

    override fun showMessage(message: String, type: Int) {

    }

    override fun setBgImage(imageUrl: String) {
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
    }
}

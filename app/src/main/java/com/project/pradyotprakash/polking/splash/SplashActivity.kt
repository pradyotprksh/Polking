package com.project.pradyotprakash.polking.splash

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.project.pradyotprakash.polking.R
import com.project.pradyotprakash.polking.signin.SignInActivity
import com.project.pradyotprakash.polking.utility.openActivity
import dagger.android.AndroidInjection
import javax.inject.Inject

class SplashActivity : AppCompatActivity(), SplashView {

    @Inject
    lateinit var presenter: SplashPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        initialize()
    }

    private fun initialize() {
        presenter.start()
    }

    override fun showLoading() {
        Handler().postDelayed({
            openActivity(SignInActivity::class.java)
            finish()
        }, 1000)
    }

    override fun hideLoading() {

    }

    override fun stopAct() {

    }

}

package com.project.pradyotprakash.polking.signin

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.project.pradyotprakash.polking.R
import dagger.android.AndroidInjection
import javax.inject.Inject

class SignInActivity : AppCompatActivity(), SignInView {

    @Inject
    lateinit var presenter: SignInPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        initialize()
    }

    private fun initialize() {
        presenter.start()
    }

    override fun showLoading() {

    }

    override fun hideLoading() {

    }

    override fun stopAct() {

    }
}

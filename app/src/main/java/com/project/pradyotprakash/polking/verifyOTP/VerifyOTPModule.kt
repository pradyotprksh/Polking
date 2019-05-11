package com.project.pradyotprakash.polking.verifyOTP

import android.app.Activity
import com.project.pradyotprakash.polking.dagger.ActivityScoped
import dagger.Binds
import dagger.Module

@Module
abstract class VerifyOTPModule {

    @ActivityScoped
    @Binds
    internal abstract fun provideVerifyOTPActivty(verifyOTPActivity: VerifyOTPActivity): Activity

    @ActivityScoped
    @Binds
    internal abstract fun verifyOTPPresenter(verifyOTPPresenterImpl: VerifyOTPPresenterImpl): VerifyOTPPresenter

    @ActivityScoped
    @Binds
    internal abstract fun verifyOTPView(verifyOTPActivity: VerifyOTPActivity): VerifyOTPView

}
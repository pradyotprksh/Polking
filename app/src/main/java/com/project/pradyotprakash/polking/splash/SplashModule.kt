package com.project.pradyotprakash.polking.splash

import android.app.Activity
import com.project.pradyotprakash.polking.dagger.ActivityScoped
import dagger.Binds
import dagger.Module

@Module
abstract class SplashModule {

    @ActivityScoped
    @Binds
    internal abstract fun provideSplashActivty(splashActivity: SplashActivity): Activity

    @ActivityScoped
    @Binds
    internal abstract fun splashPresenter(presenter: SplashPresenterImpl): SplashPresenter

    @ActivityScoped
    @Binds
    internal abstract fun splashView(view: SplashActivity): SplashView

}
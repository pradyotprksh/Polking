package com.project.pradyotprakash.polking.home

import android.app.Activity
import com.project.pradyotprakash.polking.dagger.ActivityScoped
import dagger.Binds
import dagger.Module

@Module
abstract class MainActivityModule {

    @ActivityScoped
    @Binds
    internal abstract fun provideMainActivty(mainActivity: MainActivity): Activity

    @ActivityScoped
    @Binds
    internal abstract fun mainPresenter(presenter: MainActivityPresenterImpl): MainActivityPresenter

    @ActivityScoped
    @Binds
    internal abstract fun mainView(view: MainActivity): MainActivityView

}
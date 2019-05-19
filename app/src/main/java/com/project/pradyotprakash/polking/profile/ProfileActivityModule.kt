package com.project.pradyotprakash.polking.profile

import android.app.Activity
import com.project.pradyotprakash.polking.dagger.ActivityScoped
import dagger.Binds
import dagger.Module

@Module
abstract class ProfileActivityModule {

    @ActivityScoped
    @Binds
    internal abstract fun provideProfileActivty(profileActivity: ProfileActivity): Activity

    @ActivityScoped
    @Binds
    internal abstract fun profilePresenter(presenterImpl: ProfileActivityPresenterImpl): ProfileActivityPresenter

    @ActivityScoped
    @Binds
    internal abstract fun profileView(view: ProfileActivity): ProfileActivityView

}
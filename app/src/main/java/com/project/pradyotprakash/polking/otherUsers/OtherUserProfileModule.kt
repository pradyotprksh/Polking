package com.project.pradyotprakash.polking.otherUsers

import android.app.Activity
import com.project.pradyotprakash.polking.dagger.ActivityScoped
import dagger.Binds
import dagger.Module

@Module
abstract class OtherUserProfileModule {

    @ActivityScoped
    @Binds
    internal abstract fun provideOtherUserProfileActivty(otherUserProfile: OtherUserProfileActivity): Activity

    @ActivityScoped
    @Binds
    internal abstract fun otherUserProfilePresenter(presenter: OtherUserProfilePresenterImpl): OtherUserProfilePresenter

    @ActivityScoped
    @Binds
    internal abstract fun otherUserProfilePresenterView(view: OtherUserProfileActivity): OtherUserProfileView

}
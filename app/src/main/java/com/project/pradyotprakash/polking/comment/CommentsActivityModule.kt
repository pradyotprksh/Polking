package com.project.pradyotprakash.polking.comment

import android.app.Activity
import com.project.pradyotprakash.polking.dagger.ActivityScoped
import dagger.Binds
import dagger.Module

@Module
abstract class CommentsActivityModule {

    @ActivityScoped
    @Binds
    internal abstract fun provideCommentsActivty(commentActivity: CommentsAcrivity): Activity

    @ActivityScoped
    @Binds
    internal abstract fun commentPresenter(presenter: CommentsActivityPresenterImpl): CommentsActivityPresenter

    @ActivityScoped
    @Binds
    internal abstract fun commnetView(view: CommentsAcrivity): CommentsActivityView

}
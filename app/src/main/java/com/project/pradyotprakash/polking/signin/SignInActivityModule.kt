package com.project.pradyotprakash.polking.signin

import android.app.Activity
import com.project.pradyotprakash.polking.dagger.ActivityScoped
import dagger.Binds
import dagger.Module

@Module
abstract class SignInActivityModule {

    @ActivityScoped
    @Binds
    internal abstract fun provideSignInActivty(signInActivity: SignInActivity): Activity

    @ActivityScoped
    @Binds
    internal abstract fun signInPresenter(singInPresenter: SignInPresenterImpl): SignInPresenter

    @ActivityScoped
    @Binds
    internal abstract fun splashView(view: SignInActivity): SignInView

}
package com.project.pradyotprakash.polking.dagger

import com.project.pradyotprakash.polking.home.MainActivity
import com.project.pradyotprakash.polking.home.MainActivityModule
import com.project.pradyotprakash.polking.signin.SignInActivity
import com.project.pradyotprakash.polking.signin.SignInActivityModule
import com.project.pradyotprakash.polking.splash.SplashActivity
import com.project.pradyotprakash.polking.splash.SplashModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class BuildersModule {

    @ActivityScoped
    @ContributesAndroidInjector(modules = [MainActivityModule::class])
    abstract fun mainActivity(): MainActivity

    @ActivityScoped
    @ContributesAndroidInjector(modules = [SplashModule::class])
    abstract fun splashActivity(): SplashActivity

    @ActivityScoped
    @ContributesAndroidInjector(modules = [SignInActivityModule::class])
    abstract fun singInActivity(): SignInActivity
}
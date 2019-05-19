package com.project.pradyotprakash.polking.profileDetails

import com.project.pradyotprakash.polking.dagger.ActivityScoped
import com.project.pradyotprakash.polking.dagger.FragmentScoped
import dagger.Binds
import dagger.Module

@Module
abstract class ProfileEditModule {

    @FragmentScoped
    @Binds
    internal abstract fun provideProfileEditActivty(profileEditBtmSheet: ProfileEditBtmSheet): ProfileEditBtmSheet

    @ActivityScoped
    @Binds
    internal abstract fun profileEditPresenter(presenter: ProfileEditPresenterImpl): ProfileEditPresenter

}
package com.project.pradyotprakash.polking.profileDetails

import com.project.pradyotprakash.polking.dagger.FragmentScoped
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ProfileEditModule {

    @FragmentScoped
    @ContributesAndroidInjector
    internal abstract fun provideProfileEditActivty(): ProfileEditBtmSheet

    @Binds
    internal abstract fun profileEditPresenter(presenter: ProfileEditPresenterImpl): ProfileEditPresenter

}
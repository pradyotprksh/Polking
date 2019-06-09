package com.project.pradyotprakash.polking.faq

import android.app.Activity
import com.project.pradyotprakash.polking.dagger.ActivityScoped
import dagger.Binds
import dagger.Module

@Module
abstract class FAQsActivityModule {

    @ActivityScoped
    @Binds
    internal abstract fun provideFAQsActivty(faQsActivity: FAQsActivity): Activity

    @ActivityScoped
    @Binds
    internal abstract fun faqsPresenter(presenter: FAQsActivityPresenterImpl): FAQsActivityPresenter

    @ActivityScoped
    @Binds
    internal abstract fun faqsView(view: FAQsActivity): FAQsActivityView

}
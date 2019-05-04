package com.project.pradyotprakash.polking

import android.accounts.AccountManager
import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.project.pradyotprakash.polking.dagger.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import dagger.android.DispatchingAndroidInjector
import javax.inject.Inject

class ApplicationManager: DaggerApplication(), Application.ActivityLifecycleCallbacks {

    @Inject
    lateinit var activityInjector: DispatchingAndroidInjector<Activity>

    private lateinit var applicationManager: ApplicationManager
    private var mAccountManager: AccountManager? = null

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        val appComponent = DaggerAppComponent.builder().application(this).build()
        appComponent.inject(this)
        return appComponent
    }

    override fun onActivityPaused(activity: Activity?) {

    }

    override fun onActivityResumed(activity: Activity?) {

    }

    override fun onActivityStarted(activity: Activity?) {

    }

    override fun onActivityDestroyed(activity: Activity?) {

    }

    override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {

    }

    override fun onActivityStopped(activity: Activity?) {

    }

    override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {

    }

    fun getInstance(): ApplicationManager {
        return applicationManager
    }

    override fun onCreate() {
        super.onCreate()
        applicationManager = this
        registerActivityLifecycleCallbacks(this)
        mAccountManager = AccountManager.get(applicationContext)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        System.gc()
    }

}
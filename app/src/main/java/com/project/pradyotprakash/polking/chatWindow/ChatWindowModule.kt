package com.project.pradyotprakash.polking.chatWindow

import android.app.Activity
import com.project.pradyotprakash.polking.dagger.ActivityScoped
import dagger.Binds
import dagger.Module

@Module
abstract class ChatWindowModule {

    @ActivityScoped
    @Binds
    internal abstract fun provideChatActivty(chatWindow: ChatWindow): Activity

    @ActivityScoped
    @Binds
    internal abstract fun chatPresenter(presenter: ChatWindowPresenterImpl): ChatWindowPresenter

    @ActivityScoped
    @Binds
    internal abstract fun chatView(view: ChatWindow): ChatWindowView

}
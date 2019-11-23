package com.project.pradyotprakash.polking.chatWindow

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.Coil
import coil.api.load
import coil.transform.CircleCropTransformation
import com.afollestad.materialdialogs.MaterialDialog
import com.project.pradyotprakash.polking.R
import com.project.pradyotprakash.polking.chatWindow.adapter.ChatAdapter
import com.project.pradyotprakash.polking.message.ShowMessage
import com.project.pradyotprakash.polking.utility.ChatModel
import com.project.pradyotprakash.polking.utility.InternetActivity
import com.project.pradyotprakash.polking.utility.Utility
import com.skydoves.whatif.whatIfNotNull
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_chat_window.*
import java.util.*
import javax.inject.Inject

class ChatWindow : InternetActivity(), ChatWindowView,
    PopupMenu.OnMenuItemClickListener {

    @Inject
    lateinit var presenter: ChatWindowPresenter
    private var chatAdapter: ChatAdapter? = null

    private var chatWindowId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)

        // Make full screen
        // set theme for notch if sdk is P
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            )
            window.decorView.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        } else {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        setContentView(R.layout.activity_chat_window)

        initView()
    }

    private fun initView() {
        intentData()

        setTextChangeListner()

        requestFocus()

        setOnClickListners()

        setAdapter()

        setSendMessageListner()

        presenter.getChatDetails(chatWindowId)

        presenter.getChatList(chatWindowId)
    }

    private fun setSendMessageListner() {
        commentVal_rt.setOnEditorActionListener { v, actionId, event ->
            return@setOnEditorActionListener when (actionId) {
                EditorInfo.IME_ACTION_SEND -> {
                    if (commentVal_rt.text.toString().isNotEmpty()) {
                        presenter.uploadMessage(commentVal_rt.text, this.chatWindowId)
                        commentVal_rt.setText("")
                    }
                    true
                }
                else -> false
            }
        }
    }

    private fun setAdapter() {
        chatAdapter = ChatAdapter(this, this)
        chats_rv.setHasFixedSize(true)
        chats_rv.layoutManager = LinearLayoutManager(
            this,
            RecyclerView.VERTICAL, false
        )
        chats_rv.adapter = chatAdapter
    }

    private fun setOnClickListners() {
        close_iv.setOnClickListener {
            finish()
        }

        options_iv.setOnClickListener {
            showPopUpMenu(options_iv)
        }

        sendMsg_fb.setOnClickListener {
            if (commentVal_rt.text.isNotEmpty()) {
                presenter.uploadMessage(commentVal_rt.text, this.chatWindowId)
                commentVal_rt.setText("")
            }
        }
    }

    private fun showPopUpMenu(v: ImageView) {
        PopupMenu(this, v, Gravity.END).apply {
            setOnMenuItemClickListener(this@ChatWindow)
            inflate(R.menu.chat_options)
            show()
        }
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.deleteChat -> {
                showDeleteChatOption()
                true
            }
            else -> false
        }
    }

    private fun showDeleteChatOption() {
        MaterialDialog(this)
            .title(text = getString(R.string.delete_chat))
            .message(text = "Are you sure you want to delete the chat?")
            .show {
                noAutoDismiss()
                icon(R.drawable.chat_iv)
                positiveButton(text = getString(R.string.yes)) {
                    presenter.deleteChat(chatWindowId)
                    dismiss()
                }
            }
    }

    private fun setTextChangeListner() {
        commentVal_rt.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                s.whatIfNotNull {
                    var typingStarted = false
                    if (!TextUtils.isEmpty(s.toString()) && s.toString().trim().length >= 1) {
                        typingStarted = true
                    } else if (s.toString().trim().isEmpty() && typingStarted) {
                        typingStarted = false
                    }
                    presenter.updateTypingStatus(typingStarted, chatWindowId)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun intentData() {
        intent.whatIfNotNull {
            intent.getBundleExtra("chatWindowId").whatIfNotNull {
                it.getString("chatWindowId").whatIfNotNull {
                    this.chatWindowId = intent!!
                        .getBundleExtra("chatWindowId")!!
                        .getString("chatWindowId")!!
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        commentVal_rt.clearFocus()
        Utility().hideSoftKeyboard(commentVal_rt)
        presenter.updateTypingStatus(false, chatWindowId)
    }

    private fun requestFocus() {
        commentVal_rt.requestFocus()
        Utility().showSoftKeyboard(commentVal_rt)
    }

    override fun setUserImage(imageUrl: String) {
        Coil.load(
            this,
            imageUrl
        ) {
            this.transformations(
                CircleCropTransformation()
            )
            target { drawable ->
                name_tv.chipIcon = drawable
            }
        }
    }

    override fun messageUploaded() {
        commentVal_rt.setText("")
    }

    override fun setChatList(allChatList: ArrayList<ChatModel>) {
        chats_rv.smoothScrollToPosition(0)
        chatAdapter?.updateListItems(allChatList)
    }

    override fun showDeleteOption() {
        options_iv.visibility = View.VISIBLE
    }

    override fun hideDeleteOption() {
        options_iv.visibility = View.GONE
    }

    override fun setUserData(userDetails: String) {
        name_tv.text = userDetails
    }

    override fun showLoading() {

    }

    override fun hideLoading() {

    }

    override fun stopAct() {
        finish()
    }

    override fun showMessage(message: String, type: Int) {
        messageBtmSheet = ShowMessage.newInstance()
        if (!messageBtmSheet.isAdded) {
            messageBtmSheet.show(supportFragmentManager, "btmSheet")
            messageBtmSheet.setMessage(message, type)
        } else {
            messageBtmSheet.dismiss()
            Handler().postDelayed({
                if (!messageBtmSheet.isAdded) {
                    messageBtmSheet.show(supportFragmentManager, "btmSheet")
                    messageBtmSheet.setMessage(message, type)
                }
            }, 1500)
        }
    }

}

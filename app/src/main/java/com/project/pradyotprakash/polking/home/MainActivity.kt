package com.project.pradyotprakash.polking.home

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.Coil
import coil.api.load
import coil.request.Request
import com.afollestad.materialdialogs.MaterialDialog
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.tasks.Task
import com.project.pradyotprakash.polking.R
import com.project.pradyotprakash.polking.chatWindow.ChatWindow
import com.project.pradyotprakash.polking.comment.CommentsAcrivity
import com.project.pradyotprakash.polking.home.adapter.ChatRequestAdapter
import com.project.pradyotprakash.polking.home.adapter.LabelsAdapter
import com.project.pradyotprakash.polking.home.adapter.QuestionsAdapter
import com.project.pradyotprakash.polking.message.ShowMessage
import com.project.pradyotprakash.polking.otherProfileOptions.OtherProfileOptions
import com.project.pradyotprakash.polking.profile.ProfileActivity
import com.project.pradyotprakash.polking.profileDetails.ProfileEditBtmSheet
import com.project.pradyotprakash.polking.questionStats.QuestionStatistics
import com.project.pradyotprakash.polking.signin.SignInActivity
import com.project.pradyotprakash.polking.updateTheApp.UpdateBtmSheet
import com.project.pradyotprakash.polking.utility.*
import com.skydoves.whatif.whatIfNotNull
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

class MainActivity : InternetActivity(), MainActivityView {

    @Inject
    lateinit var presenter: MainActivityPresenter
    lateinit var profileEditBtmSheet: ProfileEditBtmSheet
    lateinit var otherProfileOptions: OtherProfileOptions
    lateinit var questionStatistics: QuestionStatistics
    lateinit var updateBtmSheet: UpdateBtmSheet
    private var questionsAdapter: QuestionsAdapter? = null
    private var labelAdapter: LabelsAdapter? = null
    private var chatRequestAdapter: ChatRequestAdapter? = null
    private val allQues = ArrayList<QuestionModel>()
    private var imageLabel: String = "all"
    private var allLabelList: ArrayList<String> = ArrayList()
    private var picOptionUri: Uri? = null
    private var count = 0
    private var count1 = 0
    private var isOptionForQuestion: Boolean = false

    private val appUpdateManager by lazy { AppUpdateManagerFactory.create(this) }
    private val appUpdateInfo: Task<AppUpdateInfo> by lazy { appUpdateManager.appUpdateInfo }

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

        setContentView(R.layout.activity_main)

        logd(getString(R.string.create))
        initialize()
    }

    private fun initialize() {
        checkForUpdates()

        presenter.start()

        initVariables()

        setOnClickListners()

        textChangeListners()

        adapters()

        getQuestions()

        getDataForUser()

        getLabelsForImages()
    }

    private fun getLabelsForImages() {
        presenter.getImageLabels()
    }

    private fun getDataForUser() {
        presenter.addAuthStateListener()
    }

    private fun getQuestions() {
        presenter.getQuestions()
    }

    private fun adapters() {
        questionsAdapter = QuestionsAdapter(this, this)
        recentQ_rv.setHasFixedSize(true)
        recentQ_rv.layoutManager = LinearLayoutManager(
            this,
            RecyclerView.VERTICAL, false
        )
        recentQ_rv.adapter = questionsAdapter

        labelAdapter = LabelsAdapter(this, this)
        labels_rv.setHasFixedSize(true)
        labels_rv.layoutManager = LinearLayoutManager(
            this,
            RecyclerView.HORIZONTAL, false
        )
        labels_rv.adapter = labelAdapter

        chatRequestAdapter = ChatRequestAdapter(this, this)
        chats_rv.setHasFixedSize(true)
        chats_rv.layoutManager = LinearLayoutManager(
            this,
            RecyclerView.HORIZONTAL, false
        )
        chats_rv.adapter = chatRequestAdapter
    }

    private fun textChangeListners() {
        addQuestion_et.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                s.whatIfNotNull(
                    whatIf = {
                        if (s!!.length > 5) {
                            if (post_Tv.visibility == View.GONE) {
                                post_Tv.startAnimation(Utility().inFromRightAnimation())
                            }
                            post_Tv.visibility = View.VISIBLE
                        } else {
                            if (post_Tv.visibility != View.GONE) {
                                post_Tv.startAnimation(Utility().outToRightAnimation())
                                post_Tv.visibility = View.GONE
                            }
                        }
                    },
                    whatIfNot = {
                        if (post_Tv.visibility != View.GONE) {
                            post_Tv.startAnimation(Utility().outToRightAnimation())
                            post_Tv.visibility = View.GONE
                        }
                    }
                )
            }
        })
    }

    override fun deleteQuestionImageUri() {
        picOptionUri.whatIfNotNull {
            picOptionUri = null
            camera_iv.load(R.drawable.ic_camera)
            add_iv.load(R.drawable.ic_plus_side)
        }
    }

    override fun setNotificationIcon(notificationCount: String) {
        if (notificationCount == "0") {
            notification_iv.visibility = View.GONE
        } else {
            notification_iv.visibility = View.VISIBLE
        }
    }

    private fun setOnClickListners() {
        camera_iv.setOnClickListener {
            if (checkReadPermission() && checkWritePermission()) {
                openCamera()
                deleteQuestionImageUri()
            }
        }

        camera_iv.setOnLongClickListener {
            showMessage(getString(R.string.add_images_question), 2)
            true
        }

        user_iv.setOnClickListener {
            presenter.isLoggedIn()
        }

        post_Tv.setOnClickListener {
            picOptionUri.whatIfNotNull(
                whatIf = {
                    presenter
                        .uploadQuestionWithImage(
                            addQuestion_et.text.toString(), picOptionUri!!,
                            imageLabel
                        )
                },
                whatIfNot = { presenter.uploadQuestion(addQuestion_et.text.toString()) }
            )
        }
    }

    private fun checkReadPermission(): Boolean {
        return if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    AppConstants.PERMISSIONS_REQUEST_READ_STORAGE
                )
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    AppConstants.PERMISSIONS_REQUEST_READ_STORAGE
                )
            }
            false
        } else {
            true
        }
    }

    override fun setQuestionImage(picOptionUri: Uri) {
        add_iv.load(R.drawable.ic_add)
        this.picOptionUri = picOptionUri
        camera_iv.setImageURI(picOptionUri)
        camera_iv.borderColor = resources.getColor(R.color.white)
        camera_iv.borderWidth = 2
        isOptionForQuestion = false
    }

    private fun checkWritePermission(): Boolean {
        return if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    AppConstants.PERMISSIONS_REQUEST_WRITE_STORAGE
                )
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    AppConstants.PERMISSIONS_REQUEST_WRITE_STORAGE
                )
            }
            false
        } else {
            true
        }
    }

    private fun initVariables() {
        profileEditBtmSheet = ProfileEditBtmSheet.newInstance()
        otherProfileOptions = OtherProfileOptions.newInstance()
        questionStatistics = QuestionStatistics.newInstance()
        updateBtmSheet = UpdateBtmSheet.newInstance()
        profileEditBtmSheet.isCancelable = false
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            AppConstants.PERMISSIONS_REQUEST_READ_STORAGE -> {
                if (grantResults.isNotEmpty() && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED
                ) {
                    if (ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        openCamera()
                    }
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (!shouldShowRequestPermissionRationale(
                                Manifest.permission.READ_EXTERNAL_STORAGE
                            )
                        ) {
                            count1++
                            if (count1 > 1) {
                                showMessageOKCancel(
                                    getString(R.string.readStoragePermission)
                                ) { _, _ ->
                                    val intent = Intent()
                                    intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                                    val uri = Uri.fromParts(
                                        "package",
                                        this.packageName, null
                                    )
                                    intent.data = uri
                                    startActivity(intent)
                                }
                            }
                        }
                    }
                }
                return
            }

            AppConstants.PERMISSIONS_REQUEST_WRITE_STORAGE -> {
                if (grantResults.isNotEmpty() && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED
                ) {
                    if (ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        openCamera()
                    }
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (!shouldShowRequestPermissionRationale(
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                            )
                        ) {
                            count++
                            if (count > 1) {
                                showMessageOKCancel(
                                    getString(R.string.writeStoragePermission)
                                ) { _, _ ->
                                    val intent = Intent()
                                    intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                                    val uri = Uri.fromParts(
                                        "package",
                                        this.packageName, null
                                    )
                                    intent.data = uri
                                    startActivity(intent)
                                }
                            }
                        }
                    }
                }
                return
            }
        }
    }

    override fun loadChatRequest(allChatRequestList: java.util.ArrayList<String>) {
        if (allChatRequestList.size > 0) {
            chats_rv.visibility = View.VISIBLE
            chatRequestAdapter?.updateListItems(allChatRequestList)
        } else {
            chats_rv.visibility = View.GONE
        }
    }

    override fun loadLabels(allLabelList: ArrayList<String>) {
        this.allLabelList.clear()
        if (allLabelList.size > 0) {
            this.allLabelList.addAll(allLabelList)
            labels_rv.visibility = View.VISIBLE
            labelAdapter?.updateListItems(allLabelList)
        } else {
            labels_rv.visibility = View.GONE
        }
    }

    override fun setQuestionImage(picOptionUri: Uri, imageLabel: String) {
        add_iv.load(R.drawable.ic_add)
        camera_iv.setImageURI(picOptionUri)
        this.picOptionUri = picOptionUri
        camera_iv.borderColor = resources.getColor(R.color.white)
        camera_iv.borderWidth = 2
        isOptionForQuestion = false
        this.imageLabel = imageLabel
    }

    private fun openCamera() {
        isOptionForQuestion = true
        deleteQuestionImageUri()
        CropImage.activity().setGuidelines(CropImageView.Guidelines.ON)
            .setAspectRatio(1, 1).start(this)
    }

    private fun showMessageOKCancel(message: String, okListener: (Any, Any) -> Unit) {
        AlertDialog.Builder(this)
            .setMessage(message).setPositiveButton(getString(R.string.ok_string), okListener)
            .setNegativeButton(getString(R.string.cancel_string), null).create().show()
    }

    override fun onResume() {
        super.onResume()
        checkNewAppVersionState()
        logd(getString(R.string.resume))
    }

    private fun checkNewAppVersionState() {
        appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability()
                == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS
            ) {
                showUpdate()
            }
        }
    }

    private fun showUpdate() {
        if (!updateBtmSheet.isAdded) {
            updateBtmSheet.show(supportFragmentManager, "btmSheet")
            updateBtmSheet.isCancelable = false
        }
    }

    private fun checkForUpdates() {
        appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
                showUpdate()
            }
        }
    }

    override fun startProfileAct() {
        hideLoading()
        openActivity(ProfileActivity::class.java)
    }

    override fun startLogin() {
        hideLoading()
        openActivity(SignInActivity::class.java)
    }

    override fun showLoading() {
        progressBar.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        progressBar.visibility = View.GONE
    }

    override fun stopAct() {
        finish()
    }

    override fun showUploadedSuccess() {
        addQuestion_et.setText("")
        deleteQuestionImageUri()
        Utility().hideSoftKeyboard(addQuestion_et)
    }

    override fun setUserProfileImage(imageUrl: String?) {
        imageUrl.whatIfNotNull(
            whatIf = {
                imageProgressBar.visibility = View.VISIBLE
                user_iv.load(imageUrl,
                    Coil.loader(),
                    builder = {
                        this.listener(object : Request.Listener {
                            override fun onError(data: Any, throwable: Throwable) {
                                imageProgressBar.visibility = View.GONE
                                user_iv.load(R.drawable.ic_default_pic)
                            }

                            override fun onSuccess(
                                data: Any,
                                source: coil.decode.DataSource
                            ) {
                                super.onSuccess(data, source)
                                imageProgressBar.visibility = View.GONE
                                user_iv.borderWidth = 2
                                user_iv.borderColor = resources.getColor(R.color.white)
                            }
                        })
                    })
            },
            whatIfNot = {
                showMessage(getString(R.string.something_went_wrong), 1)
            }
        )
    }

    @SuppressLint("SetTextI18n")
    override fun setUserName(name: String) {
        addQuestion_et.hint = getString(R.string.enter_your_question) + " $name"
    }

    override fun openAddProfileDetails() {
        if (!profileEditBtmSheet.isAdded) {
            profileEditBtmSheet.show(supportFragmentManager, "btmSheet")
            profileEditBtmSheet.isCancelable = false
        }
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == Activity.RESULT_OK) {
                if (isOptionForQuestion) {
                    this.picOptionUri = result.uri
                    presenter.checkIfHumanFace(picOptionUri!!)
                } else {
                    if (profileEditBtmSheet.isAdded) {
                        profileEditBtmSheet.getImageUri(result.uri)
                    }
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                showMessage(getString(R.string.went_wrong_image), 1)
            }
        }
    }

    override fun hideOptions() {

    }

    override fun showOptions() {

    }

    override fun clearQuestions() {
        this.allQues.clear()
        questionsAdapter!!.updateListItems(allQues)
    }

    override fun loadQuestions(allQuestionList: ArrayList<QuestionModel>) {
        this.allQues.clear()
        if (allQuestionList.size > 0) {
            recentQ_rv.visibility = View.VISIBLE
            allQues.addAll(allQuestionList)
            questionsAdapter!!.updateListItems(allQues)
        } else {
            recentQ_rv.visibility = View.GONE
        }
    }

    override fun onStop() {
        super.onStop()
        logd(getString(R.string.stop))
        presenter.removeListener()
    }

    fun openProfileDetails(askedBy: String) {
        if (!otherProfileOptions.isAdded) {
            otherProfileOptions.show(supportFragmentManager, "btmSheet")
            otherProfileOptions.setUserId(askedBy)
        }
    }

    override fun setVotes(voteType: Int, docId: String) {
        presenter.setVote(voteType, docId)
    }

    override fun showStats(docId: String) {
        showLoading()
        presenter.showStats(docId)
    }

    override fun showQuestionStats(docId: String) {
        runOnUiThread {
            if (!isFinishing) {
                if (!questionStatistics.isAdded) {
                    questionStatistics.show(supportFragmentManager, "btmSheet")
                    questionStatistics.setQuestionDocId(docId)
                } else {
                    questionStatistics.dismiss()
                    if (!questionStatistics.isAdded) {
                        questionStatistics.show(supportFragmentManager, "btmSheet")
                        questionStatistics.setQuestionDocId(docId)
                    }
                }
            }
        }
    }

    fun openComment(docId: String) {
        val bundle = Bundle()
        bundle.putString("questionId", docId)
        openActivity(CommentsAcrivity::class.java, "questionId", bundle)
    }

    fun checkForChat(docId: String, askedBy: String) {
        presenter.checkForChatRequest(docId, askedBy)
    }

    override fun showChatRequestOption(docId: String, askedBy: String) {
        MaterialDialog(this)
            .title(text = getString(R.string.chat_request))
            .message(text = "Request for one-to-one conversation?")
            .show {
                noAutoDismiss()
                icon(R.drawable.chat_iv)
                positiveButton(text = getString(R.string.yes)) {
                    presenter.callGenerateChatRequest(docId, askedBy)
                    dismiss()
                }
            }
    }

    fun showOptionForAcceptRequest(requestBy: String) {
        MaterialDialog(this)
            .title(text = getString(R.string.chat_request))
            .message(text = "Accept Chat Request for one-to-one conversation?")
            .show {
                noAutoDismiss()
                icon(R.drawable.chat_iv)
                positiveButton(text = getString(R.string.yes)) {
                    presenter.acceptChatRequest(requestBy)
                    dismiss()
                }
            }
    }

    override fun openChatWindow(chatWindowId: String) {
        val bundle = Bundle()
        bundle.putString("chatWindowId", chatWindowId)
        openActivity(ChatWindow::class.java, "chatWindowId", bundle)
    }

    fun updateQuestion(labelName: String) {
        presenter.callGetQuestionsForLabel(labelName.toLowerCase())
    }

    fun getAllQuestions() {
        presenter.getQuestions()
    }

}

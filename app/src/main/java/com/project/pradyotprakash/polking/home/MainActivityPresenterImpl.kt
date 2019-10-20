package com.project.pradyotprakash.polking.home

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon
import android.net.Uri
import android.os.Build
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.FirebaseFunctionsException
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.project.pradyotprakash.polking.R
import com.project.pradyotprakash.polking.profile.ProfileActivity
import com.project.pradyotprakash.polking.utility.QuestionModel
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.HashMap

class MainActivityPresenterImpl @Inject constructor() : MainActivityPresenter {

    private val allQuestionList = ArrayList<QuestionModel>()
    lateinit var mContext: Activity
    @Inject lateinit var mView: MainActivityView
    private lateinit var mAuth: FirebaseAuth
    private var currentUser: FirebaseUser? = null
    private lateinit var dataBase: FirebaseFirestore
    private lateinit var getQuestionDataBase: FirebaseFirestore
    private lateinit var uploadQuestionDataBase: FirebaseFirestore
    private lateinit var addVotesDataBase: FirebaseFirestore
    private lateinit var getbestfriendfirestore: FirebaseFirestore
    private lateinit var firebaseFunctions: FirebaseFunctions

    @SuppressLint("SimpleDateFormat")
    var dateFormat: SimpleDateFormat = SimpleDateFormat("yyyy/MM/dd")
    @SuppressLint("SimpleDateFormat")
    var timeFormat: SimpleDateFormat = SimpleDateFormat("HH:mm:ss")
    @SuppressLint("SimpleDateFormat")
    var dateTimeFormat: SimpleDateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")

    @Inject
    internal fun MainActivityPresenterImpl(activity: Activity) {
        mContext = activity
        mAuth = FirebaseAuth.getInstance()
        currentUser = mAuth.currentUser
        dataBase = FirebaseFirestore.getInstance()
        getQuestionDataBase = FirebaseFirestore.getInstance()
        uploadQuestionDataBase = FirebaseFirestore.getInstance()
        addVotesDataBase = FirebaseFirestore.getInstance()
        getbestfriendfirestore = FirebaseFirestore.getInstance()
        firebaseFunctions = FirebaseFunctions.getInstance()
    }

    override fun start() {

    }

    override fun stop() {

    }

    override fun isNetworkAvailable(): Boolean {
        return true
    }

    override fun isLoggedIn() {
        mView.showLoading()
        if (currentUser!=null) {
            mView.startProfileAct()
        } else {
            mView.startLogin()
        }
    }

    override fun getProfileData() {
        mView.showLoading()
        if (currentUser!=null) {
            dataBase.collection("users").document(currentUser!!.uid)
                .addSnapshotListener { snapshot, exception ->
                    if (exception != null) {
                        mView.showMessage(
                            "Something Went Wrong. ${exception.localizedMessage}", 1
                        )
                    }

                    if (snapshot != null && snapshot.exists()) {
                        mView.setUserProfileImage(snapshot.data!!["imageUrl"].toString())
                        mView.setUserName(snapshot.data!!["name"].toString())
                        mView.setNotificationIcon(snapshot.data!!["notificationCount"].toString())
                        mView.hideLoading()
                    } else {
                        mView.openAddProfileDetails()
                        mView.hideLoading()
                    }
                }
        } else {
            mView.hideLoading()
            mView.hideOptions()
        }
    }

    override fun uploadQuestionWithImage(question: String, picOptionUri: Uri) {
        mView.showLoading()
        if (currentUser != null) {
            val randomString = (1..32).map { ('0'..'z').toList().toTypedArray().random() }
                .joinToString("")
            val storage = FirebaseStorage.getInstance().reference
            val imagePath: StorageReference =
                storage.child("user_question_images")
                    .child("${mAuth.currentUser!!.uid}$randomString.jpg")
            imagePath.putFile(picOptionUri)
                .continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let { exception ->
                            mView.showMessage(
                                "Something Went Wrong. ${exception.localizedMessage}",
                                1
                            )
                            mView.hideLoading()
                            throw exception
                        }
                    }
                    return@Continuation imagePath.downloadUrl
                }).addOnCanceledListener {
                    mView.showMessage(mContext.getString(R.string.not_uploaded), 4)
                    mView.hideLoading()
                }.addOnFailureListener { exception ->
                    mView.showMessage(
                        "Something Went Wrong. ${exception.localizedMessage}"
                        , 1
                    )
                    mView.hideLoading()
                }.addOnCompleteListener { task ->
                    uploadQuestionImage(
                        task,
                        imagePath,
                        question,
                        "${mAuth.currentUser!!.uid}$randomString.jpg"
                    )
                }.addOnSuccessListener {
                    mView.showMessage(mContext.getString(R.string.save_properly), 3)
                }
        }
    }

    private fun uploadQuestionImage(
        task: Task<Uri>,
        imagePath: StorageReference,
        question: String,
        imageName: String
    ) {
        if (task.isComplete) {
            if (task.isSuccessful) {

                imagePath.downloadUrl.addOnSuccessListener { uri ->

                    val imageUrl = uri.toString()

                    addDataToDatabase(imageUrl, question, imageName)

                }.addOnFailureListener { exception ->
                    mView.showMessage(
                        "Something Went Wrong. ${exception.localizedMessage}",
                        1
                    )
                    mView.hideLoading()
                }.addOnCanceledListener {
                    mView.showMessage(mContext.getString(R.string.not_uploaded), 1)
                    mView.hideLoading()
                }

            } else if (task.isCanceled) {
                mView.showMessage(mContext.getString(R.string.not_uploaded), 1)
                mView.hideLoading()
            }
        } else {
            mView.showMessage(mContext.getString(R.string.something_went_wrong), 1)
            mView.hideLoading()
        }
    }

    private fun addDataToDatabase(
        imageUrl: String,
        question: String,
        imageName: String
    ) {
        val date = Date()
        val questionData = HashMap<String, Any>()
        questionData["question"] = question
        questionData["imageUrl"] = imageUrl
        questionData["imageName"] = imageName
        questionData["askedBy"] = currentUser!!.uid
        questionData["askedOn"] = dateTimeFormat.format(date)
        questionData["askedOnDate"] = dateFormat.format(date)
        questionData["askedOnTime"] = timeFormat.format(date)
        questionData["yesVote"] = "0"
        questionData["noVote"] = "0"

        dataBase.collection("question").add(questionData)
            .addOnSuccessListener {
                mView.hideLoading()
                mView.showUploadedSuccess()
                mView.showMessage("Successfully Uploaded.", 3)
            }.addOnFailureListener { exception ->
                mView.showMessage(
                    "Something Went Wrong. ${exception.localizedMessage}",
                    1
                )
                mView.hideLoading()
            }.addOnCanceledListener {
                mView.showMessage(mContext.getString(R.string.not_uploaded_question), 4)
                mView.hideLoading()
            }
    }

    override fun addAuthStateListener() {
        mAuth.addAuthStateListener { mAuth ->
            if (mAuth.currentUser != null) {
                currentUser = mAuth.currentUser
                getUserData()
                getQuestions()
                setDynamicShortcuts()
            } else {
                mView.hideOptions()
            }
        }
    }

    private fun setDynamicShortcuts() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            val shortcutManager = mContext.getSystemService(ShortcutManager::class.java)
            val profileIntent = Intent(mContext, ProfileActivity::class.java)
            profileIntent.action = "PROFILE_ACTIVITY"
            val profileShortcut = ShortcutInfo.Builder(mContext, "profile")
                .setShortLabel("My Profile")
                .setLongLabel("Open My Profile")
                .setIcon(Icon.createWithResource(mContext, R.drawable.ic_default_appcolor))
                .setIntent(profileIntent)
                .build()
            if (shortcutManager != null)
                shortcutManager.dynamicShortcuts = listOf(profileShortcut)
        }
    }

    private fun getUserData() {
        dataBase.collection("users").document(currentUser!!.uid).get()
            .addOnSuccessListener { result ->

            if (result.exists()) {
                mView.setUserProfileImage(result.data!!["imageUrl"].toString())
                mView.setUserName(result.data!!["name"].toString())
                mView.hideLoading()
            } else {
                mView.hideOptions()
            }

        }.addOnFailureListener { exception ->
            mView.showMessage(
                "Something Went Wrong. ${exception.localizedMessage}",
                1
            )
            mView.hideLoading()
        }.addOnCanceledListener {
            mView.showMessage(mContext.getString(R.string.not_uploaded_question), 4)
            mView.hideLoading()
        }
    }

    override fun removeListener() {
        mAuth.removeAuthStateListener {}
    }

    override fun uploadQuestion(question: String) {
        mView.showLoading()
        if (currentUser != null) {
            val date = Date()
            val questionData = HashMap<String, Any>()
            questionData["question"] = question
            questionData["askedBy"] = currentUser!!.uid
            questionData["askedOn"] = dateTimeFormat.format(date)
            questionData["askedOnDate"] = dateFormat.format(date)
            questionData["askedOnTime"] = timeFormat.format(date)
            questionData["yesVote"] = "0"
            questionData["noVote"] = "0"

            dataBase.collection("question").add(questionData)
                .addOnSuccessListener {
                mView.hideLoading()
                mView.showUploadedSuccess()
                mView.showMessage("Successfully Uploaded.", 3)
            }.addOnFailureListener { exception ->
                mView.showMessage(
                    "Something Went Wrong. ${exception.localizedMessage}",
                    1
                )
                mView.hideLoading()
            }.addOnCanceledListener {
                mView.showMessage(mContext.getString(R.string.not_uploaded_question), 4)
                mView.hideLoading()
            }

        } else {
            mView.hideOptions()
        }
    }

    override fun getQuestions() {

        mView.showLoading()

        dataBase.collection("question")
            .orderBy("askedOn", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    mView.showMessage(
                        "Something Went Wrong. ${exception.localizedMessage}", 1
                    )
                }

                allQuestionList.clear()

                try {
                    for (doc in snapshot!!) {
                        val docId = doc.id
                        val quesList: QuestionModel =
                            doc.toObject<QuestionModel>(QuestionModel::class.java).withId(docId)
                        this.allQuestionList.add(quesList)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    mView.showMessage(e.localizedMessage, 1)
                }

                if (allQuestionList.size > 0) {
                    mView.loadQuestions(allQuestionList)
                }

                mView.hideLoading()

            }
    }

    override fun setVote(voteType: Int, docId: String) {
        mView.showLoading()

        if (currentUser != null) {
            val voteData = HashMap<String, Any>()
            voteData["voteType"] = voteType
            voteData["questionId"] = docId

            addVotesDataBase
                .collection("users")
                .document(currentUser!!.uid)
                .collection("votes")
                .document(docId)
                .set(voteData).addOnSuccessListener {
                    mView.hideLoading()
                }.addOnFailureListener { exception ->
                    mView.showMessage(
                        "Something Went Wrong. ${exception.localizedMessage}",
                        1
                    )
                    mView.hideLoading()
                }.addOnCanceledListener {
                    mView.showMessage(mContext.getString(R.string.not_uploaded_question), 4)
                    mView.hideLoading()
                }

        } else {
            mView.hideOptions()
        }
    }

    override fun showStats(docId: String) {
        callStatsFunction(docId)
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    if (task.exception != null) {
                        val e = task.exception
                        if (e != null) {
                            if (e is FirebaseFunctionsException) {
                                mView.hideLoading()
                                mView.showMessage(
                                    "Something Went Wrong." +
                                            " ${e.localizedMessage}", 1
                                )
                            } else {
                                openStats(docId)
                            }
                        } else {
                            openStats(docId)
                        }
                    } else {
                        openStats(docId)
                    }
                } else {
                    openStats(docId)
                }
            }
    }

    private fun openStats(docId: String) {
        mView.hideLoading()
        mView.showQuestionStats(docId)
    }

    private fun callStatsFunction(docId: String): Task<String> {
        val data = HashMap<String, Any>()
        data["questionId"] = docId
        data["userId"] = mAuth.currentUser!!.uid

        return firebaseFunctions
            .getHttpsCallable("showQuestionStats")
            .call(data)
            .continueWith { task ->
                val result = task.result?.data as String
                result
            }
    }

}
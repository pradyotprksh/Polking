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
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions
import com.google.firebase.ml.vision.face.FirebaseVisionFaceLandmark
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.project.pradyotprakash.polking.R
import com.project.pradyotprakash.polking.profile.ProfileActivity
import com.project.pradyotprakash.polking.utility.QuestionModel
import com.skydoves.whatif.whatIfNotNull
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class MainActivityPresenterImpl @Inject constructor() : MainActivityPresenter {

    private val allQuestionList = ArrayList<QuestionModel>()
    private val allLabelList = ArrayList<String>()
    private val allChatRequestList = ArrayList<String>()
    lateinit var mContext: Activity
    @Inject lateinit var mView: MainActivityView
    private lateinit var mAuth: FirebaseAuth
    private var currentUser: FirebaseUser? = null
    private lateinit var dataBase: FirebaseFirestore
    private lateinit var addVotesDataBase: FirebaseFirestore
    private lateinit var getQuestionFirestore: FirebaseFirestore
    private lateinit var getbestfriendfirestore: FirebaseFirestore
    private lateinit var isChatRequestAlreadyMade: FirebaseFirestore
    private lateinit var firebaseFunctions: FirebaseFunctions
    private lateinit var labelsFirestore: FirebaseFirestore
    private lateinit var generateChatRequest: FirebaseFirestore

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
        firebaseFunctions = FirebaseFunctions.getInstance()
        currentUser = mAuth.currentUser
        dataBase = FirebaseFirestore.getInstance()
        labelsFirestore = FirebaseFirestore.getInstance()
        addVotesDataBase = FirebaseFirestore.getInstance()
        getbestfriendfirestore = FirebaseFirestore.getInstance()
        isChatRequestAlreadyMade = FirebaseFirestore.getInstance()
        generateChatRequest = FirebaseFirestore.getInstance()
        getQuestionFirestore = FirebaseFirestore.getInstance()
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
        currentUser.whatIfNotNull(
            whatIf = {
                mView.startProfileAct()
            },
            whatIfNot = {
                mView.startLogin()
            }
        )
    }

    override fun getProfileData() {
        mView.showLoading()
        currentUser.whatIfNotNull(
            whatIf = {
                dataBase.collection("users").document(currentUser!!.uid)
                    .addSnapshotListener { snapshot, exception ->
                        exception.whatIfNotNull {
                            mView.showMessage(
                                "Something Went Wrong. ${exception!!.localizedMessage}", 1
                            )
                        }

                        snapshot.whatIfNotNull {
                            if (snapshot!!.exists()) {
                                mView.setUserProfileImage(snapshot.data!!["imageUrl"].toString())
                                mView.setUserName(snapshot.data!!["name"].toString())
                                mView.setNotificationIcon(snapshot.data!!["notificationCount"].toString())
                                mView.hideLoading()
                            } else {
                                mView.openAddProfileDetails()
                                mView.hideLoading()
                            }
                        }
                    }
            },
            whatIfNot = {
                mView.hideLoading()
                mView.hideOptions()
            }
        )
    }

    override fun uploadQuestionWithImage(
        question: String,
        picOptionUri: Uri,
        imageLabel: java.util.ArrayList<String>
    ) {
        mView.showLoading()
        currentUser.whatIfNotNull(
            whatIf = {
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
                            "${mAuth.currentUser!!.uid}$randomString.jpg",
                            imageLabel
                        )
                    }.addOnSuccessListener {
                        mView.showMessage(mContext.getString(R.string.save_properly), 3)
                    }
            }
        )
    }

    override fun getImageLabels() {
        labelsFirestore
            .collection("labels")
            .addSnapshotListener { snapshot, _ ->
                allLabelList.clear()

                try {
                    for (doc in snapshot!!) {
                        allLabelList.add(doc.id)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                if (allLabelList.size > 0) {
                    mView.loadLabels(allLabelList)
                }

            }
    }

    override fun getRequestForChats() {
        currentUser.whatIfNotNull {
            dataBase
                .collection("request")
                .document(mAuth.currentUser!!.uid)
                .collection("messageRequest")
                .whereEqualTo("isCompleted", "false")
                .addSnapshotListener { snapshot, _ ->
                    allChatRequestList.clear()

                    try {
                        for (doc in snapshot!!) {
                            allChatRequestList.add(doc.id)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                    mView.loadChatRequest(allChatRequestList)

                }
        }
    }

    private fun uploadQuestionImage(
        task: Task<Uri>,
        imagePath: StorageReference,
        question: String,
        imageName: String,
        imageLabel: java.util.ArrayList<String>
    ) {
        if (task.isComplete) {
            if (task.isSuccessful) {

                imagePath.downloadUrl.addOnSuccessListener { uri ->

                    val imageUrl = uri.toString()

                    addDataToDatabase(imageUrl, question, imageName, imageLabel)

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
        imageName: String,
        imageLabel: java.util.ArrayList<String>
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
                addLabels(imageLabel, it.id, imageUrl)
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

    private fun addLabels(
        imageLabel: ArrayList<String>,
        id: String,
        imageUrl: String
    ) {
        currentUser.whatIfNotNull {
            for (label in imageLabel) {
                val labeldata = HashMap<String, Any>()
                labeldata["questionId"] = id
                labeldata["imageUrl"] = imageUrl
                labeldata["labelName"] = label
                labelsFirestore
                    .collection("labels")
                    .document(label.toLowerCase(Locale.ENGLISH))
                    .collection(id)
                    .add(labeldata)
                    .addOnCompleteListener {
                        val dummyLabeldata = HashMap<String, Any>()
                        labeldata["labelName"] = label
                        getbestfriendfirestore
                            .collection("labels")
                            .document(label.toLowerCase(Locale.ENGLISH))
                            .set(dummyLabeldata)
                    }
            }
        }
    }

    override fun callGenerateChatRequest(docId: String, askedBy: String) {
        mAuth.currentUser.whatIfNotNull {
            mView.showLoading()
            generateChatRequest
                .collection("request")
                .document(mAuth.currentUser!!.uid)
                .collection("messageRequest")
                .document(askedBy)
                .set(
                    hashMapOf(
                        "requestBy" to mAuth.currentUser!!.uid,
                        "requestTo" to askedBy,
                        "questionId" to docId,
                        "isRequestAccepted" to "false",
                        "isCompleted" to "false",
                        "isUserTyping" to "false"
                    )
                )
                .addOnCompleteListener {
                    getQuestionFirestore
                        .collection("request")
                        .document(askedBy)
                        .collection("messageRequest")
                        .document(mAuth.currentUser!!.uid)
                        .set(
                            hashMapOf(
                                "requestBy" to mAuth.currentUser!!.uid,
                                "requestTo" to askedBy,
                                "questionId" to docId,
                                "isCompleted" to "false",
                                "isRequestAccepted" to "false",
                                "isUserTyping" to "false"
                            )
                        )
                        .addOnCompleteListener {
                            mView.hideLoading()
                        }
                }
                .addOnFailureListener {
                    mView.hideLoading()
                }
        }
    }

    override fun acceptChatRequest(requestBy: String) {
        mAuth.currentUser.whatIfNotNull {
            mView.showLoading()
            val data = HashMap<String, Any>()
            data["isRequestAccepted"] = "true"
            generateChatRequest
                .collection("request")
                .document(mAuth.currentUser!!.uid)
                .collection("messageRequest")
                .document(requestBy)
                .update(data)
                .addOnCompleteListener {
                    getQuestionFirestore
                        .collection("request")
                        .document(requestBy)
                        .collection("messageRequest")
                        .document(mAuth.currentUser!!.uid)
                        .update(data)
                        .addOnCompleteListener {
                            mView.hideLoading()
                        }
                }
                .addOnFailureListener {
                    mView.hideLoading()
                }
        }
    }

    override fun checkForChatRequest(docId: String, askedBy: String) {
        mContext.whatIfNotNull {
            mAuth.currentUser.whatIfNotNull {
                isChatRequestAlreadyMade
                    .collection("request")
                    .document(mAuth.currentUser!!.uid)
                    .collection("messageRequest")
                    .document(askedBy)
                    .get()
                    .addOnSuccessListener { result ->
                        if (result.exists()) {

                            if (result.data!!["isCompleted"] == "true") {
                                dataBase
                                    .collection("request")
                                    .document(mAuth.currentUser!!.uid)
                                    .collection("messageRequest")
                                    .document(askedBy)
                                    .update(
                                        mapOf(
                                            "isCompleted" to "false"
                                        )
                                    ).addOnCompleteListener {
                                        addVotesDataBase
                                            .collection("request")
                                            .document(askedBy)
                                            .collection("messageRequest")
                                            .document(mAuth.currentUser!!.uid)
                                            .update(
                                                mapOf(
                                                    "isCompleted" to "false"
                                                )
                                            ).addOnCompleteListener {
                                                mView.openChatWindow(askedBy)
                                            }
                                    }
                            } else {
                                dataBase.collection("users").document(askedBy)
                                    .addSnapshotListener { snapshot, exception ->
                                        exception.whatIfNotNull {
                                            mView.showMessage(
                                                "Something Went Wrong. ${exception!!.localizedMessage}",
                                                1
                                            )
                                        }

                                        snapshot.whatIfNotNull {
                                            if (snapshot!!.exists()) {
                                                if (result["questionId"] == docId) {
                                                    mView.showMessage(
                                                        "Request already sent. " +
                                                                snapshot.data!!["name"].toString() +
                                                                " needs to accept your chat request to start this conversation",
                                                        2
                                                    )
                                                } else {
                                                    if (result["requestBy"] == askedBy) {
                                                        if (result["isRequestAccepted"] == "false") {
                                                            mView.showMessage(
                                                                "You already have a request from " +
                                                                        snapshot.data!!["name"].toString(),
                                                                2
                                                            )
                                                        } else {
                                                            mView.openChatWindow(askedBy)
                                                        }
                                                    } else {
                                                        mView.openChatWindow(askedBy)
                                                    }
                                                }
                                            } else {
                                                mView.showChatRequestOption(docId, askedBy)
                                            }
                                        }
                                    }
                            }

                        } else {
                            mView.showChatRequestOption(docId, askedBy)
                        }
                    }
                    .addOnFailureListener {
                        mView.showChatRequestOption(docId, askedBy)
                    }

            }
        }
    }

    override fun uploadQuestionWithImage(question: String, picOptionUri: Uri) {
        mView.showLoading()
        currentUser.whatIfNotNull(
            whatIf = {
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
        )
    }

    override fun checkIfHumanFace(picOptionUri: Uri) {
        mView.showLoading()
        val image: FirebaseVisionImage
        val options = FirebaseVisionFaceDetectorOptions.Builder()
            .setClassificationMode(FirebaseVisionFaceDetectorOptions.ACCURATE)
            .setLandmarkMode(FirebaseVisionFaceDetectorOptions.ALL_LANDMARKS)
            .setClassificationMode(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS)
            .setMinFaceSize(0.15f)
            .enableTracking()
            .build()
        try {
            image = FirebaseVisionImage.fromFilePath(mContext, picOptionUri)
            val detector = FirebaseVision.getInstance()
                .getVisionFaceDetector(options)
            detector.detectInImage(image)
                .addOnSuccessListener { faces ->
                    var isFaceDetected = false
                    for (face in faces) {

                        // If landmark detection was enabled (mouth, ears, eyes, cheeks, and
                        // nose available):
                        val leftEar = face.getLandmark(FirebaseVisionFaceLandmark.LEFT_EAR)
                        val rightEar = face.getLandmark(FirebaseVisionFaceLandmark.RIGHT_EAR)
                        val leftCheek = face.getLandmark(FirebaseVisionFaceLandmark.LEFT_CHEEK)
                        val leftEye = face.getLandmark(FirebaseVisionFaceLandmark.LEFT_EYE)
                        val rightEye = face.getLandmark(FirebaseVisionFaceLandmark.RIGHT_EYE)
                        val rightCheek = face.getLandmark(FirebaseVisionFaceLandmark.RIGHT_CHEEK)
                        val mouthLeft = face.getLandmark(FirebaseVisionFaceLandmark.MOUTH_LEFT)
                        val mouthBottom = face.getLandmark(FirebaseVisionFaceLandmark.MOUTH_BOTTOM)
                        val mouthRight = face.getLandmark(FirebaseVisionFaceLandmark.MOUTH_RIGHT)
                        val noseBase = face.getLandmark(FirebaseVisionFaceLandmark.NOSE_BASE)

                        if (leftEar != null || rightEar != null || leftCheek != null ||
                            leftEye != null
                            || rightEye != null || rightCheek != null || mouthLeft != null ||
                            mouthBottom != null || mouthRight != null || noseBase != null
                        ) {

                            if (leftEar?.position!!.x > 0 || rightEar?.position!!.x > 0 ||
                                leftCheek?.position!!.x > 0 || leftEye?.position!!.x > 0 ||
                                rightEye?.position!!.x > 0 || rightCheek?.position!!.x > 0 ||
                                mouthLeft?.position!!.x > 0 || mouthBottom?.position!!.x > 0 ||
                                mouthRight?.position!!.x > 0 || noseBase?.position!!.x > 0
                            ) {

                                if (leftEar.position.y > 0 || rightEar?.position!!.y > 0 ||
                                    leftCheek?.position!!.y > 0 || leftEye?.position!!.y > 0 ||
                                    rightEye?.position!!.y > 0 || rightCheek?.position!!.y > 0 ||
                                    mouthLeft?.position!!.y > 0 || mouthBottom?.position!!.y > 0 ||
                                    mouthRight?.position!!.y > 0 || noseBase?.position!!.y > 0
                                ) {

                                    isFaceDetected = true

                                }

                            }

                        } else {
                            isFaceDetected = false
                        }
                    }

                    if (isFaceDetected) {
                        mView.hideLoading()
                        mView.showMessage(
                            mContext.getString(R.string.faces_not_allowed),
                            2
                        )
                        mView.deleteQuestionImageUri()
                    } else {
                        checkForLabel(picOptionUri)
                    }

                }
                .addOnFailureListener { e ->
                    mView.hideLoading()
                    mView.showMessage(
                        "Something Went Wrong ${e.localizedMessage}. " +
                                "You can upload the image we will manually look for this.", 1
                    )
                    mView.setQuestionImage(picOptionUri)
                }
        } catch (e: IOException) {
            mView.hideLoading()
            mView.showMessage(
                "Something Went Wrong ${e.localizedMessage}. " +
                        "You can upload the image we will manually look for this.", 1
            )
            mView.setQuestionImage(picOptionUri)
            e.printStackTrace()
        }
    }

    private fun checkForLabel(picOptionUri: Uri) {
        val image: FirebaseVisionImage
        try {
            image = FirebaseVisionImage.fromFilePath(mContext, picOptionUri)
            val labeler = FirebaseVision.getInstance().onDeviceImageLabeler
            labeler.processImage(image)
                .addOnSuccessListener { labels ->
                    var imageLabel: ArrayList<String> = ArrayList()
                    for (label in labels) {
                        val text = label.text
                        imageLabel.add(text)
                    }
                    if (imageLabel.size > 0) {
                        mView.setQuestionImage(picOptionUri, imageLabel)
                    } else {
                        mView.setQuestionImage(picOptionUri)
                    }
                    mView.hideLoading()
                }
                .addOnFailureListener { e ->
                    e.printStackTrace()
                    mView.hideLoading()
                    mView.setQuestionImage(picOptionUri)
                }
        } catch (e: IOException) {
            mView.hideLoading()
            mView.setQuestionImage(picOptionUri)
            e.printStackTrace()
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
            mAuth.currentUser.whatIfNotNull(
                whatIf = {
                    currentUser = mAuth.currentUser
                    mView.clearQuestions()
                    getUserData()
                    getQuestions()
                    getRequestForChats()
                    setDynamicShortcuts()
                },
                whatIfNot = {
                    mView.hideOptions()
                }
            )
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
            shortcutManager.whatIfNotNull {
                shortcutManager!!.dynamicShortcuts = listOf(profileShortcut)
            }
        }
    }

    private fun getUserData() {
        dataBase.collection("users").document(currentUser!!.uid)
            .addSnapshotListener { snapshot, exception ->
                exception.whatIfNotNull {
                    mView.showMessage(
                        "Something Went Wrong. ${exception!!.localizedMessage}", 1
                    )
                }

                snapshot.whatIfNotNull(
                    whatIf = {
                        if (snapshot!!.exists()) {
                            mView.setUserProfileImage(snapshot.data!!["imageUrl"].toString())
                            mView.setUserName(snapshot.data!!["name"].toString())
                            mView.hideLoading()
                        } else {
                            mView.openAddProfileDetails()
                            mView.hideLoading()
                        }
                    },
                    whatIfNot = {
                        mView.openAddProfileDetails()
                        mView.hideLoading()
                    }
                )
            }
    }

    override fun removeListener() {
        mAuth.removeAuthStateListener {}
    }

    override fun uploadQuestion(question: String) {
        mView.showLoading()
        currentUser.whatIfNotNull(
            whatIf = {
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
            },
            whatIfNot = {
                mView.hideOptions()
            }
        )
    }

    override fun getQuestions() {

        mView.showLoading()

        dataBase.collection("question")
            .orderBy("askedOn", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, exception ->
                exception.whatIfNotNull {
                    mView.showMessage(
                        "Something Went Wrong. ${exception!!.localizedMessage}", 1
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

                mView.loadQuestions(allQuestionList)

                mView.hideLoading()

            }
    }

    override fun setVote(voteType: Int, docId: String) {
        mView.showLoading()
        currentUser.whatIfNotNull(
            whatIf = {
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
            },
            whatIfNot = {
                mView.hideOptions()
            }
        )
    }

    override fun showStats(docId: String) {
        callStatsFunction(docId)
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    task.exception.whatIfNotNull(
                        whatIf = {
                            val e = task.exception
                            e.whatIfNotNull(
                                whatIf = {
                                    if (e is FirebaseFunctionsException) {
                                        mView.hideLoading()
                                        openStats(docId)
                                    } else {
                                        openStats(docId)
                                    }
                                },
                                whatIfNot = {
                                    openStats(docId)
                                }
                            )
                        },
                        whatIfNot = {
                            openStats(docId)
                        }
                    )
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
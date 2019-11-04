package com.project.pradyotprakash.polking.questionStats

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.Coil
import coil.api.load
import coil.request.Request
import coil.transform.BlurTransformation
import coil.transform.GrayscaleTransformation
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.checkbox.checkBoxPrompt
import com.afollestad.materialdialogs.checkbox.isCheckPromptChecked
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.FirebaseFunctionsException
import com.google.firebase.storage.FirebaseStorage
import com.project.pradyotprakash.polking.R
import com.project.pradyotprakash.polking.comment.CommentsAcrivity
import com.project.pradyotprakash.polking.message.ShowMessage
import com.project.pradyotprakash.polking.profileDetails.ProfileEditView
import com.project.pradyotprakash.polking.utility.TransparentBottomSheet
import com.project.pradyotprakash.polking.utility.VotesModel
import com.project.pradyotprakash.polking.utility.logd
import com.project.pradyotprakash.polking.utility.openActivity
import com.skydoves.whatif.whatIfNotNull
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.question_stats_btm_sheet.view.*
import rm.com.longpresspopup.*
import javax.inject.Inject

class QuestionStatistics @Inject constructor() : TransparentBottomSheet(), ProfileEditView,
    PopupInflaterListener, PopupStateListener,
    PopupOnHoverListener {

    private lateinit var askedBy: String
    private var voteType: String = "-1"
    private var questionId: String = ""
    private lateinit var imageName: String
    private lateinit var mAuth: FirebaseAuth
    private lateinit var getQuestionFirestore: FirebaseFirestore
    private lateinit var getQuestionUserFirestore: FirebaseFirestore
    private lateinit var getUserVoteFirestore: FirebaseFirestore
    private lateinit var getSimilarVoteFirestore: FirebaseFirestore
    private lateinit var firebaseFunctions: FirebaseFunctions
    private val allVoteList = ArrayList<VotesModel>()
    private val voteList = ArrayList<VotesModel>()
    private var votesAdapter: VotesAdapter? = null
    lateinit var messageBtmSheet: ShowMessage
    private val allNoVoteList = ArrayList<VotesModel>()
    private val noVoteList = ArrayList<VotesModel>()
    private var noVotesAdapter: VotesAdapter? = null
    private var question_image: ImageView? = null

    companion object {
        fun newInstance(): QuestionStatistics =
            QuestionStatistics().apply {

            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        AndroidInjection.inject(this.activity)
        val view = inflater.inflate(R.layout.question_stats_btm_sheet, container, false)

        dialog!!.setOnShowListener { dialog ->
            val bottomSheetDialog: BottomSheetDialog = dialog as BottomSheetDialog
            val bottomSheetInternal =
                bottomSheetDialog.findViewById<FrameLayout>(R.id.design_bottom_sheet)
            bottomSheetInternal.whatIfNotNull {
                BottomSheetBehavior.from<View>(bottomSheetInternal).state =
                    BottomSheetBehavior.STATE_EXPANDED
            }
        }

        activity!!.logd(getString(R.string.profilebottomsheet))

        initView(view)

        return view
    }

    private fun initView(view: View) {
        initVariables()

        initAdapter(view)

        setOnClickListners(view)

        getQuestionData(view)
    }

    private fun initAdapter(view: View) {
        allVoteList.clear()
        votesAdapter = VotesAdapter(context!!, activity!!)
        view.userYesVotesRv.setHasFixedSize(true)
        view.userYesVotesRv.layoutManager =
            LinearLayoutManager(context!!, RecyclerView.HORIZONTAL, false)
        view.userYesVotesRv.adapter = votesAdapter

        allNoVoteList.clear()
        noVotesAdapter = VotesAdapter(context!!, activity!!)
        view.userNoVotesRv.setHasFixedSize(true)
        view.userNoVotesRv.layoutManager =
            LinearLayoutManager(context!!, RecyclerView.HORIZONTAL, false)
        view.userNoVotesRv.adapter = noVotesAdapter
    }

    private fun getVotes(view: View) {
        context.whatIfNotNull(
            whatIf = {
                mAuth.currentUser.whatIfNotNull(
                    whatIf = {
                        if (questionId != "") {
                            if (mAuth.currentUser!!.uid != askedBy) {
                                getSimilarVotes(view)
                            } else {
                                getYesVoteIfSameUser(view)
                                getNoVoteIfSameUser(view)
                            }
                        } else {
                            stopAct()
                        }
                    },
                    whatIfNot = {
                        stopAct()
                    }
                )
            },
            whatIfNot = {
                stopAct()
            }
        )
    }

    private fun getSimilarVotes(view: View) {
        val voteText: String
        if (voteType != "-1") {
            voteText = if (voteType == "1") {
                "yesVotes"
            } else {
                "noVotes"
            }
            getSimilarVotesList(view, voteText)
        } else {
            getYesVoteIfSameUser(view)
            getNoVoteIfSameUser(view)
        }
    }

    private fun getSimilarVotesList(view: View, voteText: String) {
        getSimilarVoteFirestore.collection(questionId)
            .document(mAuth.currentUser!!.uid)
            .collection(voteText)
            .addSnapshotListener { snapshot, exception ->
                exception.whatIfNotNull {
                    showMessage(
                        "Something Went Wrong. ${exception!!.localizedMessage}", 1
                    )
                }

                voteList.clear()

                try {
                    for (doc in snapshot!!) {
                        val docId = doc.id
                        val voteList: VotesModel =
                            doc.toObject<VotesModel>(VotesModel::class.java)
                                .withId(docId)
                        if (mAuth.currentUser!!.uid != voteList.votedBy) {
                            this.voteList.add(voteList)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    showMessage(e.localizedMessage, 1)
                }

                context.whatIfNotNull {
                    if (voteList.size > 0) {
                        view.votesUserTv.visibility = View.VISIBLE
                        view.votesUserTv.text =
                            getString(R.string.people_who_think_ike_you)
                        this.allVoteList.clear()
                        this.allVoteList.addAll(voteList)
                        votesAdapter?.updateListItems(allVoteList)
                    } else {
                        view.votesUserTv.visibility = View.GONE
                    }
                }

            }
    }

    private fun getNoVoteIfSameUser(view: View) {
        context.whatIfNotNull {
            this.allNoVoteList.clear()
            getNoVotesList(view)
        }
    }

    private fun getNoVotesList(view: View) {
        getSimilarVoteFirestore.collection(questionId)
            .document(mAuth.currentUser!!.uid)
            .collection("noVotes")
            .addSnapshotListener { snapshot, exception ->
                exception.whatIfNotNull {
                    showMessage(
                        "Something Went Wrong. ${exception!!.localizedMessage}", 1
                    )
                }

                noVoteList.clear()

                try {
                    for (doc in snapshot!!) {
                        val docId = doc.id
                        val voteList: VotesModel =
                            doc.toObject<VotesModel>(VotesModel::class.java).withId(docId)
                        if (mAuth.currentUser!!.uid != voteList.votedBy) {
                            this.noVoteList.add(voteList)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    showMessage(e.localizedMessage, 1)
                }

                context.whatIfNotNull {
                    if (noVoteList.size > 0) {
                        view.votesUserTv2.visibility = View.VISIBLE
                        view.votesUserTv2.text = getString(R.string.voted_no_for_your_question)
                        this.allNoVoteList.clear()
                        this.allNoVoteList.addAll(noVoteList)
                        noVotesAdapter?.updateListItems(allNoVoteList)
                    } else {
                        view.votesUserTv2.visibility = View.GONE
                    }
                }

            }
    }

    private fun getYesVoteIfSameUser(view: View) {
        context.whatIfNotNull {
            this.allVoteList.clear()
            getYesVotesList(view)
        }
    }

    private fun getYesVotesList(view: View) {
        getSimilarVoteFirestore.collection(questionId)
            .document(mAuth.currentUser!!.uid)
            .collection("yesVotes")
            .addSnapshotListener { snapshot, exception ->
                exception.whatIfNotNull {
                    showMessage(
                        "Something Went Wrong. ${exception!!.localizedMessage}", 1
                    )
                }

                voteList.clear()

                try {
                    for (doc in snapshot!!) {
                        val docId = doc.id
                        val voteList: VotesModel =
                            doc.toObject<VotesModel>(VotesModel::class.java).withId(docId)
                        if (mAuth.currentUser!!.uid != voteList.votedBy) {
                            this.voteList.add(voteList)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    showMessage(e.localizedMessage, 1)
                }

                context.whatIfNotNull {
                    if (voteList.size > 0) {
                        view.votesUserTv.visibility = View.VISIBLE
                        view.votesUserTv.text = getString(R.string.voted_yes_for_your_question)
                        this.allVoteList.clear()
                        this.allVoteList.addAll(voteList)
                        votesAdapter?.updateListItems(allVoteList)
                    } else {
                        view.votesUserTv.visibility = View.GONE
                    }
                }

            }
    }

    private fun getUserVote(view: View) {
        if (mAuth.currentUser!!.uid != askedBy) {
            context.whatIfNotNull(
                whatIf = {
                    mAuth.currentUser.whatIfNotNull(
                        whatIf = {
                            if (questionId != "") {
                                getUserVotes(view)
                            } else {
                                stopAct()
                            }
                        },
                        whatIfNot = {
                            stopAct()
                        }
                    )
                },
                whatIfNot = {
                    stopAct()
                }
            )
            dontShowDeleteOption(view)
        } else {
            showDeleteOption(view)
            getVotes(view)
        }
    }

    private fun dontShowDeleteOption(view: View) {
        view.userVote.isEnabled = false
        view.userVote.isClickable = false
    }

    private fun showDeleteOption(view: View) {
        view.userVote.text = getString(R.string.delete)
        view.userVote.setChipBackgroundColorResource(R.color.disagree_color)
        view.userVote.isEnabled = true
        view.userVote.isClickable = true
    }

    private fun getUserVotes(view: View) {
        getUserVoteFirestore
            .collection("users")
            .document(mAuth.currentUser!!.uid)
            .collection("votes")
            .document(questionId)
            .get()
            .addOnCanceledListener {
                showMessage(
                    "Something Went Wrong. The request was cancelled.", 1
                )
                relaodData(view)
            }
            .addOnFailureListener { exception ->
                showMessage(
                    "Something Went Wrong. ${exception.localizedMessage}", 1
                )
                relaodData(view)
            }
            .addOnSuccessListener { result ->
                if (result.exists()) {

                    context.whatIfNotNull {
                        voteType = result.get("voteType").toString()
                        if (voteType == "1") {
                            view.userVote.text = getString(R.string.voted_yes)
                            view.userVote.setChipBackgroundColorResource(R.color.agree_color)
                        } else {
                            view.userVote.text = getString(R.string.voted_no)
                            view.userVote.setChipBackgroundColorResource(R.color.disagree_color)
                        }
                    }

                    getVotes(view)
                } else {
                    stopAct()
                }
            }
    }

    private fun relaodData(view: View) {
        view.reloadData.visibility = View.VISIBLE
    }

    private fun getQuestionData(view: View) {
        context.whatIfNotNull(
            whatIf = {
                questionId.whatIfNotNull(
                    whatIf = {
                        if (questionId != "") {
                            getQuestionDataFirestore(view)
                        } else {
                            stopAct()
                        }
                    },
                    whatIfNot = {
                        stopAct()
                    }
                )
            },
            whatIfNot = {
                stopAct()
            }
        )
    }

    private fun getQuestionDataFirestore(view: View) {
        getQuestionFirestore.collection("question").document(questionId)
            .addSnapshotListener { snapshot, exception ->

                exception.whatIfNotNull {
                    showMessage(
                        "Something Went Wrong. ${exception!!.localizedMessage}", 1
                    )
                }

                snapshot.whatIfNotNull {
                    if (snapshot!!.exists()) {
                        setQuestionData(view, snapshot)
                    }
                }

            }
    }

    private fun setQuestionData(view: View, snapshot: DocumentSnapshot) {
        try {
            askedBy = snapshot.data!!["askedBy"].toString()
            val askedOn = snapshot.data!!["askedOn"].toString()
            val noVote = snapshot.data!!["noVote"].toString()
            val question = snapshot.data!!["question"].toString()
            val yesVote = snapshot.data!!["yesVote"].toString()
            imageName = snapshot.data!!["imageName"].toString()
            val imageUrl = snapshot.data!!["imageUrl"]
            val totalVote = (yesVote.toInt() + noVote.toInt())
            view.question_tv.text = question
            view.totalVoteTv.text = (yesVote.toInt() + noVote.toInt()).toString()
            view.yesTv.text = "$yesVote out of $totalVote people said YES"
            view.noTv.text = "$noVote out of $totalVote people said NO"

            view.yesSlider.startText = ""
            view.yesSlider.endText = ""
            view.yesSlider.position =
                (yesVote.toFloat() / (yesVote.toFloat() + noVote.toFloat()))
            view.yesSlider.bubbleText = "YES"

            view.noSlider.startText = ""
            view.noSlider.endText = ""
            view.noSlider.position =
                (noVote.toFloat() / (yesVote.toFloat() + noVote.toFloat()))
            view.noSlider.bubbleText = "NO"

            view.yesSlider.positionListener = {
                view.yesSlider.bubbleText = "YES"
            }

            view.yesSlider.endTrackingListener = {
                view.yesSlider.position =
                    (yesVote.toFloat() / (yesVote.toFloat() + noVote.toFloat()))
            }

            view.noSlider.positionListener = {
                view.noSlider.bubbleText = "NO"
            }

            view.noSlider.endTrackingListener = {
                view.noSlider.position =
                    (noVote.toFloat() / (yesVote.toFloat() + noVote.toFloat()))
            }

            imageUrl.whatIfNotNull(
                whatIf = {
                    setQuestionImage(view, imageUrl.toString())
                    setPopUpImageRegister(view, imageUrl.toString())
                },
                whatIfNot = {
                    view.question_image_Iv.visibility = View.GONE
                    view.question_loading.visibility = View.GONE
                }
            )

            getUserData(askedBy, view)

            getUserVote(view)
        } catch (exception: Exception) {
            showMessage(
                "Something Went Wrong. ${exception.localizedMessage}", 1
            )
        }
    }

    private fun setPopUpImageRegister(view: View, imageUrl: String) {
        val popUp: LongPressPopup = LongPressPopupBuilder(context)
            .setTarget(view.question_image_Iv)
            .setPopupView(R.layout.question_image_popup, this)
            .setLongPressDuration(2000)
            .setTag(imageUrl)
            .setDismissOnLongPressStop(true)
            .setDismissOnTouchOutside(true)
            .setDismissOnBackPressed(true)
            .setCancelTouchOnDragOutsideView(true)
            .setPopupListener(this)
            .setAnimationType(LongPressPopup.ANIMATION_TYPE_FROM_CENTER)
            .build()
        popUp.register()
    }

    private fun setQuestionImage(view: View, imageUrl: String) {
        context.whatIfNotNull {
            view.question_image_Iv.visibility = View.VISIBLE
            view.question_image_Iv.load(imageUrl,
                Coil.loader(),
                builder = {
                    mAuth.currentUser.whatIfNotNull(
                        whatIf = {

                        },
                        whatIfNot = {
                            this.transformations(
                                GrayscaleTransformation(),
                                BlurTransformation(context!!)
                            )
                        })
                    this.listener(object : Request.Listener {
                        override fun onError(data: Any, throwable: Throwable) {
                            super.onError(data, throwable)
                            view.question_loading.visibility = View.GONE
                        }

                        override fun onStart(data: Any) {
                            super.onStart(data)
                            view.question_loading.visibility = View.VISIBLE
                        }

                        override fun onSuccess(
                            data: Any,
                            source: coil.decode.DataSource
                        ) {
                            super.onSuccess(data, source)
                            view.question_loading.visibility = View.GONE
                        }
                    })
                })
        }
    }

    override fun onViewInflated(popupTag: String?, root: View?) {
        question_image = root?.findViewById(R.id.question_image)
    }

    override fun onPopupDismiss(popupTag: String?) {

    }

    override fun onPopupShow(popupTag: String?) {
        question_image.whatIfNotNull {
            popupTag.whatIfNotNull {
                context.whatIfNotNull {
                    question_image!!.load(popupTag,
                        Coil.loader(),
                        builder = {
                            mAuth.currentUser.whatIfNotNull(
                                whatIf = {

                                },
                                whatIfNot = {
                                    this.transformations(
                                        GrayscaleTransformation(),
                                        BlurTransformation(context!!)
                                    )
                                })
                        })
                }
            }
        }
    }

    override fun onHoverChanged(view: View?, isHovered: Boolean) {

    }

    private fun getUserData(askedBy: String, view: View) {
        getQuestionUserFirestore.collection("users").document(askedBy)
            .addSnapshotListener { snapshot, exception ->

                exception.whatIfNotNull {
                    showMessage(
                        "Something Went Wrong. ${exception!!.localizedMessage}", 1
                    )
                }

                snapshot.whatIfNotNull {
                    if (snapshot!!.exists()) {
                        try {
                            setUserData(snapshot, view)
                        } catch (exception: Exception) {
                            showMessage(
                                "Something Went Wrong. ${exception.localizedMessage}", 1
                            )
                        }
                    }
                }

            }
    }

    private fun setUserData(snapshot: DocumentSnapshot, view: View) {
        context.whatIfNotNull {
            view.user_iv.load(snapshot.data!!["imageUrl"].toString(),
                Coil.loader(),
                builder = {
                    mAuth.currentUser.whatIfNotNull(
                        whatIf = {

                        },
                        whatIfNot = {
                            this.transformations(
                                GrayscaleTransformation(),
                                BlurTransformation(context!!)
                            )
                        })
                    this.listener(object : Request.Listener {
                        override fun onError(data: Any, throwable: Throwable) {
                            view.user_iv.load(R.drawable.ic_default_appcolor)
                        }

                        override fun onSuccess(
                            data: Any,
                            source: coil.decode.DataSource
                        ) {
                            super.onSuccess(data, source)
                            view.user_iv.borderColor =
                                context!!.resources.getColor(R.color.colorPrimary)
                            view.user_iv.borderWidth = 2
                        }
                    })
                })

        }
        view.username_tv.text = snapshot.data!!["name"].toString()
    }

    private fun setOnClickListners(view: View) {
        view.back_tv.setOnClickListener {
            dismiss()
        }

        view.reloadData.setOnClickListener {
            getQuestionData(view)
            getUserVote(view)
            getVotes(view)
            view.reloadData.visibility = View.GONE
        }

        view.userVote.setOnClickListener {
            if (view.userVote.isEnabled && view.userVote.isClickable) {
                showDeleteDialog(view)
            } else {
                return@setOnClickListener
            }
        }

        view.seeComnets_tv.setOnClickListener {
            if (context is CommentsAcrivity) {
                dismiss()
            } else {
                context.whatIfNotNull {
                    val bundle = Bundle()
                    bundle.putString("questionId", questionId)
                    it.openActivity(CommentsAcrivity::class.java, "questionId", bundle)
                }
            }
        }
    }

    private fun showDeleteDialog(view: View) {
        context.whatIfNotNull {
            MaterialDialog(context!!)
                .title(text = getString(R.string.delete_question))
                .message(text = getString(R.string.delete_confirmation))
                .show {
                    noAutoDismiss()
                    checkBoxPrompt(text = getString(R.string.checked_this)) {}
                    icon(R.drawable.ic_delete_forever)
                    negativeButton(text = getString(R.string.delete_final)) { dialog ->
                        val isChecked = dialog.isCheckPromptChecked()
                        if (isChecked) {
                            callDeleteQuestionFunction(questionId, imageName, view)
                            dismiss()
                        } else {
                            checkBoxPrompt(text = getString(R.string.cehck_before_deleting)) {}
                        }
                    }
                }
        }
    }

    private fun callDeleteQuestionFunction(
        questionId: String,
        imageName: String,
        view: View
    ) {
        mAuth.currentUser.whatIfNotNull(
            whatIf = {
                view.progressBar8.visibility = View.VISIBLE
                callDeleteFunctions(questionId, imageName, mAuth.currentUser!!.uid)
                    .addOnCompleteListener { task ->
                        if (!task.isSuccessful) {
                            task.exception.whatIfNotNull(
                                whatIf = {
                                    val e = task.exception
                                    e.whatIfNotNull(
                                        whatIf = {
                                            if (e is FirebaseFunctionsException) {
                                                view.progressBar8.visibility = View.GONE
                                                showMessage(
                                                    "Something Went Wrong." +
                                                            " ${e.localizedMessage}", 1
                                                )
                                            } else {
                                                deleteQuestion(imageName, view)
                                            }
                                        },
                                        whatIfNot = {
                                            deleteQuestion(imageName, view)
                                        }
                                    )
                                },
                                whatIfNot = {
                                    deleteQuestion(imageName, view)
                                }
                            )
                        } else {
                            deleteQuestion(imageName, view)
                        }
                    }
            },
            whatIfNot = {
                showMessage(getString(R.string.something_went_wring_oops), 1)
            }
        )
    }

    private fun deleteQuestion(imageName: String, view: View) {
        if (imageName != "" && imageName != "null") {
            val storage = FirebaseStorage.getInstance().reference
            val desertRef = storage.child("user_question_images/$imageName")
            desertRef.delete().addOnSuccessListener {
                view.progressBar8.visibility = View.GONE
                showMessage(getString(R.string.deleted_successfully), 3)
                dismiss()
            }.addOnFailureListener { exception ->
                view.progressBar8.visibility = View.GONE
                exception.whatIfNotNull {
                    showMessage(
                        "Something Went Wrong. ${exception.localizedMessage}",
                        1
                    )
                }
            }
        } else {
            view.progressBar8.visibility = View.GONE
            showMessage(getString(R.string.deleted_successfully), 3)
            dismiss()
        }
    }

    private fun callDeleteFunctions(questionId: String, imageName: String, uid: String)
            : Task<String> {
        val data = HashMap<String, Any>()
        data["userId"] = uid
        data["questionId"] = questionId

        return firebaseFunctions
            .getHttpsCallable("deleteQuestion")
            .call(data)
            .continueWith { task ->
                val result = task.result?.data as String
                result
            }
    }

    private fun initVariables() {
        mAuth = FirebaseAuth.getInstance()
        getQuestionFirestore = FirebaseFirestore.getInstance()
        getQuestionUserFirestore = FirebaseFirestore.getInstance()
        getUserVoteFirestore = FirebaseFirestore.getInstance()
        getSimilarVoteFirestore = FirebaseFirestore.getInstance()
        firebaseFunctions = FirebaseFunctions.getInstance()
    }

    override fun showLoading() {

    }

    override fun hideLoading() {

    }

    override fun stopAct() {
        dismiss()
    }

    override fun showMessage(message: String, type: Int) {
        messageBtmSheet = ShowMessage.newInstance()
        if (!messageBtmSheet.isAdded) {
            messageBtmSheet.show(childFragmentManager, "btmSheet")
            messageBtmSheet.setMessage(message, type)
        } else {
            messageBtmSheet.dismiss()
            Handler().postDelayed({
                if (!messageBtmSheet.isAdded) {
                    messageBtmSheet.show(childFragmentManager, "btmSheet")
                    messageBtmSheet.setMessage(message, type)
                }
            }, 1500)
        }
    }

    fun setQuestionDocId(docId: String) {
        this.questionId = docId
    }
}
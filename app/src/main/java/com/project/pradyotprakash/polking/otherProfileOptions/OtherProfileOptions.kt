package com.project.pradyotprakash.polking.otherProfileOptions

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.DisplayMetrics
import android.view.*
import android.widget.FrameLayout
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.Coil
import coil.api.load
import coil.request.Request
import coil.transform.BlurTransformation
import coil.transform.GrayscaleTransformation
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.project.pradyotprakash.polking.R
import com.project.pradyotprakash.polking.home.adapter.QuestionsAdapter
import com.project.pradyotprakash.polking.message.ShowMessage
import com.project.pradyotprakash.polking.profileDetails.ProfileEditView
import com.project.pradyotprakash.polking.utility.QuestionModel
import com.project.pradyotprakash.polking.utility.TransparentBottomSheet
import com.project.pradyotprakash.polking.utility.Utility
import com.project.pradyotprakash.polking.utility.logd
import com.skydoves.whatif.whatIfNotNull
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.other_profile_options_btm_sheet.view.*
import javax.inject.Inject

class OtherProfileOptions @Inject constructor() : TransparentBottomSheet(), ProfileEditView {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var addfriendfirestore: FirebaseFirestore
    lateinit var messageBtmSheet: ShowMessage
    private lateinit var askedBy: String
    private lateinit var mAuth: FirebaseAuth
    private val allQuestionList = ArrayList<QuestionModel>()
    private var questionsAdapter: QuestionsAdapter? = null
    private val allQues = ArrayList<QuestionModel>()

    companion object {
        fun newInstance(): OtherProfileOptions =
            OtherProfileOptions().apply {

            }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        AndroidInjection.inject(this.activity)
        val view = inflater.inflate(R.layout.other_profile_options_btm_sheet, container, false)

        dialog!!.setOnShowListener { dialog ->
            val bottomSheetDialog: BottomSheetDialog = dialog as BottomSheetDialog
            val bottomSheetInternal =
                bottomSheetDialog.findViewById<FrameLayout>(R.id.design_bottom_sheet)
            bottomSheetInternal.whatIfNotNull {
                BottomSheetBehavior.from<View>(bottomSheetInternal).state =
                    BottomSheetBehavior.STATE_EXPANDED
            }
        }

        activity!!.logd(getString(R.string.otherprofiledetailsbottomsheet))

        initView(view)

        return view
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun initView(view: View) {
        initvariables()

        setAdapters(view)

        getUserData(view)

        setOnClickListner(view)

        setBackgroundImageMeasurement(view)
    }

    private fun setOnClickListner(view: View) {
        view.back_tv.setOnClickListener {
            dismiss()
        }

        view.questionVal_tv.setOnClickListener {
            doQuestionWork(view)
        }

        view.question_tv.setOnClickListener {
            doQuestionWork(view)
        }
    }

    private fun initvariables() {
        mAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        addfriendfirestore = FirebaseFirestore.getInstance()
    }

    private fun setAdapters(view: View) {
        questionsAdapter = QuestionsAdapter(context!!, activity!!)
        view.questions_rv.setHasFixedSize(true)
        view.questions_rv.layoutManager =
            LinearLayoutManager(context!!, RecyclerView.HORIZONTAL, false)
        view.questions_rv.adapter = questionsAdapter
    }

    private fun setBackgroundImageMeasurement(view: View) {
        context.whatIfNotNull {
            val windMang: WindowManager =
                context!!.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val display: Display = windMang.defaultDisplay
            val metrics = DisplayMetrics()
            display.getMetrics(metrics)
            val width = metrics.widthPixels
            val height = metrics.heightPixels

            view.background_image.minimumWidth = width
            view.background_image.maxWidth = width
            view.background_image.maxHeight = height
            view.background_image.minimumWidth = height
        }
    }

    private fun doQuestionWork(view: View) {
        if (view.questions_rv.visibility == View.VISIBLE) {
            view.questions_rv.visibility = View.GONE
        } else {
            if (allQues.size > 0) {
                view.questions_rv.visibility = View.VISIBLE
                view.questions_rv.startAnimation(Utility().inFromRightAnimation())
            } else {
                view.questions_rv.visibility = View.GONE
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun getUserData(view: View) {
        context.whatIfNotNull(
            whatIf = {
                view.progressBar5.visibility = View.VISIBLE
                if (askedBy.isEmpty()) {
                    view.progressBar5.visibility = View.GONE
                    dismiss()
                } else {
                    // get selected user questions
                    getUserQuestionsList()

                    // get user data
                    if (isAdded) {
                        getUserBackground(view)
                        getUserDetails(view)
                    }
                }
            },
            whatIfNot = {
                dismiss()
            }
        )
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private fun getUserDetails(view: View) {
        firestore.collection("users").document(askedBy)
            .addSnapshotListener { snapshot, exception ->
                exception.whatIfNotNull {
                    showMessage(
                        "Something Went Wrong. ${exception!!.localizedMessage}", 1
                    )
                }

                if (isAdded && !isDetached) {
                    snapshot.whatIfNotNull(
                        whatIf = {
                            if (snapshot!!.exists()) {
                                if (snapshot.data!!["questions"].toString().isNotEmpty()) {
                                    view.questionVal_tv.text =
                                        snapshot.data!!["questions"].toString()
                                    if (snapshot.data!!["questions"].toString() != "0") {
                                        getUserQuestionsList()
                                    }
                                }
                                view.userNameTv.text = snapshot.data!!["name"].toString()

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
                                                super.onError(data, throwable)
                                                view.user_iv.load(R.drawable.ic_default_appcolor)
                                            }

                                            override fun onSuccess(
                                                data: Any,
                                                source: coil.decode.DataSource
                                            ) {
                                                super.onSuccess(data, source)
                                                view.user_iv.borderWidth = 2
                                                view.user_iv.borderColor =
                                                    resources.getColor(R.color.colorPrimary)
                                            }
                                        })
                                    })
                                view.progressBar5.visibility = View.GONE
                            } else {
                                hideLoading()
                                view.progressBar5.visibility = View.GONE
                            }
                        },
                        whatIfNot = {
                            hideLoading()
                            view.progressBar5.visibility = View.GONE
                        }
                    )
                }
            }
    }

    private fun getUserBackground(view: View) {
        firestore.collection("users").document(askedBy).get()
            .addOnSuccessListener { result ->
                if (result.exists()) {

                    addfriendfirestore.collection("background_images")
                        .document(result.getString("bg_option")!!).get()
                        .addOnSuccessListener { resultBg ->
                            showLoading()
                            if (result.exists()) {
                                view.background_image.load(resultBg.getString("imageUrl")!!,
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
                                        placeholder(R.drawable.pbg_two)
                                        this.listener(object : Request.Listener {
                                            override fun onError(data: Any, throwable: Throwable) {
                                                super.onError(data, throwable)
                                                view.progressBar5.visibility = View.GONE
                                            }

                                            override fun onSuccess(
                                                data: Any,
                                                source: coil.decode.DataSource
                                            ) {
                                                super.onSuccess(data, source)
                                                view.progressBar5.visibility = View.GONE
                                            }
                                        })
                                    })
                                hideLoading()
                            } else {
                                showMessage(context!!.getString(R.string.not_found_bg), 1)
                                hideLoading()
                            }
                        }.addOnFailureListener { exception ->
                            showMessage(
                                "Something Went Wrong. ${exception.localizedMessage}",
                                1
                            )
                            hideLoading()
                        }.addOnCanceledListener {
                            showMessage(
                                context!!.getString(R.string.loading_image_cancel),
                                4
                            )
                            hideLoading()
                        }

                    hideLoading()
                }
            }.addOnFailureListener {
                showMessage(context!!.getString(R.string.something_went_wring_oops), 1)
                hideLoading()
            }.addOnCanceledListener {
                showMessage(context!!.getString(R.string.getting_details), 4)
                hideLoading()
            }
    }

    private fun getUserQuestionsList() {
        firestore.collection("question")
            .whereEqualTo("askedBy", askedBy)
            .orderBy("askedOn", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, exception ->
                exception.whatIfNotNull {
                    showMessage(
                        "Something Went Wrong. ${exception!!.localizedMessage}", 1
                    )
                }

                allQuestionList.clear()

                try {
                    for (doc in snapshot!!.documentChanges) {
                        showLoading()
                        if (doc.type == DocumentChange.Type.ADDED) {
                            val docId = doc.document.id
                            val quesList: QuestionModel =
                                doc.document.toObject<QuestionModel>(QuestionModel::class.java)
                                    .withId(docId)
                            this.allQuestionList.add(quesList)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                if (allQuestionList.size > 0) {
                    allQues.clear()
                    allQues.addAll(allQuestionList)
                    questionsAdapter?.updateListItems(allQues)
                }
            }
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

    fun setUserId(askedBy: String) {
        this.askedBy = askedBy
    }
}
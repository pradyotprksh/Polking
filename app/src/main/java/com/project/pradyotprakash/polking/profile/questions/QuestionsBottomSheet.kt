package com.project.pradyotprakash.polking.profile.questions

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
import com.project.pradyotprakash.polking.utility.logd
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.question_btm_sheet.view.*
import java.util.*
import javax.inject.Inject

class QuestionsBottomSheet @Inject constructor() : TransparentBottomSheet(), ProfileEditView {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    lateinit var messageBtmSheet: ShowMessage
    private val allQuestionList = ArrayList<QuestionModel>()
    private var questionsAdapter: QuestionsAdapter? = null
    private val allQues = ArrayList<QuestionModel>()

    companion object {
        fun newInstance(): QuestionsBottomSheet =
            QuestionsBottomSheet().apply {

            }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        AndroidInjection.inject(this.activity)
        val view = inflater.inflate(R.layout.question_btm_sheet, container, false)

        activity!!.logd(getString(R.string.profilebottomsheet))

        initView(view)

        return view
    }

    private fun initView(view: View) {
        mAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        allQues.clear()
        questionsAdapter = QuestionsAdapter(allQues, context!!, activity!!)
        view.questions_rv.setHasFixedSize(true)
        view.questions_rv.layoutManager = LinearLayoutManager(context!!, RecyclerView.HORIZONTAL, false)
        view.questions_rv.adapter = questionsAdapter

        view.back_tv.setOnClickListener {
            dismiss()
        }

        if (mAuth.currentUser != null) {
            showLoading()

            firestore.collection("question")
                .orderBy("askedOn", Query.Direction.DESCENDING)
                .addSnapshotListener { snapshot, exception ->
                    if (exception != null) {
                        showMessage(
                            "Something Went Wrong. ${exception.localizedMessage}", 1
                        )
                    }

                    allQuestionList.clear()

                    try {
                        for (doc in snapshot!!.documentChanges) {
                            showLoading()
                            if (doc.type == DocumentChange.Type.ADDED) {

                                val docId = doc.document.id
                                val quesList: QuestionModel =
                                    doc.document.toObject<QuestionModel>(QuestionModel::class.java).withId(docId)
                                if (quesList.askedBy == mAuth.currentUser!!.uid) {
                                    this.allQuestionList.add(quesList)
                                }

                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        showMessage(e.localizedMessage, 1)
                    }

                    if (allQuestionList.size > 0) {
                        allQues.addAll(allQuestionList)
                        questionsAdapter!!.notifyDataSetChanged()
                    }

                    hideLoading()

                }

        } else {
            showMessage(getString(R.string.user_not_found), 1)
        }
    }

    override fun showLoading() {

    }

    override fun hideLoading() {

    }

    override fun stopAct() {

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
}
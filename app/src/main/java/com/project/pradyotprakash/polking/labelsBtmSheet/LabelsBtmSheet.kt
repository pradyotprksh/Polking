package com.project.pradyotprakash.polking.labelsBtmSheet

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.project.pradyotprakash.polking.R
import com.project.pradyotprakash.polking.message.ShowMessage
import com.project.pradyotprakash.polking.profileDetails.ProfileEditView
import com.project.pradyotprakash.polking.utility.LabelModel
import com.project.pradyotprakash.polking.utility.QuestionModel
import com.project.pradyotprakash.polking.utility.TransparentBottomSheet
import com.skydoves.whatif.whatIfNotNull
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.labels_btm_sheet.view.*
import java.util.*
import javax.inject.Inject

class LabelsBtmSheet @Inject constructor() : TransparentBottomSheet(), ProfileEditView {

    private var allQues: ArrayList<QuestionModel> = ArrayList()
    private val allLablesData = ArrayList<LabelModel>()
    private var selectedPos: Int = 0
    private var allLabelList: ArrayList<String> = ArrayList()
    private lateinit var mAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    lateinit var messageBtmSheet: ShowMessage
    private var labelsQuestionAdapter: LabelsQuestionAdapter? = null
    private lateinit var labelsFirestore: FirebaseFirestore

    companion object {
        fun newInstance(): LabelsBtmSheet =
            LabelsBtmSheet().apply {

            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        AndroidInjection.inject(this.activity)
        val view = inflater.inflate(R.layout.labels_btm_sheet, container, false)

        dialog!!.setOnShowListener { dialog ->
            val bottomSheetDialog: BottomSheetDialog = dialog as BottomSheetDialog
            val bottomSheetInternal =
                bottomSheetDialog.findViewById<FrameLayout>(R.id.design_bottom_sheet)
            bottomSheetInternal.whatIfNotNull {
                BottomSheetBehavior.from<View>(bottomSheetInternal).state =
                    BottomSheetBehavior.STATE_EXPANDED
            }
        }

        initView(view)

        return view
    }

    private fun initView(view: View) {
        initVariables()

        getImageLabels(view)

        setAdapter(view)

        setOnClickListners(view)

        setupTabLayout(view)
    }

    private fun setAdapter(view: View) {
        labelsQuestionAdapter = LabelsQuestionAdapter(context!!, activity!!)
        view.question_rv.setHasFixedSize(true)
        view.question_rv.layoutManager = LinearLayoutManager(
            context,
            RecyclerView.VERTICAL, false
        )
        view.question_rv.adapter = labelsQuestionAdapter
    }

    private fun setupTabLayout(view: View) {
        view.labelTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                callFirebaseFirestore(tab.position, view)
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {

            }

            override fun onTabReselected(tab: TabLayout.Tab) {

            }
        })
    }

    fun getImageLabels(view: View) {
        labelsFirestore
            .collection("labels")
            .addSnapshotListener { snapshot, _ ->

                allLabelList.clear()
                view.labelTabLayout.removeAllTabs()

                try {
                    for (doc in snapshot!!) {
                        allLabelList.add(doc.id)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                if (allLabelList.size > 0) {
                    for (label in allLabelList) {
                        view.labelTabLayout.addTab(view.labelTabLayout.newTab().setText(label))
                    }

                    Handler().postDelayed({
                        view.progressBar10.visibility = View.VISIBLE
                        view.labelTabLayout.getTabAt(selectedPos).whatIfNotNull {
                            it.select()
                            view.progressBar10.visibility = View.GONE
                        }
                    }, 2000)
                }

            }
    }

    private fun callFirebaseFirestore(position: Int, view: View) {
        for (ques in allQues) {
            view.progressBar10.visibility = View.VISIBLE
            this.allLablesData.clear()
            firestore
                .collection("labels")
                .document(allLabelList[position])
                .collection(ques.docId)
                .addSnapshotListener { snapshot, exception ->
                    exception.whatIfNotNull {
                        showMessage(
                            "Something Went Wrong. ${exception!!.localizedMessage}", 1
                        )
                    }

                    try {
                        for (doc in snapshot!!) {
                            val docId = doc.id
                            val labelsData: LabelModel =
                                doc.toObject<LabelModel>(LabelModel::class.java).withId(docId)
                            this.allLablesData.add(labelsData)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        showMessage(e.localizedMessage, 1)
                    }

                    if (this.allLablesData.size > 0) {
                        loadQuestions(this.allLablesData, view)
                    }

                    view.progressBar10.visibility = View.GONE

                }
        }
    }

    private fun loadQuestions(
        allLablesData: ArrayList<LabelModel>,
        view: View
    ) {
        if (allLablesData.size > 0) {
            labelsQuestionAdapter?.updateListItems(allLablesData)
        }
    }

    private fun setOnClickListners(view: View) {
        view.back_tv.setOnClickListener {
            dismiss()
        }
    }

    private fun initVariables() {
        mAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        labelsFirestore = FirebaseFirestore.getInstance()
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

    fun addLabelsList(allLabelList: ArrayList<String>, position: Int) {
        this.allLabelList = allLabelList
        this.selectedPos = position
    }

    fun addQuestions(allQues: ArrayList<QuestionModel>) {
        this.allQues = allQues
    }
}
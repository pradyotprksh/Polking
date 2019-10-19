package com.project.pradyotprakash.polking.profile.questionStats

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.project.pradyotprakash.polking.R
import com.project.pradyotprakash.polking.message.ShowMessage
import com.project.pradyotprakash.polking.profileDetails.ProfileEditView
import com.project.pradyotprakash.polking.utility.TransparentBottomSheet
import com.project.pradyotprakash.polking.utility.VotesModel
import com.project.pradyotprakash.polking.utility.logd
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.question_stats_btm_sheet.view.*
import javax.inject.Inject

class QuestionStatistics @Inject constructor() : TransparentBottomSheet(), ProfileEditView {

    private lateinit var askedBy: String
    private var voteType: String = "-1"
    private lateinit var questionId: String
    private lateinit var mAuth: FirebaseAuth
    private lateinit var getQuestionFirestore: FirebaseFirestore
    private lateinit var getQuestionUserFirestore: FirebaseFirestore
    private lateinit var getUserVoteFirestore: FirebaseFirestore
    private lateinit var getSimilarVoteFirestore: FirebaseFirestore
    private val allVoteList = ArrayList<VotesModel>()
    private val voteList = ArrayList<VotesModel>()
    private var votesAdapter: VotesAdapter? = null
    lateinit var messageBtmSheet: ShowMessage
    private val allNoVoteList = ArrayList<VotesModel>()
    private val noVoteList = ArrayList<VotesModel>()
    private var noVotesAdapter: VotesAdapter? = null

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
            if (bottomSheetInternal != null) {
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
        votesAdapter = VotesAdapter(allVoteList, context!!, activity!!)
        view.userYesVotesRv.setHasFixedSize(true)
        view.userYesVotesRv.layoutManager =
            LinearLayoutManager(context!!, RecyclerView.HORIZONTAL, false)
        view.userYesVotesRv.adapter = votesAdapter

        allNoVoteList.clear()
        noVotesAdapter = VotesAdapter(allNoVoteList, context!!, activity!!)
        view.userNoVotesRv.setHasFixedSize(true)
        view.userNoVotesRv.layoutManager =
            LinearLayoutManager(context!!, RecyclerView.HORIZONTAL, false)
        view.userNoVotesRv.adapter = noVotesAdapter
    }

    private fun getVotes(view: View) {
        if (context != null && questionId != "" && mAuth.currentUser != null) {
            if (mAuth.currentUser!!.uid != askedBy) {
                val voteText: String
                if (voteType != "-1") {
                    voteText = if (voteType == "1") {
                        "yesVotes"
                    } else {
                        "noVotes"
                    }

                    getSimilarVoteFirestore.collection(questionId)
                        .document(mAuth.currentUser!!.uid)
                        .collection(voteText)
                        .addSnapshotListener { snapshot, exception ->
                            if (exception != null) {
                                showMessage(
                                    "Something Went Wrong. ${exception.localizedMessage}", 1
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

                            if (context != null) {
                                if (voteList.size > 0) {
                                    view.votesUserTv.visibility = View.VISIBLE
                                    view.votesUserTv.text =
                                        getString(R.string.people_who_think_ike_you)
                                    this.allVoteList.clear()
                                    this.allVoteList.addAll(voteList)
                                    votesAdapter?.notifyDataSetChanged()
                                } else {
                                    view.votesUserTv.visibility = View.GONE
                                }
                            }

                        }
                } else {
                    getYesVoteIfSameUser(view)

                    getNoVoteIfSameUser(view)
                }
            } else {
                getYesVoteIfSameUser(view)

                getNoVoteIfSameUser(view)
            }
        } else {
            stopAct()
        }
    }

    private fun getNoVoteIfSameUser(view: View) {
        if (context != null) {
            this.allNoVoteList.clear()
            noVotesAdapter?.notifyDataSetChanged()
            getSimilarVoteFirestore.collection(questionId)
                .document(mAuth.currentUser!!.uid)
                .collection("noVotes")
                .addSnapshotListener { snapshot, exception ->
                    if (exception != null) {
                        showMessage(
                            "Something Went Wrong. ${exception.localizedMessage}", 1
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

                    if (context != null) {
                        if (noVoteList.size > 0) {
                            view.votesUserTv2.visibility = View.VISIBLE
                            view.votesUserTv2.text = getString(R.string.voted_no_for_your_question)
                            this.allNoVoteList.clear()
                            this.allNoVoteList.addAll(noVoteList)
                            noVotesAdapter?.notifyDataSetChanged()
                        } else {
                            view.votesUserTv2.visibility = View.GONE
                        }
                    }

                }
        }
    }

    private fun getYesVoteIfSameUser(view: View) {
        if (context != null) {
            this.allVoteList.clear()
            votesAdapter?.notifyDataSetChanged()
            getSimilarVoteFirestore.collection(questionId)
                .document(mAuth.currentUser!!.uid)
                .collection("yesVotes")
                .addSnapshotListener { snapshot, exception ->
                    if (exception != null) {
                        showMessage(
                            "Something Went Wrong. ${exception.localizedMessage}", 1
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

                    if (context != null) {
                        if (voteList.size > 0) {
                            view.votesUserTv.visibility = View.VISIBLE
                            view.votesUserTv.text = getString(R.string.voted_yes_for_your_question)
                            this.allVoteList.clear()
                            this.allVoteList.addAll(voteList)
                            votesAdapter?.notifyDataSetChanged()
                        } else {
                            view.votesUserTv.visibility = View.GONE
                        }
                    }

                }
        }
    }

    private fun getUserVote(view: View) {
        if (mAuth.currentUser!!.uid != askedBy) {
            if (context != null && questionId != "" && mAuth.currentUser != null) {
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

                            if (context != null) {
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
            } else {
                stopAct()
            }
        } else {
            view.userVote.text = getString(R.string.your_quesitons)
            view.userVote.setChipBackgroundColorResource(R.color.colorPrimary)
            getVotes(view)
        }
    }

    private fun relaodData(view: View) {
        view.reloadData.visibility = View.VISIBLE
    }

    private fun getQuestionData(view: View) {
        if (context != null && questionId != "") {
            getQuestionFirestore.collection("question").document(questionId)
                .addSnapshotListener { snapshot, exception ->

                    if (exception != null) {
                        showMessage(
                            "Something Went Wrong. ${exception.localizedMessage}", 1
                        )
                    }

                    if (snapshot != null && snapshot.exists()) {
                        try {
                            askedBy = snapshot.data!!["askedBy"].toString()
                            val askedOn = snapshot.data!!["askedOn"].toString()
                            val noVote = snapshot.data!!["noVote"].toString()
                            val question = snapshot.data!!["question"].toString()
                            val yesVote = snapshot.data!!["yesVote"].toString()
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

                            getUserData(askedBy, view)

                            getUserVote(view)
                        } catch (exception: Exception) {
                            showMessage(
                                "Something Went Wrong. ${exception.localizedMessage}", 1
                            )
                        }

                    }

                }
        } else {
            stopAct()
        }
    }

    private fun getUserData(askedBy: String, view: View) {
        getQuestionUserFirestore.collection("users").document(askedBy)
            .addSnapshotListener { snapshot, exception ->

                if (exception != null) {
                    showMessage(
                        "Something Went Wrong. ${exception.localizedMessage}", 1
                    )
                }

                if (snapshot != null && snapshot.exists()) {

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

    private fun setUserData(snapshot: DocumentSnapshot, view: View) {
        Glide.with(context!!).load(snapshot.data!!["imageUrl"].toString())
            .placeholder(R.drawable.ic_default_appcolor)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    exception: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }
            }).into(view.user_iv)
        view.user_iv.borderColor =
            context!!.resources.getColor(R.color.colorPrimary)
        view.user_iv.borderWidth = 2

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
    }

    private fun initVariables() {
        mAuth = FirebaseAuth.getInstance()
        getQuestionFirestore = FirebaseFirestore.getInstance()
        getQuestionUserFirestore = FirebaseFirestore.getInstance()
        getUserVoteFirestore = FirebaseFirestore.getInstance()
        getSimilarVoteFirestore = FirebaseFirestore.getInstance()
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
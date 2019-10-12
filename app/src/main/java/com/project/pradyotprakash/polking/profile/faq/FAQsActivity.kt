package com.project.pradyotprakash.polking.profile.faq

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import com.project.pradyotprakash.polking.R
import com.project.pradyotprakash.polking.message.ShowMessage
import com.project.pradyotprakash.polking.profile.faq.adapter.BlockReportAdapter
import com.project.pradyotprakash.polking.profile.faq.adapter.FriendBestFriendAdapter
import com.project.pradyotprakash.polking.profile.faq.adapter.QuestionResponseAdapter
import com.project.pradyotprakash.polking.profile.faq.adapter.TopQuestionAdapter
import com.project.pradyotprakash.polking.profile.faq.questionDetails.QuestionDetailsBtmSheet
import com.project.pradyotprakash.polking.utility.CustomLayoutManager
import com.project.pradyotprakash.polking.utility.FAQsQuestionModel
import com.project.pradyotprakash.polking.utility.InternetActivity
import com.project.pradyotprakash.polking.utility.logd
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_faqs_actvity.*
import javax.inject.Inject

class FAQsActivity : InternetActivity(), FAQsActivityView {

    @Inject
    lateinit var presenter: FAQsActivityPresenter
    private lateinit var questionDetailsBtmSheet: QuestionDetailsBtmSheet

    private var questionResponseAdapter: QuestionResponseAdapter? = null
    private var friendBestFriendAdapter: FriendBestFriendAdapter? = null
    private var blockReportAdapter: BlockReportAdapter? = null
    private var topQuestionAdapter: TopQuestionAdapter? = null

    private val questionResponseModelList = ArrayList<FAQsQuestionModel>()
    private val friendBestFriendModelList = ArrayList<FAQsQuestionModel>()
    private val blockReportModelList = ArrayList<FAQsQuestionModel>()
    private val topQuestionModelList = ArrayList<FAQsQuestionModel>()

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

        setContentView(R.layout.activity_faqs_actvity)

        logd(getString(R.string.create))
        initialize()
    }

    private fun initialize() {
        presenter.start()

        initVariable()

        initAdapter()

        onClickListners()

        searchListner()

        presenter.getQuestions()

    }

    private fun searchListner() {
        search_et.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                questionResponseAdapter!!.filter.filter(p0)
                friendBestFriendAdapter!!.filter.filter(p0)
                blockReportAdapter!!.filter.filter(p0)
                topQuestionAdapter!!.filter.filter(p0)
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                questionResponseAdapter!!.filter.filter(p0)
                friendBestFriendAdapter!!.filter.filter(p0)
                blockReportAdapter!!.filter.filter(p0)
                topQuestionAdapter!!.filter.filter(p0)
                return false
            }
        })
    }

    private fun onClickListners() {
        back_tv.setOnClickListener {
            stopAct()
        }
    }

    private fun initAdapter() {
        val snapHelper = PagerSnapHelper()

        questionResponseAdapter =
            QuestionResponseAdapter(questionResponseModelList, questionResponseModelList, this, this)
        questionRes_rv.setHasFixedSize(true)
        questionRes_rv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        questionRes_rv.adapter = questionResponseAdapter
        snapHelper.attachToRecyclerView(questionRes_rv)

        friendBestFriendAdapter =
            FriendBestFriendAdapter(friendBestFriendModelList, friendBestFriendModelList, this, this)
        frnBest_rv.setHasFixedSize(true)
        frnBest_rv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        frnBest_rv.adapter = friendBestFriendAdapter
        snapHelper.attachToRecyclerView(frnBest_rv)

        blockReportAdapter = BlockReportAdapter(blockReportModelList, blockReportModelList, this, this)
        blockRpt_rv.setHasFixedSize(true)
        blockRpt_rv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        blockRpt_rv.adapter = blockReportAdapter
        snapHelper.attachToRecyclerView(blockRpt_rv)

        topQuestionAdapter = TopQuestionAdapter(topQuestionModelList, topQuestionModelList, this, this)
        topQues_rv.setHasFixedSize(true)
        topQues_rv.layoutManager = CustomLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        topQues_rv.adapter = topQuestionAdapter
        snapHelper.attachToRecyclerView(topQues_rv)
    }

    private fun initVariable() {
        questionDetailsBtmSheet = QuestionDetailsBtmSheet.newInstance()
    }

    override fun showLoading() {
        progressBar4.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        progressBar4.visibility = View.GONE
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

    override fun loadQuestionResponse(questionResponseModelList: ArrayList<FAQsQuestionModel>) {
        ques_view.visibility = View.VISIBLE
        ques_tv.visibility = View.VISIBLE
        questionRes_rv.visibility = View.VISIBLE

        this.questionResponseModelList.clear()

        if (questionResponseModelList.size > 0) {
            this.questionResponseModelList.addAll(questionResponseModelList)
            questionResponseAdapter!!.notifyDataSetChanged()
        }
    }

    override fun loadFriendBestFriend(friendBestFriendModelList: ArrayList<FAQsQuestionModel>) {
        frnd_view.visibility = View.VISIBLE
        friends_tv.visibility = View.VISIBLE
        frnBest_rv.visibility = View.VISIBLE

        this.friendBestFriendModelList.clear()

        if (friendBestFriendModelList.size > 0) {
            this.friendBestFriendModelList.addAll(friendBestFriendModelList)
            friendBestFriendAdapter!!.notifyDataSetChanged()
        }
    }

    override fun loadBlockReport(blockReportModelList: ArrayList<FAQsQuestionModel>) {
        block_view.visibility = View.VISIBLE
        block_tv.visibility = View.VISIBLE
        blockRpt_rv.visibility = View.VISIBLE

        this.blockReportModelList.clear()

        if (blockReportModelList.size > 0) {
            this.blockReportModelList.addAll(blockReportModelList)
            blockReportAdapter!!.notifyDataSetChanged()
        }
    }

    override fun loadTopQuestion(topQuestionModelList: ArrayList<FAQsQuestionModel>) {
        topQues_tv.visibility = View.VISIBLE
        topQues_rv.visibility = View.VISIBLE

        this.topQuestionModelList.clear()

        if (topQuestionModelList.size > 0) {
            this.topQuestionModelList.addAll(topQuestionModelList)
            topQuestionAdapter!!.notifyDataSetChanged()
        }
    }

    override fun hideTopQuestion() {
        topQues_tv.visibility = View.GONE
        topQues_rv.visibility = View.GONE
    }

    override fun hideBlockReport() {
        block_view.visibility = View.GONE
        block_tv.visibility = View.GONE
        blockRpt_rv.visibility = View.GONE
    }

    override fun hideFriendBestFriend() {
        frnd_view.visibility = View.GONE
        friends_tv.visibility = View.GONE
        frnBest_rv.visibility = View.GONE
    }

    override fun hideQuestionResponse() {
        ques_view.visibility = View.GONE
        ques_tv.visibility = View.GONE
        questionRes_rv.visibility = View.GONE
    }

    fun openQuestionDetails(docId: String) {
        questionDetailsBtmSheet.docId(docId)
        if (!questionDetailsBtmSheet.isAdded) {
            questionDetailsBtmSheet.show(supportFragmentManager, "btmSheet")
        }
    }

}

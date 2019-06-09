package com.project.pradyotprakash.polking.faq

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.PagerSnapHelper
import android.view.View
import android.view.Window
import android.view.WindowManager
import com.project.pradyotprakash.polking.R
import com.project.pradyotprakash.polking.faq.adapter.BlockReportAdapter
import com.project.pradyotprakash.polking.faq.adapter.FriendBestFriendAdapter
import com.project.pradyotprakash.polking.faq.adapter.QuestionResponseAdapter
import com.project.pradyotprakash.polking.faq.adapter.TopQuestionAdapter
import com.project.pradyotprakash.polking.faq.askQuestion.AskFaqQuestionBtmSheet
import com.project.pradyotprakash.polking.faq.questionDetails.QuestionDetailsBtmSheet
import com.project.pradyotprakash.polking.utility.FAQsQuestionModel
import com.project.pradyotprakash.polking.utility.logd
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_faqs_actvity.*
import javax.inject.Inject

class FAQsActivity : AppCompatActivity(), FAQsActivityView {

    @Inject
    lateinit var presenter: FAQsActivityPresenter
    lateinit var askFaqQuestionBtmSheet: AskFaqQuestionBtmSheet
    lateinit var questionDetailsBtmSheet: QuestionDetailsBtmSheet

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
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        setContentView(R.layout.activity_faqs_actvity)

        logd(getString(R.string.create))
        initialize()
    }

    private fun initialize() {
        presenter.start()
        askFaqQuestionBtmSheet = AskFaqQuestionBtmSheet.newInstance()
        questionDetailsBtmSheet = QuestionDetailsBtmSheet.newInstance()

        addQuestion_tv.setOnClickListener {
            if (!askFaqQuestionBtmSheet.isAdded) {
                askFaqQuestionBtmSheet.show(supportFragmentManager, "btmSheet")
            }
        }

        presenter.getQuestions()

        val snapHelper = PagerSnapHelper()

        questionResponseAdapter = QuestionResponseAdapter(questionResponseModelList, this, this)
        questionRes_rv.setHasFixedSize(true)
        questionRes_rv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        questionRes_rv.adapter = questionResponseAdapter
        snapHelper.attachToRecyclerView(questionRes_rv)

        friendBestFriendAdapter = FriendBestFriendAdapter(friendBestFriendModelList, this, this)
        frnBest_rv.setHasFixedSize(true)
        frnBest_rv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        frnBest_rv.adapter = friendBestFriendAdapter
        snapHelper.attachToRecyclerView(frnBest_rv)

        blockReportAdapter = BlockReportAdapter(blockReportModelList, this, this)
        blockRpt_rv.setHasFixedSize(true)
        blockRpt_rv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        blockRpt_rv.adapter = blockReportAdapter
        snapHelper.attachToRecyclerView(blockRpt_rv)

        topQuestionAdapter = TopQuestionAdapter(topQuestionModelList, this, this)
        topQues_rv.setHasFixedSize(true)
        topQues_rv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        topQues_rv.adapter = topQuestionAdapter
        snapHelper.attachToRecyclerView(topQues_rv)
    }

    override fun showLoading() {

    }

    override fun hideLoading() {

    }

    override fun stopAct() {

    }

    override fun showMessage(message: String, type: Int) {

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

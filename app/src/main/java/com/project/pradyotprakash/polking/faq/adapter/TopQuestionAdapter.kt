package com.project.pradyotprakash.polking.faq.adapter

import android.app.Activity
import android.content.Context
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.project.pradyotprakash.polking.R
import com.project.pradyotprakash.polking.faq.FAQsActivity
import com.project.pradyotprakash.polking.utility.FAQsQuestionModel

class TopQuestionAdapter(
    private val topQuestionModelList: List<FAQsQuestionModel>,
    private val context: Context,
    private val activity: Activity
) : RecyclerView.Adapter<TopQuestionAdapter.ViewAdapter>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewAdapter {
        val view = LayoutInflater.from(p0.context).inflate(R.layout.top_question_response_layout, p0, false)
        return ViewAdapter(view)
    }

    override fun getItemCount(): Int {
        return topQuestionModelList.size
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onBindViewHolder(holder: ViewAdapter, pos: Int) {

        when {
            topQuestionModelList[pos].type == "queRes" -> {
                holder.type_tv.text = context.getString(R.string.how_to)
            }
            topQuestionModelList[pos].type == "friendBestFriend" -> {
                holder.type_tv.text = context.getString(R.string.friends_txt)
            }
            topQuestionModelList[pos].type == "blockReport" -> {
                holder.type_tv.text = context.getString(R.string.block_report)
            }
        }

        holder.question_tv.text = topQuestionModelList[pos].question

        holder.itemView.setOnClickListener {
            if (activity is FAQsActivity) {
                activity.openQuestionDetails(topQuestionModelList[pos].docId)
            }
        }
    }

    inner class ViewAdapter(mView: View) : RecyclerView.ViewHolder(mView) {
        val question_tv: TextView = mView.findViewById(R.id.question_tv)
        val type_tv: TextView = mView.findViewById(R.id.type_tv)
    }

}
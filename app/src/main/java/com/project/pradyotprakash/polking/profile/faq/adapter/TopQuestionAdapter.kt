package com.project.pradyotprakash.polking.profile.faq.adapter

import android.app.Activity
import android.content.Context
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import com.project.pradyotprakash.polking.R
import com.project.pradyotprakash.polking.profile.faq.FAQsActivity
import com.project.pradyotprakash.polking.utility.FAQsQuestionModel

class TopQuestionAdapter(
    private val topQuestionModelList: List<FAQsQuestionModel>,
    private var topQuestionModelListFiltered: List<FAQsQuestionModel>,
    private val context: Context,
    private val activity: Activity
) : RecyclerView.Adapter<TopQuestionAdapter.ViewAdapter>(), Filterable {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewAdapter {
        val view = LayoutInflater.from(p0.context).inflate(R.layout.top_question_response_layout, p0, false)
        return ViewAdapter(view)
    }

    override fun getItemCount(): Int {
        return topQuestionModelListFiltered.size
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onBindViewHolder(holder: ViewAdapter, pos: Int) {

        when {
            topQuestionModelListFiltered[pos].type == "queRes" -> {
                holder.typeTv.text = context.getString(R.string.how_to)
            }
            topQuestionModelListFiltered[pos].type == "friendBestFriend" -> {
                holder.typeTv.text = context.getString(R.string.friends_txt)
            }
            topQuestionModelListFiltered[pos].type == "blockReport" -> {
                holder.typeTv.text = context.getString(R.string.block_report)
            }
        }

        holder.questionTv.text = topQuestionModelListFiltered[pos].question

        holder.itemView.setOnClickListener {
            if (activity is FAQsActivity) {
                activity.openQuestionDetails(topQuestionModelListFiltered[pos].docId)
            }
        }
    }

    inner class ViewAdapter(mView: View) : RecyclerView.ViewHolder(mView) {
        val questionTv: TextView = mView.findViewById(R.id.question_tv)
        val typeTv: TextView = mView.findViewById(R.id.type_tv)
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charString = charSequence.toString()
                topQuestionModelListFiltered = if (charString.isEmpty()) {
                    topQuestionModelList
                } else {
                    val filteredList = ArrayList<FAQsQuestionModel>()
                    for (row in topQuestionModelListFiltered) {
                        if (row.question.toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row)
                        }
                    }
                    filteredList
                }

                val filterResults = FilterResults()
                filterResults.values = topQuestionModelListFiltered
                return filterResults
            }

            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
                topQuestionModelListFiltered = filterResults.values as ArrayList<FAQsQuestionModel>
                notifyDataSetChanged()
            }
        }
    }

}
package com.project.pradyotprakash.polking.faq.adapter

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
import com.project.pradyotprakash.polking.faq.FAQsActivity
import com.project.pradyotprakash.polking.utility.FAQsQuestionModel

class BlockReportAdapter(
    private val blockReportModelList: List<FAQsQuestionModel>,
    private var blockReportModelListFiltered: List<FAQsQuestionModel>,
    private val context: Context,
    private val activity: Activity
) : RecyclerView.Adapter<BlockReportAdapter.ViewAdapter>(), Filterable {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewAdapter {
        val view = LayoutInflater.from(p0.context).inflate(R.layout.question_response_layout, p0, false)
        return ViewAdapter(view)
    }

    override fun getItemCount(): Int {
        return blockReportModelListFiltered.size
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onBindViewHolder(holder: ViewAdapter, pos: Int) {
        holder.questionTv.text = blockReportModelListFiltered[pos].question

        holder.itemView.setOnClickListener {
            if (activity is FAQsActivity) {
                activity.openQuestionDetails(blockReportModelListFiltered[pos].docId)
            }
        }
    }

    inner class ViewAdapter(mView: View) : RecyclerView.ViewHolder(mView) {
        val questionTv: TextView = mView.findViewById(R.id.question_tv)
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charString = charSequence.toString()
                blockReportModelListFiltered = if (charString.isEmpty()) {
                    blockReportModelList
                } else {
                    val filteredList = ArrayList<FAQsQuestionModel>()
                    for (row in blockReportModelListFiltered) {
                        if (row.question.toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row)
                        }
                    }
                    filteredList
                }

                val filterResults = FilterResults()
                filterResults.values = blockReportModelListFiltered
                return filterResults
            }

            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
                blockReportModelListFiltered = filterResults.values as ArrayList<FAQsQuestionModel>
                notifyDataSetChanged()
            }
        }
    }

}
package com.project.pradyotprakash.polking.profile.faq.adapter

import android.app.Activity
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.project.pradyotprakash.polking.R
import com.project.pradyotprakash.polking.profile.faq.FAQsActivity
import com.project.pradyotprakash.polking.utility.FAQsQuestionModel

class QuestionResponseAdapter(
    private val questionResponseModelList: ArrayList<FAQsQuestionModel>,
    private var questionResponseModelListFiltered: ArrayList<FAQsQuestionModel>,
    private val context: Context,
    private val activity: Activity
) : RecyclerView.Adapter<QuestionResponseAdapter.ViewAdapter>(), Filterable {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewAdapter {
        val view = LayoutInflater.from(p0.context).inflate(R.layout.question_response_layout, p0, false)
        return ViewAdapter(view)
    }

    override fun getItemCount(): Int {
        return questionResponseModelListFiltered.size
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onBindViewHolder(holder: ViewAdapter, pos: Int) {
        holder.questionTv.text = questionResponseModelListFiltered[pos].question

        holder.itemView.setOnClickListener {
            if (activity is FAQsActivity) {
                activity.openQuestionDetails(questionResponseModelListFiltered[pos].docId)
            }
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charString = charSequence.toString()
                questionResponseModelListFiltered = if (charString.isEmpty()) {
                    questionResponseModelList
                } else {
                    val filteredList = ArrayList<FAQsQuestionModel>()
                    for (row in questionResponseModelList) {
                        if (row.question.toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row)
                        }
                    }
                    filteredList
                }

                val filterResults = FilterResults()
                filterResults.values = questionResponseModelListFiltered
                return filterResults
            }

            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
                questionResponseModelListFiltered = filterResults.values as ArrayList<FAQsQuestionModel>
                notifyDataSetChanged()
            }
        }
    }

    inner class ViewAdapter(mView: View) : RecyclerView.ViewHolder(mView) {
        val questionTv: TextView = mView.findViewById(R.id.question_tv)
    }

}
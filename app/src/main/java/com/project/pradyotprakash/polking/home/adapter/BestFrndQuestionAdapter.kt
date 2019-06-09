package com.project.pradyotprakash.polking.home.adapter

import android.app.Activity
import android.content.Context
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.project.pradyotprakash.polking.R
import com.project.pradyotprakash.polking.utility.QuestionModel

class BestFrndQuestionAdapter(
    private val allBestFrndQues: List<QuestionModel>,
    private val context: Context,
    private val activity: Activity
) : RecyclerView.Adapter<BestFrndQuestionAdapter.ViewAdapter>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewAdapter {
        val view = LayoutInflater.from(p0.context).inflate(R.layout.best_frnd_question_layout, p0, false)
        return ViewAdapter(view)
    }

    override fun getItemCount(): Int {
        return allBestFrndQues.size
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onBindViewHolder(holder: ViewAdapter, pos: Int) {
        holder.profile_iv.clipToOutline = true
    }

    inner class ViewAdapter(mView: View) : RecyclerView.ViewHolder(mView) {
        val profile_iv: ImageView = mView.findViewById(R.id.profile_iv)
    }

}
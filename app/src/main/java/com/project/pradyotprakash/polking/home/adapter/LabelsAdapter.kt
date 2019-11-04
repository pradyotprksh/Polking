package com.project.pradyotprakash.polking.home.adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.project.pradyotprakash.polking.R
import com.project.pradyotprakash.polking.home.MainActivity
import com.project.pradyotprakash.polking.utility.diffUtilCallbacks.LabelCallback
import java.util.*
import kotlin.collections.ArrayList

class LabelsAdapter(
    private val context: Context,
    private val activity: Activity
) : RecyclerView.Adapter<LabelsAdapter.ViewHolder>() {

    private val mLabelList: ArrayList<String> = ArrayList()

    fun updateListItems(labels: ArrayList<String>) {
        val diffCallback = LabelCallback(this.mLabelList, labels)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        this.mLabelList.clear()
        this.mLabelList.addAll(labels)
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val view =
            LayoutInflater.from(p0.context).inflate(R.layout.label_layout, p0, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mLabelList.size
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        p0.label_tv.text = mLabelList[p1].toUpperCase(Locale.ENGLISH)

        p0.label_tv.setOnClickListener {
            if (context is MainActivity) {
                context.openLabelsBtmSheet(mLabelList, p1)
            }
        }
    }

    inner class ViewHolder(mView: View) : RecyclerView.ViewHolder(mView) {
        val label_tv: Chip = mView.findViewById(R.id.label_tv)
    }

}
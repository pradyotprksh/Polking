package com.project.pradyotprakash.polking.home.adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.project.pradyotprakash.polking.R
import com.project.pradyotprakash.polking.utility.AutoUpdatableAdapter
import java.util.*
import kotlin.collections.ArrayList
import kotlin.properties.Delegates

class LabelsAdapter(
    private val context: Context,
    private val activity: Activity
) : RecyclerView.Adapter<LabelsAdapter.ViewHolder>(), AutoUpdatableAdapter {

    var allLabelList: ArrayList<String> by Delegates.observable(
        initialValue = ArrayList()
    ) { _, old, new ->
        autoNotify(old = old, new = new) { o, n -> o == n }
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val view =
            LayoutInflater.from(p0.context).inflate(R.layout.label_layout, p0, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return allLabelList.size
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        p0.label_tv.text = allLabelList[p1].toUpperCase(Locale.ENGLISH)
    }

    inner class ViewHolder(mView: View) : RecyclerView.ViewHolder(mView) {
        val label_tv: Chip = mView.findViewById(R.id.label_tv)
    }

}
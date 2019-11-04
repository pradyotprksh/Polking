package com.project.pradyotprakash.polking.profile.backgroundAdapter

import android.app.Activity
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import coil.Coil
import coil.api.load
import coil.request.Request
import com.project.pradyotprakash.polking.R
import com.project.pradyotprakash.polking.profile.ProfileActivity
import com.project.pradyotprakash.polking.utility.BgModel
import de.hdodenhof.circleimageview.CircleImageView

class BackgroundAdapter(
    private val bgList: ArrayList<BgModel>,
    private val context: Context,
    private val activity: Activity
) : RecyclerView.Adapter<BackgroundAdapter.ViewHolder>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val view = LayoutInflater.from(p0.context).inflate(R.layout.bglist_layout_single, p0, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return bgList.size
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        setImage(p0, p1)

        setOnClickListners(p0, p1)
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private fun setOnClickListners(p0: ViewHolder, p1: Int) {
        p0.itemView.setOnClickListener {
            if (activity is ProfileActivity) {
                if (p0.progressBar.visibility == View.GONE) {
                    activity.setBgImage(bgList[p1].imageUrl, bgList[p1].docId)
                }
            }
        }
    }

    private fun setImage(p0: ViewHolder, p1: Int) {
        p0.bgImage.load(bgList[p1].imageUrl,
            Coil.loader(),
            builder = {
                this.listener(object : Request.Listener {
                    override fun onError(data: Any, throwable: Throwable) {
                        p0.progressBar.visibility = View.GONE
                    }

                    override fun onSuccess(
                        data: Any,
                        source: coil.decode.DataSource
                    ) {
                        super.onSuccess(data, source)
                        p0.progressBar.visibility = View.GONE
                    }
                })
            })
    }

    inner class ViewHolder(mView: View) : RecyclerView.ViewHolder(mView) {
        val bgImage: CircleImageView = mView.findViewById(R.id.user_iv)
        val progressBar: ProgressBar = mView.findViewById(R.id.progressBar)
    }

}


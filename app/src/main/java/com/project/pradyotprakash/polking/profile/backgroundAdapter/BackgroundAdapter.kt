package com.project.pradyotprakash.polking.profile.backgroundAdapter

import android.app.Activity
import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
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

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        Glide.with(context).load(bgList[p1].imageUrl).listener(object : RequestListener<Drawable> {
            override fun onLoadFailed(
                exception: GlideException?,
                model: Any?,
                target: Target<Drawable>?,
                isFirstResource: Boolean
            ): Boolean {
                return false
            }

            override fun onResourceReady(
                resource: Drawable?,
                model: Any?,
                target: Target<Drawable>?,
                dataSource: DataSource?,
                isFirstResource: Boolean
            ): Boolean {
                p0.progressBar.visibility = View.GONE
                return false
            }
        }).into(p0.bgImage)

        p0.itemView.setOnClickListener {
            if (activity is ProfileActivity) {
                activity.setBgImage(bgList[p1].imageUrl, bgList[p1].docId)
            }
        }
    }

    inner class ViewHolder(mView: View) : RecyclerView.ViewHolder(mView) {
        val bgImage: CircleImageView = mView.findViewById(R.id.user_iv)
        val progressBar: ProgressBar = mView.findViewById(R.id.progressBar)
    }

}


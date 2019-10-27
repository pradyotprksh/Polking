package com.project.pradyotprakash.polking.profile.questionStats

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.recyclerview.widget.RecyclerView
import coil.Coil
import coil.api.load
import coil.request.Request
import com.google.android.material.chip.Chip
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.project.pradyotprakash.polking.R
import com.project.pradyotprakash.polking.home.MainActivity
import com.project.pradyotprakash.polking.profile.ProfileActivity
import com.project.pradyotprakash.polking.utility.VotesModel
import com.skydoves.whatif.whatIfNotNull
import de.hdodenhof.circleimageview.CircleImageView

class VotesAdapter(
    private val allVotesModel: ArrayList<VotesModel>,
    private val context: Context,
    private val activity: Activity
) : RecyclerView.Adapter<VotesAdapter.ViewHolder>() {

    private var mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private var userFirestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val view =
            LayoutInflater.from(p0.context).inflate(R.layout.friends_layout_adapter, p0, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return allVotesModel.size
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {

        userFirestore.collection("users").document(allVotesModel[p1].votedBy)
            .addSnapshotListener { snapshot, exception ->

                exception.whatIfNotNull {
                    if (activity is MainActivity) {
                        activity.showMessage(
                            "Something Went Wrong. ${exception!!.localizedMessage}", 1
                        )
                    }
                }

                snapshot.whatIfNotNull {
                    if (snapshot!!.exists()) {
                        p0.nameTv.text = snapshot.data!!["name"].toString()

                        p0.bgImage.load(snapshot.data!!["imageUrl"].toString(),
                            Coil.loader(),
                            builder = {
                                this.listener(object : Request.Listener {
                                    override fun onError(data: Any, throwable: Throwable) {
                                        p0.progressBar.visibility = View.GONE
                                        p0.bgImage.load(R.drawable.ic_default_appcolor)
                                    }

                                    override fun onSuccess(
                                        data: Any,
                                        source: coil.decode.DataSource
                                    ) {
                                        super.onSuccess(data, source)
                                        p0.progressBar.visibility = View.GONE
                                        p0.bgImage.borderColor =
                                            context.resources.getColor(R.color.colorPrimary)
                                        p0.bgImage.borderWidth = 2
                                    }
                                })
                            })
                    }
                }

            }

        p0.nameTv.setOnClickListener {
            openProfileOptions(p0, p1)
        }
        p0.itemView.setOnClickListener {
            openProfileOptions(p0, p1)
        }

    }

    private fun openProfileOptions(p0: ViewHolder, p1: Int) {
        when (context) {
            is MainActivity -> {
                mAuth.currentUser.whatIfNotNull(
                    whatIf = {
                        if (mAuth.currentUser!!.uid != allVotesModel[p1].votedBy) {
                            context.openProfileDetails(allVotesModel[p1].votedBy)
                        } else {
                            context.startProfileAct()
                        }
                    },
                    whatIfNot = {
                        context.startLogin()
                    }
                )
            }
            is ProfileActivity -> {
                mAuth.whatIfNotNull {
                    if (mAuth.currentUser!!.uid != allVotesModel[p1].votedBy) {
                        context.openProfileDetails(allVotesModel[p1].votedBy)
                    }
                }
            }
        }
    }

    inner class ViewHolder(mView: View) : RecyclerView.ViewHolder(mView) {
        val bgImage: CircleImageView = mView.findViewById(R.id.user_iv)
        val nameTv: Chip = mView.findViewById(R.id.name_tv)
        val progressBar: ProgressBar = mView.findViewById(R.id.progressBar)
    }

}
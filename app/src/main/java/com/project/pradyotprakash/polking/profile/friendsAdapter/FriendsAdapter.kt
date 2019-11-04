package com.project.pradyotprakash.polking.profile.friendsAdapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.recyclerview.widget.DiffUtil
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
import com.project.pradyotprakash.polking.utility.FriendsListModel
import com.project.pradyotprakash.polking.utility.diffUtilCallbacks.FriendsListCallback
import com.skydoves.whatif.whatIfNotNull
import de.hdodenhof.circleimageview.CircleImageView

class FriendsAdapter(
    private val context: Context,
    private val activity: Activity
) : RecyclerView.Adapter<FriendsAdapter.ViewHolder>() {

    private var mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private var userFirestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val allFriendsList: ArrayList<FriendsListModel> = ArrayList()

    fun updateListItems(list: ArrayList<FriendsListModel>) {
        val diffCallback = FriendsListCallback(this.allFriendsList, list)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        this.allFriendsList.clear()
        this.allFriendsList.addAll(list)
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val view =
            LayoutInflater.from(p0.context).inflate(R.layout.friends_layout_adapter, p0, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return allFriendsList.size
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {

        setuserData(p0, p1)

        setOnClickListners(p0, p1)

    }

    private fun setOnClickListners(p0: ViewHolder, p1: Int) {
        p0.name_tv.setOnClickListener {
            openUserProfile(p0, p1)
        }

        p0.itemView.setOnClickListener {
            openUserProfile(p0, p1)
        }
    }

    private fun setuserData(p0: ViewHolder, p1: Int) {
        userFirestore.collection("users").document(allFriendsList[p1].userId)
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
                        p0.name_tv.text = snapshot.data!!["name"].toString()

                        p0.bgImage.load(snapshot.data!!["imageUrl"].toString(),
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
                }
            }
    }

    private fun openUserProfile(p0: ViewHolder, p1: Int) {
        when (context) {
            is MainActivity -> {
                mAuth.currentUser.whatIfNotNull(
                    whatIf = {
                        if (mAuth.currentUser!!.uid != allFriendsList[p1].userId) {
                            context.openProfileDetails(allFriendsList[p1].userId)
                        }
                    },
                    whatIfNot = {
                        context.startLogin()
                    }
                )
            }
            is ProfileActivity -> {
                mAuth.currentUser.whatIfNotNull {
                    if (mAuth.currentUser!!.uid != allFriendsList[p1].userId) {
                        context.openProfileDetails(allFriendsList[p1].userId)
                    }
                }
            }
        }
    }

    inner class ViewHolder(mView: View) : RecyclerView.ViewHolder(mView) {
        val bgImage: CircleImageView = mView.findViewById(R.id.user_iv)
        val progressBar: ProgressBar = mView.findViewById(R.id.progressBar)
        val name_tv: Chip = mView.findViewById(R.id.name_tv)
    }

}
package com.project.pradyotprakash.polking.home.adapter

import android.app.Activity
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.Coil
import coil.api.load
import coil.request.Request
import coil.transform.BlurTransformation
import coil.transform.GrayscaleTransformation
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.chip.Chip
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.project.pradyotprakash.polking.R
import com.project.pradyotprakash.polking.home.MainActivity
import com.project.pradyotprakash.polking.profile.ProfileActivity
import com.project.pradyotprakash.polking.utility.QuestionModel
import com.project.pradyotprakash.polking.utility.diffUtilCallbacks.QuestionsCallback
import com.skydoves.whatif.whatIfNotNull
import de.hdodenhof.circleimageview.CircleImageView
import rm.com.longpresspopup.*
import java.util.*

class QuestionsAdapter(
    private val context: Context,
    private val activity: Activity
) : RecyclerView.Adapter<QuestionsAdapter.ViewAdapter>(), PopupInflaterListener, PopupStateListener,
    PopupOnHoverListener {

    private val mQuestionList: ArrayList<QuestionModel> = ArrayList()
    private var mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private var userFirestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var getVotesFirestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var question_image: ImageView? = null

    fun updateListItems(questions: ArrayList<QuestionModel>) {
        val diffCallback = QuestionsCallback(this.mQuestionList, questions)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        this.mQuestionList.clear()
        this.mQuestionList.addAll(questions)
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewAdapter {
        val view = LayoutInflater.from(p0.context).inflate(R.layout.question_layout, p0, false)
        return ViewAdapter(view)
    }

    override fun getItemCount(): Int {
        return mQuestionList.size
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onBindViewHolder(holder: ViewAdapter, pos: Int) {

        getUserData(holder, pos)

        holder.question_tv.text = mQuestionList[pos].question

        mAuth.currentUser.whatIfNotNull(
            whatIf = {
                if (mAuth.currentUser!!.uid == mQuestionList[pos].askedBy) {
                    holder.seeStsts_tv.setChipBackgroundColorResource(R.color.colorPrimaryDark)
                    holder.seeStsts_tv.text = context.getString(R.string.see_stats)
                    showStats(holder, pos)
                } else {
                    checkIfVoteExists(holder, pos)
                }
            },
            whatIfNot = {
                holder.seeStsts_tv.text = context.getString(R.string.please_login)
                showStats(holder, pos)
            }
        )

        if (mQuestionList[pos].imageUrl == "") {
            holder.question_image_Iv.visibility = View.GONE
            holder.question_loading.visibility = View.GONE
        } else {
            holder.question_image_Iv.visibility = View.VISIBLE
            holder.question_image_Iv.load(
                mQuestionList[pos].imageUrl,
                Coil.loader(),
                builder = {
                    mAuth.currentUser.whatIfNotNull(
                        whatIf = {

                        },
                        whatIfNot = {
                            this.transformations(
                                GrayscaleTransformation(),
                                BlurTransformation(context)
                            )
                        })
                    this.listener(object : Request.Listener {
                        override fun onError(data: Any, throwable: Throwable) {
                            super.onError(data, throwable)
                            holder.question_loading.visibility = View.GONE
                        }

                        override fun onStart(data: Any) {
                            super.onStart(data)
                            holder.question_loading.visibility = View.VISIBLE
                        }

                        override fun onSuccess(
                            data: Any,
                            source: coil.decode.DataSource
                        ) {
                            super.onSuccess(data, source)
                            holder.question_loading.visibility = View.GONE
                        }
                    })
                })
        }

        val popUp: LongPressPopup = LongPressPopupBuilder(context)
            .setTarget(holder.question_image_Iv)
            .setPopupView(R.layout.question_image_popup, this)
            .setLongPressDuration(2000)
            .setTag(mQuestionList[pos].imageUrl)
            .setDismissOnLongPressStop(true)
            .setDismissOnTouchOutside(true)
            .setDismissOnBackPressed(true)
            .setCancelTouchOnDragOutsideView(true)
            .setPopupListener(this)
            .setAnimationType(LongPressPopup.ANIMATION_TYPE_FROM_CENTER)
            .build()
        popUp.register()

        holder.profile_iv.setOnClickListener {
            if (context is MainActivity) {
                mAuth.currentUser.whatIfNotNull(
                    whatIf = {
                        if (mAuth.currentUser!!.uid != mQuestionList[pos].askedBy) {
                            context.openProfileDetails(mQuestionList[pos].askedBy)
                        } else {
                            context.startProfileAct()
                        }
                    },
                    whatIfNot = {
                        context.startLogin()
                    }
                )
            }
        }

        holder.username_tv.setOnClickListener {
            if (context is MainActivity) {
                mAuth.currentUser.whatIfNotNull(
                    whatIf = {
                        if (mAuth.currentUser!!.uid != mQuestionList[pos].askedBy) {
                            context.openProfileDetails(mQuestionList[pos].askedBy)
                        } else {
                            context.startProfileAct()
                        }
                    },
                    whatIfNot = {
                        context.startLogin()
                    }
                )
            }
        }

        holder.yes_tv.setOnClickListener {
            if (context is MainActivity) {
                mAuth.currentUser.whatIfNotNull(
                    whatIf = {
                        if (mAuth.currentUser!!.uid != mQuestionList[pos].askedBy) {
                            context.setVotes(1, mQuestionList[pos].docId)
                            holder.seeStsts_tv.setChipBackgroundColorResource(R.color.agree_color)
                            showStats(holder, pos)
                        }
                    },
                    whatIfNot = {
                        context.startLogin()
                    }
                )
            } else if (context is ProfileActivity) {
                mAuth.currentUser.whatIfNotNull(
                    whatIf = {
                        if (mAuth.currentUser!!.uid != mQuestionList[pos].askedBy) {
                            holder.seeStsts_tv.setChipBackgroundColorResource(R.color.disagree_color)
                            context.setVotes(1, mQuestionList[pos].docId)
                            showStats(holder, pos)
                        }
                    }
                )
            }
        }

        holder.no_tv.setOnClickListener {
            if (context is MainActivity) {
                mAuth.currentUser.whatIfNotNull(
                    whatIf = {
                        if (mAuth.currentUser!!.uid != mQuestionList[pos].askedBy) {
                            holder.seeStsts_tv.setChipBackgroundColorResource(R.color.disagree_color)
                            context.setVotes(2, mQuestionList[pos].docId)
                            showStats(holder, pos)
                        }
                    },
                    whatIfNot = {
                        context.startLogin()
                    }
                )
            } else if (context is ProfileActivity) {
                mAuth.currentUser.whatIfNotNull(
                    whatIf = {
                        if (mAuth.currentUser!!.uid != mQuestionList[pos].askedBy) {
                            holder.seeStsts_tv.setChipBackgroundColorResource(R.color.disagree_color)
                            context.setVotes(1, mQuestionList[pos].docId)
                            showStats(holder, pos)
                        }
                    }
                )
            }
        }

        holder.seeStsts_tv.setOnClickListener {
            if (context is MainActivity) {
                mAuth.currentUser.whatIfNotNull(
                    whatIf = {
                        context.showStats(mQuestionList[pos].docId)
                    },
                    whatIfNot = {
                        context.startLogin()
                    }
                )
            } else if (context is ProfileActivity) {
                mAuth.currentUser.whatIfNotNull(
                    whatIf = {
                        context.showStats(mQuestionList[pos].docId)
                    }
                )
            }
        }

    }

    override fun onViewInflated(popupTag: String?, root: View?) {
        question_image = root?.findViewById(R.id.question_image)
    }

    override fun onPopupDismiss(popupTag: String?) {

    }

    override fun onPopupShow(popupTag: String?) {
        question_image.whatIfNotNull {
            popupTag.whatIfNotNull {
                context.whatIfNotNull {
                    question_image!!.load(popupTag,
                        Coil.loader(),
                        builder = {
                            mAuth.currentUser.whatIfNotNull(
                                whatIf = {

                                },
                                whatIfNot = {
                                    this.transformations(
                                        GrayscaleTransformation(),
                                        BlurTransformation(context)
                                    )
                                })
                        })
                }
            }
        }
    }

    override fun onHoverChanged(view: View?, isHovered: Boolean) {

    }

    private fun checkIfVoteExists(holder: ViewAdapter, pos: Int) {
        getVotesFirestore
            .collection("users")
            .document(mAuth.currentUser!!.uid)
            .collection("votes")
            .document(mQuestionList[pos].docId)
            .get()
            .addOnCanceledListener {
                if (activity is MainActivity) {
                    activity.showMessage(
                        "Something Went Wrong. The request was cancelled.", 1
                    )
                }
            }
            .addOnFailureListener { exception ->
                if (activity is MainActivity) {
                    activity.showMessage(
                        "Something Went Wrong. ${exception.localizedMessage}", 1
                    )
                }
            }
            .addOnSuccessListener { result ->
                if (result.exists()) {
                    val voteType = result.get("voteType")
                    if (voteType == 1L) {
                        holder.seeStsts_tv.text = context.getString(R.string.see_stats)
                        holder.seeStsts_tv.setChipBackgroundColorResource(R.color.agree_color)
                    } else {
                        holder.seeStsts_tv.text = context.getString(R.string.see_stats)
                        holder.seeStsts_tv.setChipBackgroundColorResource(R.color.disagree_color)
                    }
                    showStats(holder, pos)
                } else {
                    hideStats(holder, pos)
                }
            }
    }

    private fun hideStats(holder: ViewAdapter, pos: Int) {
        holder.seeStsts_tv.visibility = View.GONE
        holder.yes_tv.visibility = View.VISIBLE
        holder.no_tv.visibility = View.VISIBLE
    }

    private fun showStats(holder: ViewAdapter, pos: Int) {
        holder.seeStsts_tv.visibility = View.VISIBLE
        holder.yes_tv.visibility = View.GONE
        holder.no_tv.visibility = View.GONE
    }

    private fun getUserData(holder: ViewAdapter, pos: Int) {
        userFirestore.collection("users").document(mQuestionList[pos].askedBy)
            .addSnapshotListener { snapshot, exception ->

                exception.whatIfNotNull {
                    if (activity is MainActivity) {
                        activity.showMessage(
                            "Something Went Wrong. ${exception!!.localizedMessage}", 1
                        )
                    } else if (activity is ProfileActivity) {
                        activity.showMessage(
                            "Something Went Wrong. ${exception!!.localizedMessage}", 1
                        )
                    }
                }

                snapshot.whatIfNotNull {
                    if (snapshot!!.exists()) {
                        try {
                            holder.profile_iv.load(snapshot.data!!["imageUrl"].toString(),
                                Coil.loader(),
                                builder = {
                                    mAuth.currentUser.whatIfNotNull(
                                        whatIf = {

                                        },
                                        whatIfNot = {
                                            this.transformations(
                                                GrayscaleTransformation(),
                                                BlurTransformation(context)
                                            )
                                        })
                                    this.listener(object : Request.Listener {
                                        override fun onError(data: Any, throwable: Throwable) {
                                            holder.profile_iv.load(R.drawable.ic_default_appcolor)
                                        }

                                        override fun onSuccess(
                                            data: Any,
                                            source: coil.decode.DataSource
                                        ) {
                                            super.onSuccess(data, source)
                                            holder.profile_iv.borderColor =
                                                context.resources.getColor(R.color.colorPrimary)
                                            holder.profile_iv.borderWidth = 2
                                        }
                                    })
                                })

                            holder.username_tv.text = snapshot.data!!["name"].toString()
                        } catch (exception: Exception) {
                            if (activity is MainActivity) {
                                activity.showMessage(
                                    "Something Went Wrong. ${exception.localizedMessage}", 1
                                )
                            }
                        }
                    }
                }
            }
    }

    inner class ViewAdapter(context: View) : RecyclerView.ViewHolder(context) {
        val profile_iv: CircleImageView = context.findViewById(R.id.user_iv)
        val username_tv: Chip = context.findViewById(R.id.username_tv)
        val question_tv: TextView = context.findViewById(R.id.question_tv)
        val yes_tv: Chip = context.findViewById(R.id.yes_tv)
        val no_tv: Chip = context.findViewById(R.id.no_tv)
        val seeStsts_tv: Chip = context.findViewById(R.id.seeStsts_tv)
        val question_loading: LottieAnimationView = context.findViewById(R.id.question_loading)
        val question_image_Iv: ImageView = context.findViewById(R.id.question_image_Iv)
    }

}
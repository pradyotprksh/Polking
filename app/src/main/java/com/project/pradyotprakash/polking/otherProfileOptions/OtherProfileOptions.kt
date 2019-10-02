package com.project.pradyotprakash.polking.otherProfileOptions

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.project.pradyotprakash.polking.R
import com.project.pradyotprakash.polking.home.adapter.QuestionsAdapter
import com.project.pradyotprakash.polking.profile.friendsAdapter.FriendsAdapter
import com.project.pradyotprakash.polking.profileDetails.ProfileEditView
import com.project.pradyotprakash.polking.utility.*
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.other_profile_options_btm_sheet.*
import kotlinx.android.synthetic.main.other_profile_options_btm_sheet.view.*
import java.text.SimpleDateFormat
import javax.inject.Inject

class OtherProfileOptions @Inject constructor() : TransparentBottomSheet(), ProfileEditView {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var addfriendfirestore: FirebaseFirestore
    private lateinit var addBestfriendfirestore: FirebaseFirestore
    private lateinit var deletefriendfirestore: FirebaseFirestore
    private lateinit var getfriendfirestore: FirebaseFirestore
    private lateinit var getbestfriendfirestore: FirebaseFirestore
    private lateinit var askedBy: String
    private lateinit var mAuth: FirebaseAuth
    private val allQuestionList = ArrayList<QuestionModel>()
    private var questionsAdapter: QuestionsAdapter? = null
    private val allQues = ArrayList<QuestionModel>()
    private val allFriends = ArrayList<FriendsListModel>()
    private val allBestFriends = ArrayList<FriendsListModel>()
    private val allFriendsList = ArrayList<FriendsListModel>()
    private val allBestFriendsList = ArrayList<FriendsListModel>()
    private var friendsAdapter: FriendsAdapter? = null
    private var bestfriendsAdapter: FriendsAdapter? = null
    @SuppressLint("SimpleDateFormat")
    var dateTimeFormat: SimpleDateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")

    companion object {
        fun newInstance(): OtherProfileOptions =
            OtherProfileOptions().apply {

            }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        AndroidInjection.inject(this.activity)
        val view = inflater.inflate(R.layout.other_profile_options_btm_sheet, container, false)

        activity!!.logd(getString(R.string.otherprofiledetailsbottomsheet))

        initView(view)

        return view
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun initView(view: View) {
        mAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        addfriendfirestore = FirebaseFirestore.getInstance()
        addBestfriendfirestore = FirebaseFirestore.getInstance()
        getfriendfirestore = FirebaseFirestore.getInstance()
        getbestfriendfirestore = FirebaseFirestore.getInstance()
        deletefriendfirestore = FirebaseFirestore.getInstance()

        allQues.clear()
        questionsAdapter = QuestionsAdapter(allQues, context!!, activity!!)
        view.questions_rv.setHasFixedSize(true)
        view.questions_rv.layoutManager =
            LinearLayoutManager(context!!, RecyclerView.HORIZONTAL, false)
        view.questions_rv.adapter = questionsAdapter

        allFriendsList.clear()
        friendsAdapter = FriendsAdapter(allFriendsList, context!!, activity!!)
        view.friends_rv.setHasFixedSize(true)
        view.friends_rv.layoutManager =
            LinearLayoutManager(context!!, RecyclerView.HORIZONTAL, false)
        view.friends_rv.adapter = friendsAdapter

        allBestFriendsList.clear()
        bestfriendsAdapter = FriendsAdapter(allBestFriendsList, context!!, activity!!)
        view.bestFriends_rv.setHasFixedSize(true)
        view.bestFriends_rv.layoutManager =
            LinearLayoutManager(context!!, RecyclerView.HORIZONTAL, false)
        view.bestFriends_rv.adapter = bestfriendsAdapter

        getUserData(view)

        view.back_tv.setOnCloseIconClickListener {
            dismiss()
        }
        view.back_tv.setOnClickListener {
            dismiss()
        }

        view.questionVal_tv.setOnClickListener {
            view.friends_rv.visibility = View.GONE
            if (view.questions_rv.visibility == View.VISIBLE) {
                view.questions_rv.visibility = View.GONE
            } else {
                if (allQues.size > 0) {
                    view.questions_rv.visibility = View.VISIBLE
                    view.questions_rv.startAnimation(Utility().inFromRightAnimation())
                } else {
                    view.questions_rv.visibility = View.GONE
                }
            }
        }

        view.question_tv.setOnClickListener {
            view.friends_rv.visibility = View.GONE
            if (view.questions_rv.visibility == View.VISIBLE) {
                view.questions_rv.visibility = View.GONE
            } else {
                if (allQues.size > 0) {
                    view.questions_rv.visibility = View.VISIBLE
                    view.questions_rv.startAnimation(Utility().inFromRightAnimation())
                } else {
                    view.questions_rv.visibility = View.GONE
                }
            }
        }

        view.friendsVal_tv.setOnClickListener {
            view.questions_rv.visibility = View.GONE
            if (view.friendsVal_tv.text == "0") {
                view.friends_rv.visibility = View.GONE
            } else {
                if (view.friends_rv.visibility == View.VISIBLE) {
                    view.friends_rv.visibility = View.GONE
                } else {
                    view.friends_rv.visibility = View.VISIBLE
                    view.friends_rv.startAnimation(Utility().inFromRightAnimation())
                }
            }
        }

        view.friends_tv.setOnClickListener {
            view.questions_rv.visibility = View.GONE
            if (view.friendsVal_tv.text == "0") {
                view.friends_rv.visibility = View.GONE
            } else {
                if (view.friends_rv.visibility == View.VISIBLE) {
                    view.friends_rv.visibility = View.GONE
                } else {
                    view.friends_rv.visibility = View.VISIBLE
                    view.friends_rv.startAnimation(Utility().inFromRightAnimation())
                }
            }
        }

        view.bestFrndVal_tv.setOnClickListener {
            view.questions_rv.visibility = View.GONE
            if (view.bestFrnd_tv.text == "0") {
                view.bestFriends_rv.visibility = View.GONE
            } else {
                if (view.bestFriends_rv.visibility == View.VISIBLE) {
                    view.bestFriends_rv.visibility = View.GONE
                } else {
                    view.bestFriends_rv.visibility = View.VISIBLE
                    view.bestFriends_rv.startAnimation(Utility().inFromRightAnimation())
                }
            }
        }

        view.bestFrnd_tv.setOnClickListener {
            view.questions_rv.visibility = View.GONE
            if (view.bestFrnd_tv.text == "0") {

            }
        }

        view.makeBfChip.setOnCloseIconClickListener {
            if (mAuth.currentUser != null) {
                if (view.connectTv.text == getString(R.string.unfollow_as_a_friend)) {
                    view.progressBar5.visibility = View.VISIBLE
                    addBestfriendfirestore.collection("users").document(mAuth.currentUser!!.uid)
                        .collection("bestfriends")
                        .document(askedBy)
                        .delete()
                        .addOnSuccessListener {
                            view.progressBar5.visibility = View.GONE
                        }.addOnCompleteListener {

                            val otherFriendData = HashMap<String, Any>()
                            otherFriendData["userId"] = mAuth.currentUser!!.uid

                            firestore.collection("users").document(askedBy)
                                .collection("bestfriends")
                                .document(mAuth.currentUser!!.uid)
                                .delete()
                                .addOnSuccessListener {
                                    view.progressBar5.visibility = View.GONE
                                }.addOnCompleteListener {
                                    if (isAdded && !isDetached) {
                                        view.makeBfChip.isCloseIconVisible = false
                                        view.makeBfChip.text = getString(R.string.add_as_bestfriend)
                                        view.makeBfChip.setChipBackgroundColorResource(R.color.colorAccent)
                                        view.makeBfChip.setRippleColorResource(R.color.agree_color)
                                    }
                                }.addOnFailureListener { exception ->
                                    showMessage(
                                        "Something Went Wrong. ${exception.localizedMessage}",
                                        1
                                    )
                                    view.progressBar5.visibility = View.GONE
                                }
                        }.addOnFailureListener { exception ->
                            showMessage(
                                "Something Went Wrong. ${exception.localizedMessage}",
                                1
                            )
                            view.progressBar5.visibility = View.GONE
                        }
                } else {
                    showMessage("First Friend Then Best Friend", 2)
                }
            }
        }

        view.makeBfChip.setOnClickListener {
            if (mAuth.currentUser != null) {
                if (view.connectTv.text == getString(R.string.unfollow_as_a_friend)) {
                    view.progressBar5.visibility = View.VISIBLE
                    val friendData = HashMap<String, Any>()
                    friendData["userId"] = askedBy
                    addBestfriendfirestore.collection("users").document(mAuth.currentUser!!.uid)
                        .collection("bestfriends")
                        .document(askedBy).set(friendData)
                        .addOnSuccessListener {
                            view.progressBar5.visibility = View.GONE
                        }.addOnCompleteListener {

                            val otherFriendData = HashMap<String, Any>()
                            otherFriendData["userId"] = mAuth.currentUser!!.uid

                            firestore.collection("users").document(askedBy)
                                .collection("bestfriends").document(mAuth.currentUser!!.uid)
                                .set(otherFriendData).addOnSuccessListener {
                                    view.progressBar5.visibility = View.GONE
                                }.addOnCompleteListener {
                                    if (isAdded && !isDetached) {
                                        view.makeBfChip.isCloseIconVisible = true
                                        view.makeBfChip.text = getString(R.string.best_friends)
                                        view.makeBfChip.setChipBackgroundColorResource(R.color.agree_color)
                                        view.makeBfChip.setRippleColorResource(R.color.colorAccent)
                                    }
                                }.addOnFailureListener { exception ->
                                    showMessage(
                                        "Something Went Wrong. ${exception.localizedMessage}",
                                        1
                                    )
                                    view.progressBar5.visibility = View.GONE
                                }
                        }.addOnFailureListener { exception ->
                            showMessage(
                                "Something Went Wrong. ${exception.localizedMessage}",
                                1
                            )
                            view.progressBar5.visibility = View.GONE
                        }
                } else {
                    showMessage(getString(R.string.first_friend_then_best_friend), 2)
                }
            }
        }

        view.connectTv.setOnClickListener {
            if (mAuth.currentUser != null) {
                view.progressBar5.visibility = View.VISIBLE

                if (view.connectTv.text == getString(R.string.unfollow_as_a_friend)) {

                    if (!view.makeBfChip.isCloseIconVisible) {
                        deletefriendfirestore.collection("users").document(mAuth.currentUser!!.uid)
                            .collection("friends").document(askedBy)
                            .delete()
                            .addOnFailureListener { exception ->
                                showMessage(
                                    "Something Went Wrong. ${exception.localizedMessage}",
                                    1
                                )
                                view.progressBar5.visibility = View.GONE
                            }.addOnSuccessListener {
                                view.progressBar5.visibility = View.GONE
                            }.addOnCompleteListener {

                                firestore.collection("users").document(askedBy)
                                    .collection("friends").document(mAuth.currentUser!!.uid)
                                    .delete()
                                    .addOnFailureListener { exception ->
                                        showMessage(
                                            "Something Went Wrong. ${exception.localizedMessage}",
                                            1
                                        )
                                        view.progressBar5.visibility = View.GONE
                                    }.addOnSuccessListener {
                                        view.progressBar5.visibility = View.GONE
                                    }.addOnCompleteListener {
                                        if (isAdded && !isDetached) {
                                            view.connectTv.text =
                                                getString(R.string.follow_as_a_friend)
                                            view.connectTv.setChipBackgroundColorResource(R.color.agree_color)
                                            view.connectTv.isCloseIconVisible = false
                                            view.makeBfChip.visibility = View.GONE
                                        }
                                    }

                            }
                    } else {
                        showMessage(
                            "A best friend is also a friend. Can't remove make someone best friend and not friend. Get It?",
                            2
                        )
                    }

                } else {
                    val friendData = HashMap<String, Any>()
                    friendData["userId"] = askedBy

                    addfriendfirestore.collection("users").document(mAuth.currentUser!!.uid)
                        .collection("friends").document(askedBy).set(friendData)
                        .addOnSuccessListener {
                            view.progressBar5.visibility = View.GONE
                        }.addOnCompleteListener {

                            val otherFriendData = HashMap<String, Any>()
                            otherFriendData["userId"] = mAuth.currentUser!!.uid

                            firestore.collection("users").document(askedBy)
                                .collection("friends").document(mAuth.currentUser!!.uid)
                                .set(otherFriendData).addOnSuccessListener {
                                    view.progressBar5.visibility = View.GONE
                                }.addOnCompleteListener {
                                    if (isAdded && !isDetached) {
                                        view.connectTv.text =
                                            getString(R.string.unfollow_as_a_friend)
                                        view.connectTv.setChipBackgroundColorResource(R.color.disagree_color)
                                        view.connectTv.isCloseIconVisible = false
                                        view.makeBfChip.visibility = View.VISIBLE
                                    }
                                }.addOnFailureListener { exception ->
                                    showMessage(
                                        "Something Went Wrong. ${exception.localizedMessage}",
                                        1
                                    )
                                    view.progressBar5.visibility = View.GONE
                                }
                        }.addOnFailureListener { exception ->
                            showMessage(
                                "Something Went Wrong. ${exception.localizedMessage}",
                                1
                            )
                            view.progressBar5.visibility = View.GONE
                        }

                }
            }
        }

    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun getUserData(view: View) {
        view.progressBar5.visibility = View.VISIBLE
        if (askedBy.isEmpty()) {
            view.progressBar5.visibility = View.GONE
            dismiss()
        } else {
            // get user friend list
            getUserFriendList(view)

            // get best friend list
            getUserBestFriendList(view)

            // get selected user questions
            getUserQuestionsList(view)

            // get user data
            if (isAdded) {
                firestore.collection("users").document(askedBy)
                    .addSnapshotListener { snapshot, exception ->
                        if (exception != null) {
                            showMessage(
                                "Something Went Wrong. ${exception.localizedMessage}", 1
                            )
                        }

                        if (isAdded && !isDetached) {
                            if (snapshot != null && snapshot.exists()) {
                                if (snapshot.data!!["questions"].toString().isNotEmpty()) {
                                    view.questionVal_tv.text =
                                        snapshot.data!!["questions"].toString()
                                    if (snapshot.data!!["questions"].toString() == "0") {
                                        view.friends_rv.visibility = View.GONE
                                    } else {
                                        getUserQuestionsList(view)
                                    }
                                }
                                if (snapshot.data!!["friends"].toString().isNotEmpty()) {
                                    view.friendsVal_tv.text = snapshot.data!!["friends"].toString()
                                    if (snapshot.data!!["friends"].toString() == "0") {
                                        view.friends_rv.visibility = View.GONE
                                    } else {
                                        getUserFriendList(view)
                                    }
                                }
                                if (snapshot.data!!["best_friends"].toString().isNotEmpty()) {
                                    view.bestFrndVal_tv.text =
                                        snapshot.data!!["best_friends"].toString()
                                    if (snapshot.data!!["best_friends"].toString() == "0") {
                                        view.bestFriends_rv.visibility = View.GONE
                                    } else {
                                        getUserBestFriendList(view)
                                    }
                                }
                                view.userNameTv.text = snapshot.data!!["name"].toString()

                                Glide.with(this).load(snapshot.data!!["imageUrl"].toString())
                                    .listener(object :
                                        RequestListener<Drawable> {
                                        override fun onLoadFailed(
                                            exception: GlideException?,
                                            model: Any?,
                                            target: Target<Drawable>?,
                                            isFirstResource: Boolean
                                        ): Boolean {
                                            view.progressBar5.visibility = View.GONE
                                            showMessage(
                                                "Something Went Wrong. ${exception?.localizedMessage}",
                                                1
                                            )
                                            return false
                                        }

                                        override fun onResourceReady(
                                            resource: Drawable?,
                                            model: Any?,
                                            target: Target<Drawable>?,
                                            dataSource: DataSource?,
                                            isFirstResource: Boolean
                                        ): Boolean {
                                            view.progressBar5.visibility = View.GONE
                                            return false
                                        }
                                    }).into(user_iv)

                                view.progressBar5.visibility = View.GONE
                            } else {
                                hideLoading()
                                view.progressBar5.visibility = View.GONE
                            }
                        }
                    }
            }
        }
    }

    private fun getUserBestFriendList(view: View) {
        if (mAuth.currentUser != null) {
            getbestfriendfirestore.collection("users").document(askedBy)
                .collection("bestfriends").addSnapshotListener { snapshot, exception ->
                    if (exception != null) {
                        showMessage(
                            "Something Went Wrong. ${exception.localizedMessage}", 1
                        )
                    }

                    allBestFriends.clear()

                    try {
                        for (doc in snapshot!!.documentChanges) {
                            if (doc.type == DocumentChange.Type.ADDED || doc.type == DocumentChange.Type.REMOVED) {
                                val docId = doc.document.id
                                val friendList: FriendsListModel =
                                    doc.document.toObject(FriendsListModel::class.java)
                                        .withId(docId)
                                if (friendList.userId == mAuth.currentUser!!.uid) {
                                    view.makeBfChip.visibility = View.VISIBLE
                                    view.makeBfChip.isCloseIconVisible = true
                                    view.makeBfChip.text = getString(R.string.best_friends)
                                    view.makeBfChip.setChipBackgroundColorResource(R.color.agree_color)
                                    view.makeBfChip.setRippleColorResource(R.color.colorAccent)
                                } else {
                                    view.makeBfChip.visibility = View.VISIBLE
                                    view.makeBfChip.isCloseIconVisible = false
                                    view.makeBfChip.text = getString(R.string.add_as_bestfriend)
                                    view.makeBfChip.setChipBackgroundColorResource(R.color.colorAccent)
                                    view.makeBfChip.setRippleColorResource(R.color.agree_color)
                                }

                                allBestFriends.add(friendList)
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        showMessage(e.localizedMessage, 1)
                    }

                    if (allBestFriends.size > 0) {
                        allBestFriendsList.clear()
                        allBestFriendsList.addAll(allBestFriends)
                        bestfriendsAdapter!!.notifyDataSetChanged()
                    }
                }
        }
    }

    private fun getUserQuestionsList(view: View) {
        firestore.collection("question")
            .orderBy("askedOn", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    showMessage(
                        "Something Went Wrong. ${exception.localizedMessage}", 1
                    )
                }

                allQuestionList.clear()

                try {
                    for (doc in snapshot!!.documentChanges) {
                        showLoading()
                        if (doc.type == DocumentChange.Type.ADDED) {

                            val docId = doc.document.id
                            val quesList: QuestionModel =
                                doc.document.toObject<QuestionModel>(QuestionModel::class.java)
                                    .withId(docId)
                            if (quesList.askedBy == askedBy) {
                                this.allQuestionList.add(quesList)
                            }

                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    showMessage(e.localizedMessage, 1)
                }

                if (allQuestionList.size > 0) {
                    allQues.clear()
                    allQues.addAll(allQuestionList)
                    questionsAdapter!!.notifyDataSetChanged()
                }
            }
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private fun getUserFriendList(view: View) {
        if (mAuth.currentUser != null) {
            getfriendfirestore.collection("users").document(askedBy)
                .collection("friends").addSnapshotListener { snapshot, exception ->
                    if (exception != null) {
                        showMessage(
                            "Something Went Wrong. ${exception.localizedMessage}", 1
                        )
                    }

                    allFriends.clear()

                    try {
                        for (doc in snapshot!!.documentChanges) {
                            if (doc.type == DocumentChange.Type.ADDED || doc.type == DocumentChange.Type.REMOVED) {
                                val docId = doc.document.id
                                val friendList: FriendsListModel =
                                    doc.document.toObject(FriendsListModel::class.java)
                                        .withId(docId)
                                if (friendList.userId == mAuth.currentUser!!.uid) {
                                    view.connectTv.text = getString(R.string.unfollow_as_a_friend)
                                    view.connectTv.setChipBackgroundColorResource(R.color.disagree_color)
                                    view.connectTv.isCloseIconVisible = true
                                    view.makeBfChip.visibility = View.VISIBLE
                                } else {
                                    view.connectTv.text = getString(R.string.follow_as_a_friend)
                                    view.connectTv.setChipBackgroundColorResource(R.color.agree_color)
                                    view.connectTv.isCloseIconVisible = false
                                    view.makeBfChip.visibility = View.GONE
                                }

                                allFriends.add(friendList)
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        showMessage(e.localizedMessage, 1)
                    }

                    if (allFriends.size > 0) {
                        allFriendsList.clear()
                        allFriendsList.addAll(allFriends)
                        friendsAdapter!!.notifyDataSetChanged()
                    }
                }
        }
    }

    override fun showLoading() {

    }

    override fun hideLoading() {

    }

    override fun stopAct() {

    }

    override fun showMessage(message: String, type: Int) {

    }

    fun setUserId(askedBy: String) {
        this.askedBy = askedBy
    }
}
package com.project.pradyotprakash.polking.utility

// Profile Page Background List
data class BgModel(val imageUrl: String = "") : GetId()

// Questions
data class QuestionModel(
    val question: String = "",
    val askedBy: String = "",
    val askedOnDate: String = "",
    val askedOnTime: String = "",
    val yesVote: String = "",
    val noVote: String = ""
) : GetId()

// FAQs Questions
data class FAQsQuestionModel(
    val question: String = "",
    val askedBy: String = "",
    val askedOn: String = "",
    val openedBy: String = "",
    val helpFullYes: String = "",
    val helpFullNo: String = "",
    val type: String = "",
    val isTopQuestion: String = "",
    val answer: String = ""
) : GetId()

// Votes
data class UserVotesModel(
    val voted: String = "",
    val votedFor: String = "",
    val votedOnDate: String = "",
    val votedOnTime: String = ""
) : GetId()

// Question Votes List
data class QuestionVotesModel(
    val voted: String = "",
    val votedFor: String = "",
    val votedOnDate: String = "",
    val votedOnTime: String = "",
    val votedBy: String = ""
) : GetId()

// Friend List
data class FriendsListModel(
    val userId: String = "",
    val madeFriendOn: String = "",
    val madeBestFriendOn: String = "",
    val isBestFriend: String = "",
    val isFriend: String = ""
) : GetId()

// Notification List
data class NotificationModel(
    val notificationMessageBy: String = "",
    val notificationMessage: String = "",
    val notificationOn: String = "",
    val notificationIsRead: String = ""
) : GetId()

// Votes
data class VotesModel(
    val votedBy: String = ""
) : GetId()
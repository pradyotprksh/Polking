package com.project.pradyotprakash.polking.utility

// Profile Page Background List
data class BgModel(
    val imageUrl: String = ""
) : GetId()

// Questions
data class QuestionModel(
    val question: String = "",
    val askedBy: String = "",
    val askedOnDate: String = "",
    val askedOnTime: String = "",
    val yesVote: String = "",
    val noVote: String = "",
    val imageUrl: String = "",
    val imageName: String = ""
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
    val notificationIsRead: String = "",
    val notificationForQuestionVote: String = "",
    val notificationQuestionId: String = "",
    val voteType: String = "",
    val notificationForReview: String = "",
    val notificationReviewId: String = "",
    val notificationForComment: String = "",
    val notificationCommentId: String = "",
    val notificationInnerCommentId: String = "",
    val commentValue: String = "",
    val notificationForCommentVotes: String = "",
    val notificationForInnerComment: String = "",
    val parentCommentId: String = "",
    val innerCommentValue: String = "",
    val notificationForInnerCommentVotes: String = "",
    val parentCommentVal: String = ""
) : GetId()

// Votes
data class VotesModel(
    val votedBy: String = ""
) : GetId()

// Label
data class LabelModel(
    val questionId: String = "",
    val imageUrl: String = "",
    val labelName: String = ""
) : GetId()

// Label
data class ChatModel(
    val message: String = "",
    val messageOn: String = "",
    val messageTo: String = "",
    val messageBy: String = ""
) : GetId()

// Comment Model
data class CommentModel(
    val comment: String = "",
    val commentedOn: String = "",
    val commentedOnDate: String = "",
    val commentedOnTime: String = "",
    val givenBy: String = "",
    val likes: String = "",
    val dislikes: String = "",
    val innerComment: String = "",
    val parentComment: String = "",
    val isEdited: String = ""
) : GetId()
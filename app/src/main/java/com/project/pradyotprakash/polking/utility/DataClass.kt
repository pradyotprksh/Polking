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
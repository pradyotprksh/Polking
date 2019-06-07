package com.project.pradyotprakash.polking.utility

// Profile Page Background List
data class BgModel(val imageUrl: String = "") : GetId()

// Best Friend Questions
data class BestFrndQuesModel(val question: String = "", val askedBy: String = "", val askedOn: String = "") : GetId()
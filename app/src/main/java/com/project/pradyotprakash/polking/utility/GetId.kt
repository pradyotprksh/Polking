package com.project.pradyotprakash.polking.utility

open class GetId {

    var docId: String = ""

    fun <T : GetId> withId(id: String): T {
        this.docId = id
        return this as T
    }

}

package com.example.campusmarketplace.data

import com.google.firebase.database.Exclude

data class User (
    val uid: String? = null,
    val email : String? = null,
    val username : String? = null,
    val mobileNum : String? = null,
    var imagePath: String = ""
){
    constructor(): this("", "", "", "", "")

    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "uid" to uid,
            "email" to email,
            "username" to username,
            "mobileNum" to mobileNum,
            "imagePath" to imagePath
        )
    }
}

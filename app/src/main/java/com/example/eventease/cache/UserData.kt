package com.example.eventease.cache

object UserData {
    var uid: String = ""
    var name: String = ""
    var email: String = ""
    var photoUrl: String = ""

    fun set(uid: String, name: String, email: String, photoUrl: String = "") {
        this.uid = uid
        this.name = name
        this.email = email
        this.photoUrl = photoUrl
    }

    fun clear() {
        uid = ""
        name = ""
        email = ""
        photoUrl = ""
    }
}

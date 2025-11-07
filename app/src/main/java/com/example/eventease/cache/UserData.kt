package com.example.eventease.cache

object UserData {
    var uid: String = ""
    var name: String = ""
    var email: String = ""

    fun set(uid: String, name: String, email: String) {
        this.uid = uid
        this.name = name
        this.email = email
    }

    fun clear() {
        uid = ""
        name = ""
        email = ""
    }
}
package com.example.eventease.data.remote.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

object FirebaseProvider {
    val database: DatabaseReference by lazy {
        FirebaseDatabase.getInstance("https://weaveon-f5b5e-default-rtdb.asia-southeast1.firebasedatabase.app/").reference
    }

    val auth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
}
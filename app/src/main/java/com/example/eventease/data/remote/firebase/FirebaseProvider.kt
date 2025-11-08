package com.example.eventease.data.remote.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

object FirebaseProvider {
    val database: DatabaseReference by lazy {
        FirebaseDatabase.getInstance("https://eventease-4eab1-default-rtdb.asia-southeast1.firebasedatabase.app/").reference
    }

    val firestore: FirebaseFirestore get() = Firebase.firestore

    val auth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
}
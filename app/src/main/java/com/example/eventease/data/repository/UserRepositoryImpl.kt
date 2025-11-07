package com.example.eventease.data.repository

import android.content.Context
import com.example.eventease.cache.UserData
import com.example.eventease.data.datastore.UserPreferencesManager
import com.example.eventease.data.remote.firebase.FirebaseProvider
import com.example.eventease.domain.model.User
import com.example.eventease.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await

class UserRepositoryImpl(private val userPreferencesManager: UserPreferencesManager, ) : UserRepository {

    private val database = FirebaseProvider.database
    private val auth = FirebaseProvider.auth

    // function untuk mendapatkan user dari remote
    override suspend fun getUserFromRemote(uid: String): User? {
        val snapshot = database.child("users").child(uid).get().await()
        return snapshot.getValue(User::class.java)
    }

    // function untuk mendapatkan user dari cache
    override suspend fun saveUserToCache(uid: String, name: String, email: String) {
        userPreferencesManager.saveUser(uid, name, email)
        UserData.set(uid, name, email)
    }

    // function untuk mendapatkan user uid dari cache
    override fun getUserUidFlow(): Flow<String?> {
        return userPreferencesManager.userUid
    }

    // function untuk menghapus user dari cache
    override suspend fun logout() {
        userPreferencesManager.clear()
        UserData.clear()

        FirebaseProvider.auth.signOut()
    }

    // function untuk edit profile
    override suspend fun editProfile(uid: String, name: String, email: String): Boolean {
        try {

            // update ke firebase
            val userUpdates = hashMapOf<String, Any>(
                "uid" to uid,
                "name" to name,
                "email" to email
            )
            database.child("users").child(uid).updateChildren(userUpdates).await()

            // update di datastore
            userPreferencesManager.saveUser(uid, name, email)

            // update di cache
            UserData.set(uid, name, email)
            return true
        }

        catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    // function untuk hapus akun
    override suspend fun deleteAccount(uid: String) {
        try {
            // hapus user dari firebase
            auth.currentUser?.delete()?.await()
            database.child("users").child(uid).removeValue().await()

            // hapus user dari datastore
            userPreferencesManager.clear()

            // hapus user dari cache
            UserData.clear()
        }
        catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }
}
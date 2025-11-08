package com.example.eventease.data.repository

import android.content.Context
import android.net.Uri
import com.example.eventease.cache.UserData
import com.example.eventease.data.datastore.UserPreferencesManager
import com.example.eventease.data.remote.cloudinary.CloudinaryService
import com.example.eventease.data.remote.firebase.FirebaseProvider
import com.example.eventease.domain.model.User
import com.example.eventease.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await

class UserRepositoryImpl(
    private val userPreferencesManager: UserPreferencesManager,
    private val context: Context
) : UserRepository {

    private val database = FirebaseProvider.database
    private val auth = FirebaseProvider.auth
    private val cloudinaryService = CloudinaryService(context)


    override suspend fun getUserFromRemote(uid: String): User? {
        val snapshot = database.child("users").child(uid).get().await()
        return snapshot.getValue(User::class.java)
    }

    override suspend fun saveUserToCache(uid: String, name: String, email: String, photoUrl: String) {
        userPreferencesManager.saveUser(uid, name, email, photoUrl)
        UserData.set(uid, name, email, photoUrl)
    }

    override fun getUserUidFlow(): Flow<String?> {
        return userPreferencesManager.userUid
    }

    override suspend fun logout() {
        userPreferencesManager.clear()
        UserData.clear()

        FirebaseProvider.auth.signOut()
    }


    override suspend fun editProfile(
        uid: String,
        name: String,
        email: String,
        imageUri: Uri?
    ): Boolean {
        try {
            val photoUrl = if (imageUri != null) {
                cloudinaryService.uploadImage(imageUri)
            } else {
                ""
            }

            val userUpdates = hashMapOf<String, Any>(
                "uid" to uid,
                "name" to name,
                "email" to email,
                "photoUrl" to photoUrl
            )
            database.child("users").child(uid).updateChildren(userUpdates).await()

            userPreferencesManager.saveUser(uid, name, email, photoUrl)

            UserData.set(uid, name, email, photoUrl)

            return true
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }


    override suspend fun deleteAccount(uid: String) {
        try {
            auth.currentUser?.delete()?.await()
            database.child("users").child(uid).removeValue().await()

            userPreferencesManager.clear()

            UserData.clear()
        }
        catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }
}
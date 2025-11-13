package com.example.eventease.data.repository

import android.content.Context
import android.net.Uri
import com.example.eventease.cache.UserData
import com.example.eventease.data.datastore.UserPreferencesManager
import com.example.eventease.data.remote.ApiService
import com.example.eventease.data.remote.cloudinary.CloudinaryService
import com.example.eventease.domain.model.User
import com.example.eventease.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await

class UserRepositoryImpl(
    private val userPreferencesManager: UserPreferencesManager,
    private val context: Context,
    private val apiService: ApiService
) : UserRepository {

    private val cloudinaryService = CloudinaryService(context)

    override suspend fun getUserFromRemote(uid: String): User? {
        try {
            val response = apiService.getUserById(uid)
            return response.data?.toDomain()
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    override suspend fun saveUserToCache(uid: String, name: String, email: String, photoUrl: String) {
        val currentToken = userPreferencesManager.userToken.first() ?: ""
        userPreferencesManager.saveUser(uid, name, email, photoUrl, currentToken)
        UserData.set(uid, name, email, photoUrl)
    }

    override fun getUserUidFlow(): Flow<String?> {
        return userPreferencesManager.userUid
    }

    override suspend fun logout() {
        userPreferencesManager.clear()
        UserData.clear()
    }

    override suspend fun editProfile(
        uid: String,
        name: String,
        email: String,
        imageUri: Uri?
    ): Boolean {
        return try {
            val photoUrl = if (imageUri != null) {
                cloudinaryService.uploadImage(imageUri)
            } else {
                UserData.photoUrl
            }

            val userUpdates = mapOf(
                "name" to name,
                "email" to email,
                "profile_picture" to photoUrl
            )

            val response = apiService.updateProfile(userUpdates)
            val updatedUser = response.data

            if (updatedUser != null) {
                saveUserToCache(updatedUser.id.toString(), updatedUser.name, updatedUser.email, updatedUser.profilePicture ?: "")
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    override suspend fun deleteAccount(uid: String) {
        try {
            apiService.deleteAccount()

            userPreferencesManager.clear()
            UserData.clear()
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

}
package com.example.eventease.domain.repository

import android.net.Uri
import com.example.eventease.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun getUserFromRemote(uid: String): User?
    suspend fun saveUserToCache(uid: String, name: String, email: String, photoUrl: String)
    fun getUserUidFlow(): Flow<String?>
    suspend fun logout()
    suspend fun editProfile(uid: String, name: String, email: String, imageUri: Uri? = null): Boolean
    suspend fun deleteAccount(uid: String)
}
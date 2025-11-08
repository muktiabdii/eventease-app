package com.example.eventease.domain.usecase

import android.net.Uri
import com.example.eventease.domain.model.User
import com.example.eventease.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow

class UserUseCase(private val userRepository: UserRepository) {

    suspend fun getUserFromRemote(uid: String): User? {
        return userRepository.getUserFromRemote(uid)
    }

    suspend fun saveUserToCache(uid: String, name: String, email: String, photoUrl: String) {
        userRepository.saveUserToCache(uid, name, email, photoUrl)
    }

    fun getUserUidFlow(): Flow<String?> {
        return userRepository.getUserUidFlow()
    }

    suspend fun logout() {
        userRepository.logout()
    }

    suspend fun editProfile(uid: String, name: String, email: String, imageUri: Uri? = null): Boolean {
        return userRepository.editProfile(uid, name, email, imageUri)
    }

    suspend fun deleteAccount(uid: String) {
        userRepository.deleteAccount(uid)
    }
}
package com.example.eventease.domain.usecase

import com.example.eventease.domain.model.User
import com.example.eventease.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow

class UserUseCase(private val userRepository: UserRepository) {

    // function untuk mendapatkan user dari remote
    suspend fun getUserFromRemote(uid: String): User? {
        return userRepository.getUserFromRemote(uid)
    }

    // function untuk mendapatkan user dari cache
    suspend fun saveUserToCache(uid: String, name: String, email: String) {
        userRepository.saveUserToCache(uid, name, email)
    }

    // function untuk mendapatkan user uid dari cache
    fun getUserUidFlow(): Flow<String?> {
        return userRepository.getUserUidFlow()
    }

    // function logout
    suspend fun logout() {
        userRepository.logout()
    }

    // function untuk edit profile
    suspend fun editProfile(uid: String, name: String, email: String): Boolean {
        return userRepository.editProfile(uid, name, email)
    }

    // function untuk hapus akun
    suspend fun deleteAccount(uid: String) {
        userRepository.deleteAccount(uid)
    }
}
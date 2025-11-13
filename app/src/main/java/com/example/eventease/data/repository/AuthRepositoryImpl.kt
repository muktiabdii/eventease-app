package com.example.eventease.data.repository

import com.example.eventease.data.datastore.UserPreferencesManager
import com.example.eventease.data.remote.ApiService
import com.example.eventease.domain.repository.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthRepositoryImpl(
    private val apiService: ApiService,
    private val userPreferencesManager: UserPreferencesManager
): AuthRepository {

    override suspend fun login(
        email: String,
        password: String
    ): String = withContext(Dispatchers.IO) {
        try {
            val request = mapOf("email" to email, "password" to password)
            val response = apiService.login(request)

            val user = response.data.user
            val token = response.data.token

            userPreferencesManager.saveUser(
                uid = user.id.toString(),
                name = user.name,
                email = user.email,
                photoUrl = user.profilePicture ?: "",
                token = token
            )

            return@withContext user.id.toString()

        } catch (e: Exception) {
            throw Exception("Login Gagal: ${e.message}")
        }
    }

    override suspend fun register(
        name: String,
        email: String,
        password: String,
        passwordConfirmation: String
    ): Unit = withContext(Dispatchers.IO) {
        try {
            val request = mapOf("name" to name, "email" to email, "password" to password)
            apiService.register(request)
        } catch (e: Exception) {
            throw Exception("Registrasi Gagal: ${e.message}")
        }
    }

}
package com.example.eventease.domain.repository

interface AuthRepository {
    suspend fun login(email: String, password: String): String
    suspend fun register(name: String, email: String, password: String, passwordConfirmation: String): Unit
}
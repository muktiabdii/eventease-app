package com.example.eventease.data.repository

import android.util.Log
import com.example.eventease.data.remote.firebase.FirebaseProvider
import com.example.eventease.domain.repository.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class AuthRepositoryImpl: AuthRepository {

    private val auth = FirebaseProvider.auth
    private val database = FirebaseProvider.database

    override suspend fun login(
        email: String,
        password: String
    ): String {
        try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val uid = result.user?.uid ?: throw Exception("UID tidak ditemukan")
            return uid
        }

        catch (e: Exception) {
            throw Exception(getLocalizedErrorMessage(e.message))
        }
    }


    override suspend fun register(
        name: String,
        email: String,
        password: String,
        passwordConfirmation: String
    ): Unit = withContext(Dispatchers.IO) {
        try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user ?: throw Exception("Gagal membuat akun")

            val userId = user.uid
            val userRef = database.child("users").child(userId)
            val userData = mapOf(
                "uid" to userId,
                "name" to name,
                "email" to email
            )

            userRef.setValue(userData).await()

        } catch (e: Exception) {
            auth.currentUser?.delete()?.await()
            throw Exception(getLocalizedErrorMessage(e.message))
        }
    }


    private fun getLocalizedErrorMessage(errorMessage: String?): String {
        return when {
            errorMessage?.contains("The email address is badly formatted") == true ->
                "Format email salah"

            errorMessage?.contains("The supplied auth credential is incorrect, malformed or has expired.") == true ->
                "Silahkan periksa kembali email dan password Anda"

            errorMessage?.contains("A network error") == true ->
                "Terjadi kesalahan jaringan"

            errorMessage?.contains("The email address is already in use by another account") == true ->
                "Email sudah terdaftar"

            else -> errorMessage ?: "Terjadi kesalahan saat login"
        }
    }
}
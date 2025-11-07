package com.example.eventease.data.repository

import android.util.Log
import com.example.eventease.data.remote.firebase.FirebaseProvider
import com.example.eventease.domain.repository.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class AuthRepositoryImpl: AuthRepository {

    // inisiasi firebase auth dan database
    private val auth = FirebaseProvider.auth
    private val database = FirebaseProvider.database

    // function login
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


    // function register
    override suspend fun register(
        name: String,
        email: String,
        password: String,
        passwordConfirmation: String
    ): Unit = withContext(Dispatchers.IO) {
        try {
            Log.d("AuthRepository", "📝 Mulai register user: $name ($email)")
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user ?: throw Exception("Gagal membuat akun")
            Log.d("AuthRepository", "✅ Akun FirebaseAuth berhasil dibuat. UID: ${user.uid}")

            val userId = user.uid
            val userRef = database.child("users").child(userId)
            val userData = mapOf(
                "uid" to userId,
                "name" to name,
                "email" to email
            )

            Log.d("AuthRepository", "📦 Menulis data user ke Realtime Database...")
            userRef.setValue(userData).await()
            Log.d("AuthRepository", "🎉 Data user berhasil disimpan di Realtime Database")

        } catch (e: Exception) {
            Log.e("AuthRepository", "💥 Error register: ${e.message}")
            auth.currentUser?.let {
                Log.w("AuthRepository", "🧹 Menghapus akun karena error...")
                it.delete().await()
            }
            throw Exception(getLocalizedErrorMessage(e.message))
        }
    }


    // function untuk mendapatkan pesan error yang sesuai
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
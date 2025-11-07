package com.example.eventease.domain.usecase

import com.example.eventease.domain.repository.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthUseCase(private val authRepository: AuthRepository) {

    // function login
    suspend fun login(
        email: String,
        password: String
    ): Result<String> {
        val validateLogin = validateLogin(email, password)
        if (validateLogin != null) {
            return Result.failure(Exception(validateLogin))
        }

        return try {
            val uid = authRepository.login(email.trim(), password.trim())
            Result.success(uid)
        }

        catch (e: Exception) {
            Result.failure(e)
        }
    }

    // function register
    suspend fun register(
        name: String,
        email: String,
        password: String,
        passwordConfirmation: String
    ): Result<Unit> = withContext(Dispatchers.IO) {
        validateRegister(name, email, password, passwordConfirmation)?.let {
            return@withContext Result.failure(Exception(it))
        }

        try {
            authRepository.register(name.trim(), email.trim(), password.trim(), passwordConfirmation.trim())
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // function validasi login
    private fun validateLogin(email: String, password: String): String? {
        if (email.trim().isEmpty() || password.trim().isEmpty()) {
            return "Email dan password harus diisi"
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return "Format email salah"
        }
        return null
    }

    // function validasi register
    private fun validateRegister(name: String, email: String, password: String, passwordConfirmation: String): String? {
        if (name.trim().isEmpty() || email.trim().isEmpty() || password.trim().isEmpty() || passwordConfirmation.trim().isEmpty()) {
            return "Semua field harus diisi"
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return "Format email salah"
        }

        if (password.length < 8) {
            return "Password harus lebih dari 8 karakter"
        }

        if (password != passwordConfirmation) {
            return "Konfirmasi password tidak sesuai"
        }
        return null
    }
}
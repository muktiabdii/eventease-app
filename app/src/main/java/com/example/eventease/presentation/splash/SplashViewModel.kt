package com.example.eventease.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.eventease.domain.usecase.UserUseCase
import kotlinx.coroutines.flow.Flow

class SplashViewModel(private val userUseCase: UserUseCase) : ViewModel() {

    // function untuk mendapatkan user uid dari cache
    fun getUserUidFlow(): Flow<String?> {
        return userUseCase.getUserUidFlow()
    }

    // function untuk load user
    suspend fun loadUser(uid: String) {
        val user = userUseCase.getUserFromRemote(uid)
        if (user != null) {
            userUseCase.saveUserToCache(user.uid, user.name, user.email)
        }
    }

    class Factory(private val userUseCase: UserUseCase) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SplashViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return SplashViewModel(userUseCase) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
package com.example.eventease.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.eventease.domain.usecase.AuthUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import androidx.lifecycle.viewModelScope
import com.example.eventease.domain.usecase.UserUseCase
import kotlinx.coroutines.launch

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object Success : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel(
    private val authUseCase: AuthUseCase,
    private val userUseCase: UserUseCase
): ViewModel() {
    private val _loginState = MutableStateFlow<AuthState>(AuthState.Idle)
    val loginState: StateFlow<AuthState> = _loginState

    private val _registerState = MutableStateFlow<AuthState>(AuthState.Idle)
    val registerState: StateFlow<AuthState> = _registerState

    fun resetLoginState() {
        _loginState.value = AuthState.Idle
    }

    fun resetRegisterState() {
        _registerState.value = AuthState.Idle
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = AuthState.Loading
            val result = authUseCase.login(email, password)
            result.onSuccess { uid ->
                if (uid.isNotEmpty()) {
                    loadUser(uid)
                }

                _loginState.value = AuthState.Success
            }.onFailure { e ->
                _loginState.value = AuthState.Error(e.message ?: "Login gagal")
            }
        }
    }

    suspend fun loadUser(uid: String) {
        val user = userUseCase.getUserFromRemote(uid)
        if (user != null) {
            userUseCase.saveUserToCache(user.uid, user.name, user.email, user.photoUrl)
        }
    }

    fun register(
        name: String,
        email: String,
        password: String,
        passwordConfirmation: String
    ) {
        viewModelScope.launch {
            _registerState.value = AuthState.Loading

            val result = authUseCase.register(name, email, password, passwordConfirmation)

            result.onSuccess {
                _registerState.value = AuthState.Success
            }.onFailure { e ->
                _registerState.value = AuthState.Error(e.message ?: "Register gagal")
            }
        }
    }



    class Factory(
        private val authUseCase: AuthUseCase,
        private val userUseCase: UserUseCase
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return AuthViewModel(authUseCase, userUseCase) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
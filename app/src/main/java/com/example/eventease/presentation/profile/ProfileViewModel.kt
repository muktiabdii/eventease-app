package com.example.eventease.presentation.profile

import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.eventease.cache.UserData
import com.example.eventease.domain.usecase.UserUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

data class State(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val photoUrl: String = ""
)

sealed class ProfileUiState {
    object Idle : ProfileUiState()
    object Loading : ProfileUiState()
    data class Success(val message: String) : ProfileUiState()
    data class Error(val message: String) : ProfileUiState()
}

class UserViewModel(private val userUseCase: UserUseCase) : ViewModel() {

    private val _userState = MutableStateFlow(State())
    val userState: StateFlow<State> = _userState

    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Idle)
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    fun setName(name: String) {
        _userState.value = _userState.value.copy(name = name)
    }

    fun setEmail(email: String) {
        _userState.value = _userState.value.copy(email = email)
    }

    init {
        loadProfileFromNetwork()
    }

    fun loadProfileFromNetwork() {
        viewModelScope.launch {
            val uid = userUseCase.getUserUidFlow().firstOrNull()
            if (uid == null) {
                _uiState.value = ProfileUiState.Error("User not logged in")
                return@launch
            }

            val user = userUseCase.getUserFromRemote(uid)
            if (user != null) {
                userUseCase.saveUserToCache(user.uid, user.name, user.email, user.photoUrl)

                _userState.value = State(
                    uid = user.uid,
                    name = user.name,
                    email = user.email,
                    photoUrl = user.photoUrl
                )
            } else {
                _uiState.value = ProfileUiState.Error("Failed to fetch profile")
            }
        }
    }

    fun resetUiState() {
        _uiState.value = ProfileUiState.Idle
    }

    fun setTempPhoto(photoUrl: String) {
        _userState.value = _userState.value.copy(photoUrl = photoUrl)
    }

    fun editProfile(name: String, email: String, imageUri: Uri? = null) {
        viewModelScope.launch {
            _uiState.value = ProfileUiState.Loading
            try {
                val uid = _userState.value.uid
                val result = userUseCase.editProfile(uid, name, email, imageUri)

                if (result) {
                    loadProfileFromNetwork()
                    _uiState.value = ProfileUiState.Success("Profile updated successfully!")
                } else {
                    _uiState.value = ProfileUiState.Error("Failed to update profile")
                }
            } catch (e: Exception) {
                _uiState.value = ProfileUiState.Error(e.message ?: "An unknown error occurred")
                e.printStackTrace()
            }
        }
    }

    fun logout(onComplete: (() -> Unit)? = null) {
        viewModelScope.launch {
            userUseCase.logout()
            _userState.value = State()
            onComplete?.invoke()
        }
    }

    fun deleteAccount(onComplete: (() -> Unit)? = null) {
        viewModelScope.launch {
            try {
                val uid = _userState.value.uid
                userUseCase.deleteAccount(uid)
                _userState.value = State()
                onComplete?.invoke()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    class Factory(private val userUseCase: UserUseCase) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return UserViewModel(userUseCase) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
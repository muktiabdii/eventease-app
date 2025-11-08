package com.example.eventease.presentation.profile

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.eventease.cache.UserData
import com.example.eventease.domain.usecase.UserUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class State(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val photoUrl: String = ""
)

class UserViewModel(private val userUseCase: UserUseCase) : ViewModel() {

    private val _userState = MutableStateFlow(State())
    val userState: StateFlow<State> = _userState

    fun setName(name: String) {
        _userState.value = _userState.value.copy(name = name)
    }

    fun setEmail(email: String) {
        _userState.value = _userState.value.copy(email = email)
    }

    init {
        loadUserData()
    }

    fun loadUserData() {
        viewModelScope.launch {
            _userState.value = State(
                uid = UserData.uid,
                name = UserData.name,
                email = UserData.email,
                photoUrl = UserData.photoUrl
            )
        }
    }

    fun refreshUserData() {
        loadUserData()
    }

    fun setTempPhoto(photoUrl: String) {
        _userState.value = _userState.value.copy(photoUrl = photoUrl)
    }

    fun editProfile(name: String, email: String, imageUri: Uri? = null) {
        viewModelScope.launch {
            try {
                val uid = _userState.value.uid
                val result = userUseCase.editProfile(uid, name, email, imageUri)
                if (result) {
                    _userState.value = _userState.value.copy(
                        name = name,
                        email = email,
                        photoUrl = UserData.photoUrl
                    )
                }
            } catch (e: Exception) {
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
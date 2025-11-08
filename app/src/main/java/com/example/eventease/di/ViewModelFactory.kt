package com.example.eventease.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.eventease.domain.usecase.AuthUseCase
import com.example.eventease.domain.usecase.EventUseCase
import com.example.eventease.domain.usecase.UserUseCase
import com.example.eventease.presentation.auth.AuthViewModel
import com.example.eventease.presentation.create.CreateEventViewModel
import com.example.eventease.presentation.detail.DetailEventViewModel
import com.example.eventease.presentation.home.HomeViewModel
import com.example.eventease.presentation.myevents.MyEventsViewModel
import com.example.eventease.presentation.splash.SplashViewModel

class ViewModelFactory(
    private val authUseCase: AuthUseCase,
    private val userUseCase: UserUseCase,
    private val eventUseCase: EventUseCase
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>,
        extras: CreationExtras
    ): T {
        return when {
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> {
                AuthViewModel(authUseCase, userUseCase) as T
            }
            modelClass.isAssignableFrom(SplashViewModel::class.java) -> {
                SplashViewModel(userUseCase) as T
            }
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(eventUseCase) as T
            }
            modelClass.isAssignableFrom(CreateEventViewModel::class.java) -> {
                CreateEventViewModel(eventUseCase) as T
            }
            modelClass.isAssignableFrom(DetailEventViewModel::class.java) -> {
                val savedStateHandle = extras.createSavedStateHandle()
                DetailEventViewModel(eventUseCase, userUseCase, savedStateHandle) as T
            }
            modelClass.isAssignableFrom(MyEventsViewModel::class.java) -> {
                MyEventsViewModel(eventUseCase) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
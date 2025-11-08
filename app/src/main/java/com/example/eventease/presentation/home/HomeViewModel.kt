package com.example.eventease.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventease.data.domain.model.Event
import com.example.eventease.domain.usecase.EventUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
sealed class HomeState {
    object Loading : HomeState()
    data class Success(val events: List<Event>) : HomeState()
    data class Error(val message: String) : HomeState()
}

class HomeViewModel(private val eventUseCase: EventUseCase) : ViewModel() {

    private val _homeState = MutableStateFlow<HomeState>(HomeState.Loading)
    val homeState: StateFlow<HomeState> = _homeState

    init {
        fetchAllEvents()
    }

    private fun fetchAllEvents() {
        viewModelScope.launch {
            eventUseCase.getAllEvents().collect { result ->
                result.onSuccess { events ->
                    _homeState.value = HomeState.Success(events)
                }.onFailure { error ->
                    _homeState.value = HomeState.Error(error.message ?: "An unknown error occurred")
                }
            }
        }
    }
}
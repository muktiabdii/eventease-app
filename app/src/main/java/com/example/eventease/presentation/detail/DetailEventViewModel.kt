package com.example.eventease.presentation.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventease.NavDestination
import com.example.eventease.data.domain.model.Event
import com.example.eventease.domain.usecase.EventUseCase
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

sealed class DetailEventState {
    object Loading : DetailEventState()
    data class Success(val event: Event) : DetailEventState()
    data class Error(val message: String) : DetailEventState()
}

class DetailEventViewModel(
    private val eventUseCase: EventUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val eventId: String = savedStateHandle[NavDestination.EVENT_DETAIL_ID_ARG] ?: ""
    private val currentUserId: String? = Firebase.auth.currentUser?.uid

    private val _uiState = MutableStateFlow<DetailEventState>(DetailEventState.Loading)
    val uiState: StateFlow<DetailEventState> = _uiState.asStateFlow()

    // State untuk melacak apakah user saat ini sudah "attend"
    private val _isAttending = MutableStateFlow(false)
    val isAttending: StateFlow<Boolean> = _isAttending.asStateFlow()

    init {
        fetchEventDetails()
    }

    private fun fetchEventDetails() {
        viewModelScope.launch {
            eventUseCase.getEventDetails(eventId).collectLatest { result ->
                result.onSuccess { event ->
                    _uiState.value = DetailEventState.Success(event)
                    _isAttending.value = event.participants.contains(currentUserId)
                }.onFailure {
                    _uiState.value = DetailEventState.Error(it.message ?: "Failed to load event")
                }
            }
        }
    }

    fun onAttendClicked() {
        viewModelScope.launch {
            if (!_isAttending.value) {
                eventUseCase.attendEvent(eventId)
                // State akan update otomatis berkat snapshotListener
            }
        }
    }

    fun onCancelAttendanceClicked() {
        viewModelScope.launch {
            if (_isAttending.value) {
                eventUseCase.cancelAttendance(eventId)
                // State akan update otomatis
            }
        }
    }
}
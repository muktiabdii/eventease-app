package com.example.eventease.presentation.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventease.NavDestination
import com.example.eventease.data.domain.model.Event
import com.example.eventease.domain.model.User
import com.example.eventease.domain.usecase.EventUseCase
import com.example.eventease.domain.usecase.UserUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

sealed class DetailEventState {
    object Loading : DetailEventState()
    data class Success(
        val event: Event,
        val organizer: User?,
        val participants: List<User>,
        val isCreator: Boolean
    ) : DetailEventState()
    data class Error(val message: String) : DetailEventState()
}

class DetailEventViewModel(
    private val eventUseCase: EventUseCase,
    private val userUseCase: UserUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val eventId: String = savedStateHandle[NavDestination.EVENT_DETAIL_ID_ARG] ?: ""
    private var currentUserId: String? = null

    private val _uiState = MutableStateFlow<DetailEventState>(DetailEventState.Loading)
    val uiState: StateFlow<DetailEventState> = _uiState.asStateFlow()

    private val _isAttending = MutableStateFlow(false)
    val isAttending: StateFlow<Boolean> = _isAttending.asStateFlow()

    private val _isAttendingLoading = MutableStateFlow(false)
    val isAttendingLoading: StateFlow<Boolean> = _isAttendingLoading.asStateFlow()

    init {
        viewModelScope.launch {
            currentUserId = userUseCase.getUserUidFlow().firstOrNull()
            fetchEventDetails(showLoadingScreen = true)
        }
    }

    private fun fetchEventDetails(showLoadingScreen: Boolean = false) {
        viewModelScope.launch {
            if (showLoadingScreen) {
                _uiState.value = DetailEventState.Loading
            }

            eventUseCase.getEventDetails(eventId).collectLatest { result ->
                result.onSuccess { event ->
                    _isAttending.value = event.participants.contains(currentUserId)

                    val organizer = userUseCase.getUserFromRemote(event.creatorId)
                    val participants = event.participants.mapNotNull { uid ->
                        userUseCase.getUserFromRemote(uid)
                    }
                    val isCurrentUserTheCreator = (event.creatorId == currentUserId)

                    _uiState.value = DetailEventState.Success(
                        event = event,
                        organizer = organizer,
                        participants = participants,
                        isCreator = isCurrentUserTheCreator
                    )
                }.onFailure {
                    _uiState.value = DetailEventState.Error(it.message ?: "Failed to load event")
                }
            }
        }
    }

    fun onAttendClicked() {
        viewModelScope.launch {
            if (!_isAttending.value) {
                _isAttendingLoading.value = true

                val attendResult = eventUseCase.attendEvent(eventId)

                if (attendResult.isSuccess) {
                    val refreshResult = eventUseCase.getEventDetails(eventId).first()

                    refreshResult.onSuccess { event ->
                        _isAttending.value = event.participants.contains(currentUserId)
                        val organizer = userUseCase.getUserFromRemote(event.creatorId)
                        val participants = event.participants.mapNotNull { uid ->
                            userUseCase.getUserFromRemote(uid)
                        }
                        val isCurrentUserTheCreator = (event.creatorId == currentUserId)

                        _uiState.value = DetailEventState.Success(
                            event = event,
                            organizer = organizer,
                            participants = participants,
                            isCreator = isCurrentUserTheCreator
                        )
                    }.onFailure {
                        _uiState.value = DetailEventState.Error(it.message ?: "Failed to refresh details")
                    }
                } else {
                    _uiState.value = DetailEventState.Error(attendResult.exceptionOrNull()?.message ?: "Failed to attend")
                }

                _isAttendingLoading.value = false
            }
        }
    }

    fun onCancelAttendanceClicked() {
        viewModelScope.launch {
            if (_isAttending.value) {
                _isAttendingLoading.value = true

                val cancelResult = eventUseCase.cancelAttendance(eventId)

                if (cancelResult.isSuccess) {
                    val refreshResult = eventUseCase.getEventDetails(eventId).first()

                    refreshResult.onSuccess { event ->
                        _isAttending.value = event.participants.contains(currentUserId)
                        val organizer = userUseCase.getUserFromRemote(event.creatorId)
                        val participants = event.participants.mapNotNull { uid ->
                            userUseCase.getUserFromRemote(uid)
                        }
                        val isCurrentUserTheCreator = (event.creatorId == currentUserId)

                        _uiState.value = DetailEventState.Success(
                            event = event,
                            organizer = organizer,
                            participants = participants,
                            isCreator = isCurrentUserTheCreator
                        )
                    }.onFailure {
                        _uiState.value = DetailEventState.Error(it.message ?: "Failed to refresh details")
                    }
                } else {
                    _uiState.value = DetailEventState.Error(cancelResult.exceptionOrNull()?.message ?: "Failed to cancel")
                }

                _isAttendingLoading.value = false
            }
        }
    }
}
package com.example.eventease.presentation.myevents

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventease.data.domain.model.Event
import com.example.eventease.domain.usecase.EventUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MyEventsUiState(
    val isLoading: Boolean = true,
    val createdEvents: List<Event> = emptyList(),
    val joinedEvents: List<Event> = emptyList(),
    val error: String? = null
)

class MyEventsViewModel(private val eventUseCase: EventUseCase) : ViewModel() {

    private val _uiState = MutableStateFlow(MyEventsUiState())
    val uiState: StateFlow<MyEventsUiState> = _uiState.asStateFlow()

    init {
        fetchMyEvents()
    }

    private fun fetchMyEvents() {
        viewModelScope.launch {
            combine(
                eventUseCase.getCreatedEvents(),
                eventUseCase.getJoinedEvents()
            ) { createdResult, joinedResult ->

                val created = createdResult.getOrNull() ?: emptyList()
                val joined = joinedResult.getOrNull() ?: emptyList()

                val error = createdResult.exceptionOrNull()?.message
                    ?: joinedResult.exceptionOrNull()?.message

                MyEventsUiState(
                    isLoading = false,
                    createdEvents = created,
                    joinedEvents = joined,
                    error = error
                )

            }.collect { combinedState ->
                _uiState.value = combinedState
            }
        }
    }
    fun deleteEvent(event: Event) {
        viewModelScope.launch {
            val result = eventUseCase.deleteEvent(event.id)

            result.onFailure { error ->
                _uiState.update {
                    it.copy(error = error.message ?: "Failed to delete event")
                }
            }

            result.onSuccess {
                _uiState.update { currentState ->
                    val updatedCreatedEvents = currentState.createdEvents.filter { it.id != event.id }

                    currentState.copy(
                        createdEvents = updatedCreatedEvents
                    )
                }
            }
        }
    }
}
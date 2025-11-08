package com.example.eventease.presentation.create

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventease.data.domain.model.Event
import com.example.eventease.domain.usecase.EventUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

// State untuk UI (Loading, Success, Error)
sealed class CreateEventState {
    object Idle : CreateEventState()
    object Loading : CreateEventState()
    object Success : CreateEventState()
    data class Error(val message: String) : CreateEventState()
}

class CreateEventViewModel(private val eventUseCase: EventUseCase) : ViewModel() {

    // State untuk form fields
    var title by mutableStateOf("")
    var description by mutableStateOf("")
    var location by mutableStateOf("")
    var capacity by mutableStateOf("")
    var dateText by mutableStateOf("mm/dd/yyyy")
    var timeText by mutableStateOf("--:-- --")
    var imageUri by mutableStateOf<Uri?>(null) // State untuk menyimpan Uri gambar

    // State untuk UI
    private val _uiState = MutableStateFlow<CreateEventState>(CreateEventState.Idle)
    val uiState: StateFlow<CreateEventState> = _uiState

    fun onDateChange(newDateMillis: Long?) {
        if (newDateMillis != null) {
            val dateFormatter = SimpleDateFormat("MM/dd/yyyy", Locale.US)
            dateText = dateFormatter.format(newDateMillis)
        }
    }

    fun onTimeChange(hour: Int, minute: Int) {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
        }
        val timeFormatter = SimpleDateFormat("hh:mm aa", Locale.US)
        timeText = timeFormatter.format(calendar.time)
    }

    fun onTitleChange(newTitle: String) { title = newTitle }
    fun onDescriptionChange(newDesc: String) { description = newDesc }
    fun onLocationChange(newLoc: String) { location = newLoc }
    fun onCapacityChange(newCap: String) { capacity = newCap }
    fun onImagePicked(uri: Uri?) { imageUri = uri }

    fun resetState() {
        _uiState.value = CreateEventState.Idle
    }

    fun saveEvent() {
        viewModelScope.launch {
            val localImageUri = imageUri
            if (localImageUri == null) {
                _uiState.value = CreateEventState.Error("Please pick an event poster")
                return@launch
            }
            if (dateText == "mm/dd/yyyy" || timeText == "--:-- --") {
                _uiState.value = CreateEventState.Error("Please select a valid date and time")
                return@launch
            }

            if (description.trim().length < 800) {
                _uiState.value = CreateEventState.Error("Description must be at least 800 characters.")
                return@launch
            }

            _uiState.value = CreateEventState.Loading

            val event = Event(
                title = title,
                description = description,
                location = location,
                date = "$dateText at $timeText",
                capacity = capacity // <-- Perbaikan dari bug sebelumnya
            )

            val result = eventUseCase.createEvent(event, localImageUri)

            result.onSuccess {
                _uiState.value = CreateEventState.Success
            }.onFailure {
                _uiState.value = CreateEventState.Error(it.message ?: "Failed to create event")
            }
        }
    }
}
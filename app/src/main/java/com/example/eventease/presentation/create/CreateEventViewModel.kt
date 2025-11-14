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

sealed class CreateEventState {
    object Idle : CreateEventState()
    object Loading : CreateEventState()
    object Success : CreateEventState()
    data class Error(val message: String) : CreateEventState()
}

class CreateEventViewModel(private val eventUseCase: EventUseCase) : ViewModel() {

    var title by mutableStateOf("")
    var description by mutableStateOf("")
    var location by mutableStateOf("")
    var capacity by mutableStateOf("")
    var dateText by mutableStateOf("mm/dd/yyyy")
    var timeText by mutableStateOf("--:-- --")
    var imageUri by mutableStateOf<Uri?>(null)

    private var selectedDateMillis: Long? = null
    private var selectedHour: Int? = null
    private var selectedMinute: Int? = null

    private val _uiState = MutableStateFlow<CreateEventState>(CreateEventState.Idle)
    val uiState: StateFlow<CreateEventState> = _uiState

    fun onDateChange(newDateMillis: Long?) {
        if (newDateMillis != null) {
            selectedDateMillis = newDateMillis

            val dateFormatter = SimpleDateFormat("MM/dd/yyyy", Locale.US)
            dateText = dateFormatter.format(newDateMillis)
        }
    }

    fun onTimeChange(hour: Int, minute: Int) {
        selectedHour = hour
        selectedMinute = minute

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

            if (selectedDateMillis == null || selectedHour == null || selectedMinute == null) {
                _uiState.value = CreateEventState.Error("Please select a valid date and time")
                return@launch
            }

            if (description.trim().length < 200) {
                _uiState.value = CreateEventState.Error("Description must be at least 200 characters.")
                return@launch
            }

            _uiState.value = CreateEventState.Loading

            val finalCalendar = Calendar.getInstance().apply {
                timeInMillis = selectedDateMillis!!
                set(Calendar.HOUR_OF_DAY, selectedHour!!)
                set(Calendar.MINUTE, selectedMinute!!)
                set(Calendar.SECOND, 0)
            }

            val mysqlDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
            val backendDateString = mysqlDateFormat.format(finalCalendar.time)

            val event = Event(
                title = title,
                description = description,
                location = location,
                date = backendDateString,
                capacity = capacity
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
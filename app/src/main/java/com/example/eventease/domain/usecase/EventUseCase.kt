package com.example.eventease.domain.usecase

import android.net.Uri
import com.example.eventease.data.domain.model.Event
import com.example.eventease.domain.repository.EventRepository

class EventUseCase(private val eventRepository: EventRepository) {
    fun getAllEvents() = eventRepository.getAllEvents()
    fun getEventDetails(eventId: String) = eventRepository.getEventDetails(eventId)
    fun getCreatedEvents() = eventRepository.getCreatedEvents()
    fun getJoinedEvents() = eventRepository.getJoinedEvents()

    suspend fun createEvent(event: Event, imageUri: Uri): Result<Unit> {
        if (event.title.isBlank() || event.location.isBlank() || event.date.isBlank()) {
            return Result.failure(Exception("Title, Location, and Date cannot be empty"))
        }
        return eventRepository.createEvent(event, imageUri)
    }
    suspend fun attendEvent(eventId: String) = eventRepository.attendEvent(eventId)
    suspend fun cancelAttendance(eventId: String) = eventRepository.cancelAttendance(eventId)
    suspend fun deleteEvent(eventId: String) = eventRepository.deleteEvent(eventId)
}
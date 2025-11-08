package com.example.eventease.domain.repository

import android.net.Uri
import com.example.eventease.data.domain.model.Event
import kotlinx.coroutines.flow.Flow

interface EventRepository {
    fun getAllEvents(): Flow<Result<List<Event>>>
    fun getEventDetails(eventId: String): Flow<Result<Event>>
    fun getCreatedEvents(): Flow<Result<List<Event>>>
    fun getJoinedEvents(): Flow<Result<List<Event>>>
    suspend fun createEvent(event: Event, imageUri: Uri): Result<Unit>
    suspend fun attendEvent(eventId: String): Result<Unit>
    suspend fun deleteEvent(eventId: String): Result<Unit>
    suspend fun cancelAttendance(eventId: String): Result<Unit>
}
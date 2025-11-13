package com.example.eventease.data.repository

import android.content.Context
import android.net.Uri
import com.example.eventease.data.domain.model.Event
import com.example.eventease.data.remote.ApiService
import com.example.eventease.data.remote.cloudinary.CloudinaryService
import com.example.eventease.data.remote.dto.CreateEventRequest
import com.example.eventease.domain.repository.EventRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class EventRepositoryImpl(
    context: Context,
    private val apiService: ApiService
) : EventRepository {

    private val cloudinaryService = CloudinaryService(context)

    override fun getAllEvents(): Flow<Result<List<Event>>> = flow {
        val response = apiService.getAllEvents()
        val events = response.data?.map { it.toDomain() } ?: emptyList()
        emit(Result.success(events))
    }.catch { e ->
        emit(Result.failure(e as Exception))
    }

    override suspend fun createEvent(event: Event, imageUri: Uri): Result<Unit> {
        return try {
            val imageUrl = cloudinaryService.uploadImage(imageUri)
            val request = CreateEventRequest(
                title = event.title,
                description = event.description,
                date = event.date,
                location = event.location,
                capacity = event.capacity.toIntOrNull() ?: 0,
                image = imageUrl
            )
            apiService.createEvent(request)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getEventDetails(eventId: String): Flow<Result<Event>> = flow {
        val response = apiService.getEventDetails(eventId)
        if (response.data != null) {
            emit(Result.success(response.data.toDomain()))
        } else {
            emit(Result.failure(Exception(response.error ?: "Event not found")))
        }
    }.catch { e ->
        emit(Result.failure(e as Exception))
    }

    override suspend fun attendEvent(eventId: String): Result<Unit> {
        return try {
            apiService.joinEvent(eventId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun cancelAttendance(eventId: String): Result<Unit> {
        return try {
            apiService.leaveEvent(eventId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getCreatedEvents(): Flow<Result<List<Event>>> = flow {
        val response = apiService.getMyCreatedEvents()
        val events = response.data?.map { it.toDomain() } ?: emptyList()
        emit(Result.success(events))
    }.catch { e ->
        emit(Result.failure(e as Exception))
    }

    override fun getJoinedEvents(): Flow<Result<List<Event>>> = flow {
        val response = apiService.getMyJoinedEvents()
        val events = response.data?.map { it.toDomain() } ?: emptyList()
        emit(Result.success(events))
    }.catch { e ->
        emit(Result.failure(e as Exception))
    }

    override suspend fun deleteEvent(eventId: String): Result<Unit> {
        return try {
            apiService.deleteEvent(eventId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
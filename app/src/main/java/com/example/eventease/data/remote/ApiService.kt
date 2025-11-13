package com.example.eventease.data.remote

import com.example.eventease.data.remote.dto.*
import retrofit2.http.*

interface ApiService {

    @POST("api/users/register")
    suspend fun register(@Body request: Map<String, String>): AuthResponse

    @POST("api/users/login")
    suspend fun login(@Body request: Map<String, String>): AuthResponse

    @GET("api/users/profile")
    suspend fun getProfile(): GeneralResponse<UserDto>

    @PUT("api/users/profile")
    suspend fun updateProfile(@Body request: Map<String, String?>): GeneralResponse<UserDto>

    @DELETE("api/users/profile")
    suspend fun deleteAccount(): GeneralResponse<Unit>

    @GET("api/users/{id}")
    suspend fun getUserById(@Path("id") userId: String): GeneralResponse<UserDto>

    @POST("api/events")
    suspend fun createEvent(@Body request: CreateEventRequest): GeneralResponse<EventDto>

    @GET("api/events")
    suspend fun getAllEvents(): GeneralResponse<List<EventDto>>

    @GET("api/events/{id}")
    suspend fun getEventDetails(@Path("id") eventId: String): GeneralResponse<EventDto>

    @PUT("api/events/{id}")
    suspend fun updateEvent(
        @Path("id") eventId: String,
        @Body request: CreateEventRequest
    ): GeneralResponse<EventDto>

    @DELETE("api/events/{id}")
    suspend fun deleteEvent(@Path("id") eventId: String): GeneralResponse<Unit>

    @POST("api/events/{id}/join")
    suspend fun joinEvent(@Path("id") eventId: String): GeneralResponse<Unit>

    @DELETE("api/events/{id}/leave")
    suspend fun leaveEvent(@Path("id") eventId: String): GeneralResponse<Unit>

    @GET("api/events/created/my-events")
    suspend fun getMyCreatedEvents(): GeneralResponse<List<EventDto>>

    @GET("api/events/joined/my-events")
    suspend fun getMyJoinedEvents(): GeneralResponse<List<EventDto>>
}
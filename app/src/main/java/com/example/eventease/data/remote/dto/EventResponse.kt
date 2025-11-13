package com.example.eventease.data.remote.dto

import com.example.eventease.data.domain.model.Event
import com.google.gson.annotations.SerializedName

data class EventDto(
    val id: Int,
    val title: String,
    val description: String,
    val date: String,
    val location: String,
    val capacity: Int,
    val image: String?,
    @SerializedName("creator_id")
    val creatorId: Int,
    @SerializedName("creator_name")
    val creatorName: String?,

    val participants: List<UserDto>?
) {
    fun toDomain(): Event {
        return Event(
            id = id.toString(),
            title = title,
            description = description,
            date = date,
            location = location,
            capacity = capacity.toString(),
            imageUrl = image ?: "",
            creatorId = creatorId.toString(),
            participants = participants?.map { it.id.toString() } ?: emptyList()
        )
    }
}

data class CreateEventRequest(
    val title: String,
    val description: String,
    val date: String,
    val location: String,
    val capacity: Int,
    val image: String?
)

data class GeneralResponse<T>(
    val message: String? = null,
    val data: T? = null,
    val error: String? = null
)
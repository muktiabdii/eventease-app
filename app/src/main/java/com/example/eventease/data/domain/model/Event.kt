package com.example.eventease.data.domain.model


data class Event(
    val id: String = "",
    val title: String = "",
    val date: String = "",
    val location: String = "",
    val creatorId: String = "",
    val participants: List<String> = emptyList(),
    val imageUrl: String = "",
    val description: String = "",
    val capacity: String = ""
)
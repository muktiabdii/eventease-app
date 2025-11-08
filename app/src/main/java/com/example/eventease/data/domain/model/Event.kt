package com.example.eventease.data.domain.model

import com.google.firebase.firestore.DocumentId

data class Event(
    @DocumentId
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
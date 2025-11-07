package com.example.eventease.data.domain.model

data class Event(
    val id: Int,
    val title: String,
    val date: String,
    val location: String,
    val imageRes: Int
)
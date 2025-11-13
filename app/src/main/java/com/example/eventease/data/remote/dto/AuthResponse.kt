package com.example.eventease.data.remote.dto

import com.example.eventease.domain.model.User
import com.google.gson.annotations.SerializedName

data class UserDto(
    val id: Int,
    val name: String,
    val email: String,
    @SerializedName("profile_picture")
    val profilePicture: String?
) {
    fun toDomain(): User {
        return User(
            uid = id.toString(),
            name = name,
            email = email,
            photoUrl = profilePicture ?: ""
        )
    }
}

data class AuthResponse(
    val message: String,
    val data: AuthData
)

data class AuthData(
    val user: UserDto,
    val token: String
)
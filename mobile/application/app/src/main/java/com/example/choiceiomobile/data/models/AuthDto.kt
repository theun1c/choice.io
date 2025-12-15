package com.example.choiceiomobile.data.models

data class AuthRequest(
    val name: String,
    val password: String
)

data class AuthResponse(
    val success: Boolean,
    val message: String
)


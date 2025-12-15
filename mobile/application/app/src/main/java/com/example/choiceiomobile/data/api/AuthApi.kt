package com.example.choiceiomobile.data.api

import com.example.choiceiomobile.data.models.AuthRequest
import com.example.choiceiomobile.data.models.AuthResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("/api/auth/register")
    suspend fun register(@Body request: AuthRequest): AuthResponse

    @POST("/api/auth/login")
    suspend fun login(@Body request: AuthRequest): AuthResponse
}


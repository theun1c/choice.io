package com.example.choiceiomobile.domain.repository

import com.example.choiceiomobile.domain.models.User

interface AuthRepository {
    suspend fun login(username: String, password: String): Result<User>
    suspend fun register(username: String, password: String): Result<User>
}
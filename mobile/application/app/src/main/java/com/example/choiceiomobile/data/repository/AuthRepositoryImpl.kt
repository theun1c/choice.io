package com.example.choiceiomobile.data.repository

import com.example.choiceiomobile.data.api.ApiClient
import com.example.choiceiomobile.data.models.AuthRequest
import com.example.choiceiomobile.domain.models.User
import com.example.choiceiomobile.domain.repository.AuthRepository
import kotlin.contracts.Returns

class AuthRepositoryImpl : AuthRepository {
    private val api = ApiClient.authApi

    override suspend fun login(username: String, password: String): Result<User> {
        return try {
            val request = AuthRequest(name = username, password = password)
            val response = api.login(request)

            if (response.success) {
                val user = User(
                    userId = response.userId,
                    username = username
                )
                Result.success(user)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun register(username: String, password: String): Result<User> {
        return try {
            val request = AuthRequest(name = username, password = password)
            val response = api.register(request)

            if (response.success) {
                val user = User(
                    userId = response.userId,
                    username = username
                )
                Result.success(user)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
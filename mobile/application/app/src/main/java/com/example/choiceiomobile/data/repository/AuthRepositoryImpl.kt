package com.example.choiceiomobile.data.repository

import com.example.choiceiomobile.data.api.ApiClient
import com.example.choiceiomobile.data.models.AuthRequest
import com.example.choiceiomobile.domain.models.User
import com.example.choiceiomobile.domain.repository.AuthRepository
import kotlin.contracts.Returns

class AuthRepositoryImpl : AuthRepository {
    override suspend fun login(
        username: String,
        password: String
    ): Result<User> {
        return try {
            val request = AuthRequest(username, password)
            val response = ApiClient.authApi.login(request)

            if (response.success) {
                Result.success(
                    User(
                        username = username
                    )
                )
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception){
            Result.failure(e)
        }
    }

    override suspend fun register(
        username: String,
        password: String
    ): Result<User> {
        return try {
            val request = AuthRequest(username, password)
            val response = ApiClient.authApi.register(request)

            if(response.success) {
                Result.success(
                    User(
                        username = username
                    )
                )
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
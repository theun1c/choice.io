package com.example.choiceiomobile.domain.usecase

import com.example.choiceiomobile.domain.models.User
import com.example.choiceiomobile.domain.repository.AuthRepository

class LoginUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(username: String, password: String): Result<User> {
        if (username.isEmpty() || password.isEmpty()) {
            return Result.failure(Exception("empty username"))
        }

        if(password.length < 8) {
            return Result.failure(Exception("password must be > 8"))
        }

        return repository.login(username, password)
    }
}
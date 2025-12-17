package com.example.choiceiomobile.domain.usecase.auth

import com.example.choiceiomobile.domain.models.User
import com.example.choiceiomobile.domain.repository.AuthRepository

class RegisterUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(username: String, password: String): Result<User> {
        return repository.register(username, password)
    }
}
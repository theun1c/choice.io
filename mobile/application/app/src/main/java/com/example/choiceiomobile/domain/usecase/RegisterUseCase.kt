package com.example.choiceiomobile.domain.usecase

import com.example.choiceiomobile.domain.models.User
import com.example.choiceiomobile.domain.repository.AuthRepository

class RegisterUseCase (
    private val repository: AuthRepository
) {
    suspend operator fun invoke(username: String, password: String, confirmPassword: String): Result<User> {
        if (username.isEmpty()){
            return Result.failure(Exception("username is empty"))
        }

        if (password.length < 8){
            return Result.failure(Exception("password must be more then 8"))
        }

        if (password != confirmPassword){
            return Result.failure(Exception("passwords not confirmed"))
        }

        return repository.register(username, password)
    }
}
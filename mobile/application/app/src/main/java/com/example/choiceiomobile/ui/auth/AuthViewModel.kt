package com.example.choiceiomobile.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.choiceiomobile.data.repository.AuthRepositoryImpl
import com.example.choiceiomobile.domain.repository.AuthRepository
import com.example.choiceiomobile.domain.usecase.LoginUseCase
import com.example.choiceiomobile.domain.usecase.RegisterUseCase
import com.example.choiceiomobile.ui.screens.auth.RegisterScreen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val repository = AuthRepositoryImpl()
    private val loginUseCase = LoginUseCase(repository)
    private val registerUseCase = RegisterUseCase(repository)

    private val _loginUsername = MutableStateFlow("")
    val loginUsername: StateFlow<String> = _loginUsername

    private val _loginPassword = MutableStateFlow("")
    val loginPassword: StateFlow<String> = _loginPassword

    private val _loginLoading = MutableStateFlow(false)
    val loginLoading: StateFlow<Boolean> = _loginLoading

    private val _loginError = MutableStateFlow("")
    val loginError: StateFlow<String> = _loginError


    private val _registerUsername = MutableStateFlow("")
    val registerUsername: StateFlow<String> = _registerUsername

    private val _registerPassword = MutableStateFlow("")
    val registerPassword: StateFlow<String> = _registerPassword

    private val _registerConfirmPassword = MutableStateFlow("")
    val registerConfirmPassword: StateFlow<String> = _registerConfirmPassword

    private val _registerLoading = MutableStateFlow(false )
    val registerLoading: StateFlow<Boolean> = _registerLoading

    private val _registerError = MutableStateFlow("")
    val registerError: StateFlow<String> = _registerError

    fun setLoginUsername(text: String){
        _loginUsername.value = text
        _loginError.value = ""
    }

    fun setLoginPassword(text: String){
        _loginPassword.value = text
        _loginError.value = ""
    }

    fun login(onSuccess: () -> Unit){
        _loginLoading.value = true
        _loginError.value = ""

        viewModelScope.launch {
            val result = loginUseCase(_loginUsername.value, _loginPassword.value)

            _loginLoading.value = false

            result.onSuccess {
                user ->
                _loginError.value = ""
                onSuccess()
            }.onFailure {
                error ->
                _loginError.value = error.message ?: "login error"
            }
        }
    }

    fun setRegisterUsername(text: String) {
        _registerUsername.value = text
        _registerError.value = ""
    }

    fun setRegisterPassword(text: String) {
        _registerPassword.value = text
        _registerError.value = ""
    }

    fun setRegisterConfirmPassword(text: String){
        _registerConfirmPassword.value = text
        _registerError.value = ""
    }

    fun register(onSuccess: () -> Unit) {
        _registerLoading.value = true
        _registerError.value = ""

        viewModelScope.launch {
            val result = registerUseCase(_registerUsername.value, _registerPassword.value, _registerConfirmPassword.value)

            _registerLoading.value = false

            result.onSuccess {
                user ->
                _registerError.value = ""
                onSuccess()
            }.onFailure {
                error ->
                _registerError.value = error.message ?: "register error"
            }
        }
    }
}
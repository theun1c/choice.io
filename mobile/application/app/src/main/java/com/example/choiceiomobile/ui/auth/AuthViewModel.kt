package com.example.choiceiomobile.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.choiceiomobile.data.repository.AuthRepositoryImpl
import com.example.choiceiomobile.domain.repository.AuthRepository
import com.example.choiceiomobile.domain.usecase.auth.LoginUseCase
import com.example.choiceiomobile.domain.usecase.auth.RegisterUseCase
import com.example.choiceiomobile.ui.screens.auth.RegisterScreen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val repository = AuthRepositoryImpl()
    private val loginUseCase = LoginUseCase(repository)
    private val registerUseCase = RegisterUseCase(repository)

    // Состояния для логина
    private val _loginUsername = MutableStateFlow("")
    val loginUsername: StateFlow<String> = _loginUsername.asStateFlow()

    private val _loginPassword = MutableStateFlow("")
    val loginPassword: StateFlow<String> = _loginPassword.asStateFlow()

    private val _loginLoading = MutableStateFlow(false)
    val loginLoading: StateFlow<Boolean> = _loginLoading.asStateFlow()

    private val _loginError = MutableStateFlow("")
    val loginError: StateFlow<String> = _loginError.asStateFlow()

    // Состояния для регистрации
    private val _registerUsername = MutableStateFlow("")
    val registerUsername: StateFlow<String> = _registerUsername.asStateFlow()

    private val _registerPassword = MutableStateFlow("")
    val registerPassword: StateFlow<String> = _registerPassword.asStateFlow()

    private val _registerConfirmPassword = MutableStateFlow("")
    val registerConfirmPassword: StateFlow<String> = _registerConfirmPassword.asStateFlow()

    private val _registerLoading = MutableStateFlow(false)
    val registerLoading: StateFlow<Boolean> = _registerLoading.asStateFlow()

    private val _registerError = MutableStateFlow("")
    val registerError: StateFlow<String> = _registerError.asStateFlow()

    // НОВОЕ: Состояния пользователя
    private val _currentUser = MutableStateFlow<com.example.choiceiomobile.domain.models.User?>(null)
    val currentUser: StateFlow<com.example.choiceiomobile.domain.models.User?> = _currentUser.asStateFlow()

    private val _userId = MutableStateFlow<Int?>(null)
    val userId: StateFlow<Int?> = _userId.asStateFlow()

    private val _username = MutableStateFlow<String?>(null)
    val username: StateFlow<String?> = _username.asStateFlow()

    // Методы для логина
    fun setLoginUsername(text: String) {
        _loginUsername.value = text
        _loginError.value = ""
    }

    fun setLoginPassword(text: String) {
        _loginPassword.value = text
        _loginError.value = ""
    }

    fun login(onSuccess: () -> Unit) {
        _loginLoading.value = true
        _loginError.value = ""

        viewModelScope.launch {
            val result = loginUseCase(_loginUsername.value, _loginPassword.value)

            _loginLoading.value = false

            result.onSuccess { user ->
                // Сохраняем данные пользователя
                _currentUser.value = user
                _userId.value = user.userId
                _username.value = user.username

                _loginError.value = ""
                onSuccess()
            }.onFailure { error ->
                _loginError.value = error.message ?: "Login error"
            }
        }
    }

    // Методы для регистрации
    fun setRegisterUsername(text: String) {
        _registerUsername.value = text
        _registerError.value = ""
    }

    fun setRegisterPassword(text: String) {
        _registerPassword.value = text
        _registerError.value = ""
    }

    fun setRegisterConfirmPassword(text: String) {
        _registerConfirmPassword.value = text
        _registerError.value = ""
    }

    fun register(onSuccess: () -> Unit) {
        // Проверка совпадения паролей
        if (_registerPassword.value != _registerConfirmPassword.value) {
            _registerError.value = "Passwords do not match"
            return
        }

        _registerLoading.value = true
        _registerError.value = ""

        viewModelScope.launch {
            val result = registerUseCase(_registerUsername.value, _registerPassword.value)

            _registerLoading.value = false

            result.onSuccess { user ->
                // Сохраняем данные пользователя
                _currentUser.value = user
                _userId.value = user.userId
                _username.value = user.username

                _registerError.value = ""
                onSuccess()
            }.onFailure { error ->
                _registerError.value = error.message ?: "Registration error"
            }
        }
    }

    // НОВЫЕ методы: Выход и очистка данных
    fun logout() {
        _currentUser.value = null
        _userId.value = null
        _username.value = null
        _loginUsername.value = ""
        _loginPassword.value = ""
        _loginError.value = ""
    }

    // Получить текущего пользователя
    fun getCurrentUser(): com.example.choiceiomobile.domain.models.User? {
        return _currentUser.value
    }

    // Проверить, авторизован ли пользователь
    val isLoggedIn: Boolean
        get() = _currentUser.value != null
}
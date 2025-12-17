package com.example.choiceiomobile.ui.favourites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.choiceiomobile.domain.usecase.favourites.AddToFavouritesUseCase
import com.example.choiceiomobile.domain.usecase.favourites.GetFavouritesUseCase
import com.example.choiceiomobile.domain.usecase.favourites.RemoveFromFavouritesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FavouritesViewModel(
    private val getFavouritesUseCase: GetFavouritesUseCase,
    private val addToFavouritesUseCase: AddToFavouritesUseCase,
    private val removeFromFavouritesUseCase: RemoveFromFavouritesUseCase
) : ViewModel() {

    // Состояния
    private val _favouriteIds = MutableStateFlow<List<Int>>(emptyList())
    val favouriteIds: StateFlow<List<Int>> = _favouriteIds.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _operationMessage = MutableStateFlow<String?>(null)
    val operationMessage: StateFlow<String?> = _operationMessage.asStateFlow()

    private var currentUserId: Int? = null

    // Инициализация
    fun initialize(userId: Int) {
        currentUserId = userId
        loadFavourites()
    }

    // Загрузить избранное
    fun loadFavourites() {
        val userId = currentUserId ?: return

        _isLoading.value = true
        _error.value = null

        viewModelScope.launch {
            try {
                val favourites = getFavouritesUseCase(userId)
                _favouriteIds.value = favourites.animeIds
            } catch (e: Exception) {
                _error.value = "Ошибка загрузки: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Добавить в избранное
    suspend fun addFavourite(animeId: Int): Boolean {
        val userId = currentUserId ?: return false

        return try {
            val result = addToFavouritesUseCase(userId, animeId)

            if (result.success) {
                // Обновляем локальный список
                _favouriteIds.update { currentList ->
                    if (animeId !in currentList) {
                        currentList + animeId
                    } else {
                        currentList
                    }
                }
                _operationMessage.value = result.message
                true
            } else {
                _operationMessage.value = result.message
                false
            }
        } catch (e: Exception) {
            _operationMessage.value = "Ошибка: ${e.message}"
            false
        }
    }

    // Удалить из избранного
    suspend fun removeFavourite(animeId: Int): Boolean {
        val userId = currentUserId ?: return false

        return try {
            val result = removeFromFavouritesUseCase(userId, animeId)

            if (result.success) {
                // Обновляем локальный список
                _favouriteIds.update { currentList ->
                    currentList.filter { it != animeId }
                }
                _operationMessage.value = result.message
                true
            } else {
                _operationMessage.value = result.message
                false
            }
        } catch (e: Exception) {
            _operationMessage.value = "Ошибка: ${e.message}"
            false
        }
    }

    // Проверить, находится ли в избранном
    fun isFavourite(animeId: Int): Boolean {
        return animeId in _favouriteIds.value
    }

    // Количество избранных
    val favouritesCount: Int
        get() = _favouriteIds.value.size

    // Очистить сообщение об операции
    fun clearOperationMessage() {
        _operationMessage.value = null
    }
}
package com.example.choiceiomobile.ui.favourites

import com.example.choiceiomobile.domain.repository.FavouritesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Простой менеджер избранного как синглтон
object FavouritesManager {
    private val _favouriteIds = MutableStateFlow<Set<Int>>(emptySet())
    val favouriteIds: StateFlow<Set<Int>> = _favouriteIds.asStateFlow()

    private val coroutineScope: CoroutineScope
        get() = CoroutineScope(Dispatchers.IO)

    // Добавь эти свойства
    private var currentUserId: Int? = null
    private var repository: FavouritesRepository? = null

    // Инициализация с репозиторием
    fun initialize(
        userId: Int,
        repository: FavouritesRepository
    ) {
        this.currentUserId = userId
        this.repository = repository
        loadFromServer()
    }

    // Загрузить избранное с сервера
    private fun loadFromServer() {
        val userId = currentUserId ?: return
        val repo = repository ?: return

        coroutineScope.launch {
            try {
                val serverFavourites = repo.getFavourites(userId)
                _favouriteIds.value = _favouriteIds.value + serverFavourites.animeIds.toSet()
                println("Загружено избранное с сервера: ${serverFavourites.animeIds}")
            } catch (e: Exception) {
                println("Ошибка загрузки избранного: ${e.message}")
            }
        }
    }

    // Добавить в избранное (и локально, и на сервер)
    fun addFavourite(animeId: Int) {
        println("Добавляем в избранное ID: $animeId")

        // Добавляем локально
        _favouriteIds.value = _favouriteIds.value + animeId

        // Сохраняем на сервер
        saveToServer(animeId, add = true)
    }

    // Удалить из избранного
    fun removeFavourite(animeId: Int) {
        _favouriteIds.value = _favouriteIds.value - animeId

        // Удаляем с сервера
        saveToServer(animeId, add = false)
    }

    private fun saveToServer(animeId: Int, add: Boolean) {
        val userId = currentUserId ?: return
        val repo = repository ?: return

        coroutineScope.launch {
            try {
                if (add) {
                    val result = repo.addToFavourites(userId, animeId)
                    println("Сервер: Добавлено в избранное - успех: ${result.success}, сообщение: ${result.message}")
                } else {
                    val result = repo.removeFromFavourites(userId, animeId)
                    println("Сервер: Удалено из избранного - успех: ${result.success}, сообщение: ${result.message}")
                }
            } catch (e: Exception) {
                println("Ошибка синхронизации с сервером: ${e.message}")
            }
        }
    }

    fun isFavourite(animeId: Int): Boolean {
        return animeId in _favouriteIds.value
    }

    fun getFavourites(): List<Int> {
        return _favouriteIds.value.toList()
    }

    // Очистка при выходе
    fun clear() {
        currentUserId = null
        repository = null
        _favouriteIds.value = emptySet()
    }
}
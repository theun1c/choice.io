package com.example.choiceiomobile.ui.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.choiceiomobile.data.repository.AnimeRepositoryImpl
import com.example.choiceiomobile.domain.models.Anime
import com.example.choiceiomobile.domain.usecase.GetAnimeFeedUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AnimeFeedViewModel : ViewModel() {
    private val repository = AnimeRepositoryImpl()
    private val getAnimeFeedUseCase = GetAnimeFeedUseCase(repository)

    private val _animeList = MutableStateFlow<List<AnimeUiModel>>(emptyList())
    val animeList: StateFlow<List<AnimeUiModel>> = _animeList.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isLoadingMore = MutableStateFlow(false)
    val isLoadingMore: StateFlow<Boolean> = _isLoadingMore.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _canLoadMore = MutableStateFlow(true)
    val canLoadMore: StateFlow<Boolean> = _canLoadMore.asStateFlow()

    private var currentPage = 0
    private var currentMood: String? = null

    // Для отслеживания уникальных ID
    private val loadedAnimeIds = mutableSetOf<Int>()

    fun setMood(mood: String) {
        if (currentMood != mood) {
            println("🎭 Смена настроения: $mood")
            currentMood = mood
            currentPage = 0
            loadedAnimeIds.clear()
            _animeList.value = emptyList()
            _canLoadMore.value = true
            _error.value = null
            loadInitialAnime()
        }
    }

    private fun filterValidAnime(animeList: List<Anime>): List<Anime> {
        return animeList.filter { anime ->
            // Фильтруем аниме без названия или с пустым названием
            anime.title.isNotBlank() &&
                    anime.title.trim().isNotEmpty() &&
                    anime.title != "null" &&
                    anime.title != "Unknown"
        }.also { filtered ->
            if (animeList.size != filtered.size) {
                println("🗑️ Отфильтровано ${animeList.size - filtered.size} аниме без названия")
            }
        }
    }

    private fun loadInitialAnime() {
        val mood = currentMood ?: return

        _isLoading.value = true
        _error.value = null

        viewModelScope.launch {
            try {
                println("⏳ Начальная загрузка для настроения: $mood")
                val result = getAnimeFeedUseCase(mood)

                if (result.isSuccess) {
                    val anime = result.getOrThrow()
                    val filteredAnime = filterValidAnime(anime)

                    if (filteredAnime.isEmpty()) {
                        _error.value = "Нет доступных аниме с названиями"
                    } else {
                        val uiModels = filteredAnime.map { it.toUiModel() }

                        // Сохраняем ID
                        uiModels.forEach { loadedAnimeIds.add(it.id) }

                        _animeList.value = uiModels
                        currentPage = 0

                        // Если загрузили меньше 20, возможно больше нет
                        _canLoadMore.value = uiModels.size >= 20

                        println("✅ Загружено ${uiModels.size} аниме. Можно грузить еще: ${_canLoadMore.value}")
                    }
                } else {
                    _error.value = result.exceptionOrNull()?.message ?: "Ошибка загрузки"
                }
            } catch (e: Exception) {
                _error.value = "Ошибка: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadMoreAnime() {
        val mood = currentMood ?: return

        if (_isLoadingMore.value || !_canLoadMore.value) {
            println("⏸️ Пропускаем загрузку: isLoadingMore=${_isLoadingMore.value}, canLoadMore=${_canLoadMore.value}")
            return
        }

        _isLoadingMore.value = true
        _error.value = null

        viewModelScope.launch {
            try {
                val nextPage = currentPage + 1
                println("🔄 Загрузка страницы $nextPage для $mood")

                val result = getAnimeFeedUseCase(mood, nextPage)

                if (result.isSuccess) {
                    val newAnime = result.getOrThrow()

                    if (newAnime.isEmpty()) {
                        println("🏁 Достигнут конец - пустой список")
                        _canLoadMore.value = false
                    } else {
                        // Фильтруем дубликаты
                        val newUiModels = newAnime
                            .map { it.toUiModel() }
                            .filter { it.id !in loadedAnimeIds }

                        if (newUiModels.isNotEmpty()) {
                            // Добавляем новые ID
                            newUiModels.forEach { loadedAnimeIds.add(it.id) }

                            // Добавляем в список
                            _animeList.update { currentList ->
                                currentList + newUiModels
                            }
                            currentPage = nextPage

                            // Проверяем, можно ли грузить дальше
                            _canLoadMore.value = newUiModels.size >= 20

                            println("✅ Добавлено ${newUiModels.size} новых аниме. Всего: ${_animeList.value.size}")
                        } else {
                            // Если все дубликаты, пробуем следующую страницу
                            currentPage = nextPage
                            _canLoadMore.value = false
                            println("⚠️ Все аниме на странице $nextPage уже были загружены")
                        }
                    }
                } else {
                    println("❌ Ошибка API при загрузке страницы $nextPage")
                    _canLoadMore.value = false
                }
            } catch (e: Exception) {
                println("💥 Исключение: ${e.message}")
                _canLoadMore.value = false
            } finally {
                _isLoadingMore.value = false
                println("🏁 Загрузка завершена. canLoadMore=${_canLoadMore.value}")
            }
        }
    }

    fun refresh() {
        currentMood?.let { mood ->
            println("🔄 Принудительное обновление для $mood")
            currentPage = 0
            loadedAnimeIds.clear()
            _animeList.value = emptyList()
            _canLoadMore.value = true
            loadInitialAnime()
        }
    }
}

data class AnimeUiModel(
    val id: Int,
    val title: String,
    val englishTitle: String?,
    val score: String,
    val shortSynopsis: String,
    val imageUrl: String?,
    val fullSynopsis: String?
)

private fun Anime.toUiModel(): AnimeUiModel {
    val shortSynopsis = this.synopsis?.take(150) ?: "No description"
    val formattedScore = this.score?.toString() ?: "N/A"

    return AnimeUiModel(
        id = this.id,
        title = this.title,
        englishTitle = this.englishTitle,
        score = formattedScore,
        shortSynopsis = shortSynopsis,
        imageUrl = this.imageUrl,
        fullSynopsis = this.synopsis
    )
}
package com.example.choiceiomobile.ui.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.choiceiomobile.data.repository.AnimeRepositoryImpl
import com.example.choiceiomobile.domain.models.Anime
import com.example.choiceiomobile.domain.repository.AnimeRepository
import com.example.choiceiomobile.domain.usecase.GetAnimeFeedUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AnimeFeedViewModel : ViewModel() {
    private val repository = AnimeRepositoryImpl()
    private val getAnimeFeedUseCase = GetAnimeFeedUseCase(repository)

    private val _animeList = MutableStateFlow<List<AnimeUiModel>>(emptyList())
    val animeList: StateFlow<List<AnimeUiModel>> = _animeList

    private  val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private var currentMood: String? = null

    fun setMood(mood: String){
        currentMood = mood
        loadAnimeFeed()
    }

    private fun loadAnimeFeed(){
        val mood = currentMood ?: return

        _isLoading.value = true
        _error.value = null

        viewModelScope.launch {
            val result = getAnimeFeedUseCase(mood)

            _isLoading.value = false

            result.onSuccess { anime ->
                _animeList.value = anime.map { it.toUiModel() }
            }.onFailure { e ->
                _error.value = "${e.message}"
            }
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
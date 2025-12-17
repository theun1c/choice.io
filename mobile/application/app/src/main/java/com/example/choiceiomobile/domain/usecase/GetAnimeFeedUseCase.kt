package com.example.choiceiomobile.domain.usecase

import com.example.choiceiomobile.data.repository.AnimeRepositoryImpl
import com.example.choiceiomobile.domain.models.Anime
import com.example.choiceiomobile.domain.repository.AnimeRepository
import javax.inject.Inject

class GetAnimeFeedUseCase @Inject constructor(
    private val repository: AnimeRepository
) {
    // Для начальной загрузки
    suspend operator fun invoke(mood: String): Result<List<Anime>> {
        return repository.getFeedByMood(mood, limit = 20)
    }

    suspend operator fun invoke(mood: String, page: Int): Result<List<Anime>> {
        return (repository as? AnimeRepositoryImpl)?.getFeedByMoodPage(mood, page, 20)
            ?: repository.getFeedByMood(mood, limit = 20)
    }
}
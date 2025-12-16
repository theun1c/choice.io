package com.example.choiceiomobile.domain.usecase

import com.example.choiceiomobile.domain.models.Anime
import com.example.choiceiomobile.domain.repository.AnimeRepository
import javax.inject.Inject

class GetAnimeFeedUseCase @Inject constructor(
    private val repository: AnimeRepository
) {
    suspend operator fun invoke(mood: String): Result<List<Anime>> {
        return repository.getFeedByMood(mood, limit = 20)
    }
}
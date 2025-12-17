package com.example.choiceiomobile.domain.repository

import com.example.choiceiomobile.domain.models.Anime

interface AnimeRepository {
    suspend fun getFeedByMood(mood: String, limit: Int = 20): Result<List<Anime>>
    suspend fun getFeedByMoodWithOffset(mood: String, limit: Int = 20, offset: Int = 0): Result<List<Anime>>
}
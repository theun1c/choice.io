package com.example.choiceiomobile.data.repository

import com.example.choiceiomobile.data.api.ApiClient
import com.example.choiceiomobile.data.models.AnimeDto
import com.example.choiceiomobile.domain.models.Anime
import com.example.choiceiomobile.domain.repository.AnimeRepository

class AnimeRepositoryImpl : AnimeRepository {
    override suspend fun getFeedByMood(
        mood: String,
        limit: Int
    ): Result<List<Anime>> {
        return try {
            val response = ApiClient.animeApi.getAnimeFeed(mood, limit)
            if(response.success){
                val animeList = response.anime.map { it.toDomain() }
                Result.success(animeList)
            } else {
                Result.failure(Exception("API request failed"))
            }
        } catch (e: Exception){
            Result.failure(e)
        }
    }
}

private fun AnimeDto.toDomain(): Anime {
    return Anime(
        id = this.id,
        title = this.title,
        englishTitle = this.titleEnglish,
        score = this.score,
        synopsis = this.synopsis,
        imageUrl = this.imageUrl
    )
}
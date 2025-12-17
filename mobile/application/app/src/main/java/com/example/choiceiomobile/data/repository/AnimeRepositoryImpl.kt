package com.example.choiceiomobile.data.repository

import com.example.choiceiomobile.data.api.ApiClient
import com.example.choiceiomobile.data.models.AnimeDto
import com.example.choiceiomobile.domain.models.Anime
import com.example.choiceiomobile.domain.repository.AnimeRepository
import kotlinx.coroutines.delay

class AnimeRepositoryImpl : AnimeRepository {

    // Для хранения уже полученных аниме по настроениям
    private val cachedAnimeByMood = mutableMapOf<String, MutableList<Anime>>()

    override suspend fun getFeedByMood(mood: String, limit: Int): Result<List<Anime>> {
        return try {
            println("Запрос аниме для настроения: $mood, limit: $limit")

            val response = ApiClient.animeApi.getAnimeFeed(mood, limit)

            response.anime.firstOrNull()?.let { anime ->
                println("Первая картинка URL: ${anime.imageUrl}")
            }

            if (response.success) {
                val animeList = response.anime.map { it.toDomain() }

                // Кэшируем результат
                cachedAnimeByMood[mood] = animeList.toMutableList()

                println("Загружено ${animeList.size} аниме для настроения: $mood")
                Result.success(animeList)
            } else {
                Result.failure(Exception("API request failed"))
            }
        } catch (e: Exception) {
            println("Ошибка при загрузке аниме: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun getFeedByMoodWithOffset(mood: String, limit: Int, offset: Int): Result<List<Anime>> {
        return try {
            println("Запрос аниме с offset: mood=$mood, limit=$limit, offset=$offset")

            // Если у API нет поддержки offset, делаем запрос и берем нужную часть
            val totalLimit = offset + limit
            val response = ApiClient.animeApi.getAnimeFeed(mood, totalLimit)

            if (response.success) {
                val allAnime = response.anime.map { it.toDomain() }

                // Берем только нужную часть (симулируем пагинацию)
                val resultAnime = if (offset < allAnime.size) {
                    allAnime.subList(offset, minOf(offset + limit, allAnime.size))
                } else {
                    emptyList()
                }

                // Обновляем кэш
                val cachedList = cachedAnimeByMood.getOrPut(mood) { mutableListOf() }
                resultAnime.forEach { newAnime ->
                    if (!cachedList.any { it.id == newAnime.id }) {
                        cachedList.add(newAnime)
                    }
                }

                println("Загружено ${resultAnime.size} новых аниме (offset=$offset)")
                Result.success(resultAnime)
            } else {
                Result.failure(Exception("API request failed"))
            }
        } catch (e: Exception) {
            println("Ошибка при загрузке аниме с offset: ${e.message}")
            Result.failure(e)
        }
    }

    // Альтернативный подход: симулируем разные запросы для разных страниц
    suspend fun getFeedByMoodPage(mood: String, page: Int, limit: Int = 20): Result<List<Anime>> {
        return try {
            println("Запрос страницы $page для настроения: $mood")

            // В реальности здесь должен быть вызов API с параметром страницы
            // Но если API не поддерживает, можно использовать offset
            val offset = page * limit
            return getFeedByMoodWithOffset(mood, limit, offset)
        } catch (e: Exception) {
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
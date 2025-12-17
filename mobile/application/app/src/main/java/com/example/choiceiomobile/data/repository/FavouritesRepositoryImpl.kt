package com.example.choiceiomobile.data.repository

import com.example.choiceiomobile.data.api.ApiClient
import com.example.choiceiomobile.data.models.FavouriteRequestDto
import com.example.choiceiomobile.domain.models.FavouriteOperation
import com.example.choiceiomobile.domain.models.UserFavourites
import com.example.choiceiomobile.domain.repository.FavouritesRepository
import javax.inject.Inject

class FavouritesRepositoryImpl @Inject constructor() : FavouritesRepository {
    private val api = ApiClient.favouritesApi

    override suspend fun getFavourites(userId: Int): UserFavourites {
        val response = api.getFavourites(userId)
        return UserFavourites(
            userId = response.userId,
            animeIds = response.animeIds,
            count = response.count
        )
    }

    override suspend fun addToFavourites(userId: Int, animeId: Int): FavouriteOperation {
        val request = FavouriteRequestDto(animeId = animeId, userId = userId)
        val response = api.addToFavourites(request)

        return FavouriteOperation(
            success = response.success,
            message = response.message,
            animeId = response.animeId,
            userId = response.userId
        )
    }

    override suspend fun removeFromFavourites(userId: Int, animeId: Int): FavouriteOperation {
        val request = FavouriteRequestDto(animeId = animeId, userId = userId)
        val response = api.removeFromFavourites(request)

        return FavouriteOperation(
            success = response.success,
            message = response.message,
            animeId = response.animeId,
            userId = response.userId
        )
    }

    override suspend fun isFavourite(userId: Int, animeId: Int): Boolean {
        return try {
            val favourites = getFavourites(userId)
            animeId in favourites.animeIds
        } catch (e: Exception) {
            false
        }
    }
}

object FavouritesRepositoryProvider {
    val repository by lazy { FavouritesRepositoryImpl() }
}
package com.example.choiceiomobile.domain.repository

import com.example.choiceiomobile.domain.models.FavouriteOperation
import com.example.choiceiomobile.domain.models.UserFavourites

interface FavouritesRepository {
    suspend fun getFavourites(userId: Int): UserFavourites
    suspend fun addToFavourites(userId: Int, animeId: Int): FavouriteOperation
    suspend fun removeFromFavourites(userId: Int, animeId: Int): FavouriteOperation
    suspend fun isFavourite(userId: Int, animeId: Int): Boolean
}
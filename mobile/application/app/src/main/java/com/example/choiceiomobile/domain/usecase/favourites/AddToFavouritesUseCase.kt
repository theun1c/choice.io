package com.example.choiceiomobile.domain.usecase.favourites

import com.example.choiceiomobile.domain.models.FavouriteOperation
import com.example.choiceiomobile.domain.repository.FavouritesRepository

class AddToFavouritesUseCase(
    private val repository: FavouritesRepository
) {
    suspend operator fun invoke(userId: Int, animeId: Int): FavouriteOperation {
        return repository.addToFavourites(userId, animeId)
    }
}
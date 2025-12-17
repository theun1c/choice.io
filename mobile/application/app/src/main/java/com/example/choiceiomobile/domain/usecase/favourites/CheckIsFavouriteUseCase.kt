package com.example.choiceiomobile.domain.usecase.favourites

import com.example.choiceiomobile.domain.repository.FavouritesRepository

class CheckIsFavouriteUseCase(
    private val repository: FavouritesRepository
) {
    suspend operator fun invoke(userId: Int, animeId: Int): Boolean {
        return repository.isFavourite(userId, animeId)
    }
}
package com.example.choiceiomobile.domain.usecase.favourites

import com.example.choiceiomobile.domain.models.UserFavourites
import com.example.choiceiomobile.domain.repository.FavouritesRepository

class GetFavouritesUseCase(
    private val repository: FavouritesRepository
) {
    suspend operator fun invoke(userId: Int): UserFavourites {
        return repository.getFavourites(userId)
    }
}
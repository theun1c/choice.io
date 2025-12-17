package com.example.choiceiomobile.domain.models

data class FavouriteOperation(
    val success: Boolean,
    val message: String,
    val animeId: Int,
    val userId: Int
)

data class UserFavourites(
    val userId: Int,
    val animeIds: List<Int>,
    val count: Int
)
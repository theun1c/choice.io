package com.example.choiceiomobile.data.models

// Для POST и DELETE запросов
data class FavouriteOperationResponseDto(
    val success: Boolean,
    val message: String,
    val animeId: Int,
    val userId: Int
)

// Для GET запроса
data class FavouritesListResponseDto(
    val success: Boolean,
    val userId: Int,
    val count: Int,
    val animeIds: List<Int>
)

// Для POST и DELETE запросов (тело запроса)
data class FavouriteRequestDto(
    val animeId: Int,
    val userId: Int
)
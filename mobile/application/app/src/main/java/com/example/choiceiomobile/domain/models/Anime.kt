package com.example.choiceiomobile.domain.models

data class Anime(
    val id: Int,
    val title: String,
    val englishTitle: String?,
    val score: Double?,
    val synopsis: String?,
    val imageUrl: String?
)
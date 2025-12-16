package com.example.choiceiomobile.data.models

import com.google.gson.annotations.SerializedName

data class FeedResponse(
    val success: Boolean,
    val mood: String,
    val count: Int,
    val anime: List<AnimeDto>
)

data class AnimeDto(
    val id: Int,
    val malId: Int,
    val title: String,
    @SerializedName("titleEnglish") val titleEnglish: String?,
    val type: String?,
    val episodes: Int?,
    val score: Double?,
    val synopsis: String?,
    val year: Int?,
    @SerializedName("largeImageUrl") val imageUrl: String?
)
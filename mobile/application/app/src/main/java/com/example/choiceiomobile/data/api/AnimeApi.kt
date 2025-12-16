package com.example.choiceiomobile.data.api

import com.example.choiceiomobile.data.models.FeedResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface AnimeApi {
    @GET("/api/feed")
    suspend fun getAnimeFeed(
        @Query("mood") mood: String,
        @Query("limit") limit: Int = 20
    ): FeedResponse
}
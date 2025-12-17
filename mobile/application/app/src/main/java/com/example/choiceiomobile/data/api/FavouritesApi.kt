package com.example.choiceiomobile.data.api

import com.example.choiceiomobile.data.models.FavouriteOperationResponseDto
import com.example.choiceiomobile.data.models.FavouriteRequestDto
import com.example.choiceiomobile.data.models.FavouritesListResponseDto
import okhttp3.Call
import okhttp3.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface FavouritesApi {
    @GET("api/Favourites")
    suspend fun getFavourites(@Query("userId") userId: Int): FavouritesListResponseDto

    @POST("api/Favourites")
    suspend fun addToFavourites(@Body request: FavouriteRequestDto): FavouriteOperationResponseDto

    @DELETE("api/Favourites")
    suspend fun removeFromFavourites(@Body request: FavouriteRequestDto): FavouriteOperationResponseDto
}
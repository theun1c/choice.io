package com.example.choiceiomobile.data.api

object ApiClient {
    private const val BASE_URL = "https://api-service-choice-io.onrender.com"

    private val retrofit = retrofit2.Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create())
        .build()

    val authApi: AuthApi by lazy {
        retrofit.create(AuthApi::class.java)
    }

    val animeApi: AnimeApi by lazy {
        retrofit.create(AnimeApi::class.java)
    }
}
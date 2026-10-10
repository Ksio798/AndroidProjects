package com.example.starships.data

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface SwapiApi {

    @GET("starships/")
    suspend fun getStarships(
        @Query("page") page: Int = 1,
        @Query("search") search: String? = null
    ): StarshipsResponse

    @GET("starships/{id}/")
    suspend fun getStarship(
        @Path("id") id: String
    ): Starship
}
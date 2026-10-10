package com.example.starships.data

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StarshipsRepository @Inject constructor(
    private val api: SwapiApi
) {

    suspend fun getStarships(
        page: Int = 1,
        search: String = ""
    ): StarshipsResponse {
        val query = search.trim().takeIf { it.isNotEmpty() }

        return api.getStarships(
            page = page,
            search = query
        )
    }

    suspend fun getStarship(id: String): Starship {
        return api.getStarship(id)
    }
}
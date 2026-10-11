package com.example.starships

import com.example.starships.data.Starship
import com.example.starships.data.StarshipsResponse
import com.example.starships.data.SwapiApi

data class ListRequest(
    val page: Int,
    val search: String?
)

class FakeSwapiApi : SwapiApi {

    val listRequests = mutableListOf<ListRequest>()
    val detailRequests = mutableListOf<String>()

    var listHandler: suspend (Int, String?) -> StarshipsResponse =
        { _, _ -> testResponse() }

    var detailHandler: suspend (String) -> Starship =
        { id -> testStarship(id = id) }

    override suspend fun getStarships(
        page: Int,
        search: String?
    ): StarshipsResponse {
        listRequests += ListRequest(
            page = page,
            search = search
        )

        return listHandler(page, search)
    }

    override suspend fun getStarship(id: String): Starship {
        detailRequests += id
        return detailHandler(id)
    }
}
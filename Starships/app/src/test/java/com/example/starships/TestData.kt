package com.example.starships

import com.example.starships.data.Starship
import com.example.starships.data.StarshipsResponse

fun testStarship(
    id: String = "10",
    name: String = "Millennium Falcon"
): Starship {
    return Starship(
        name = name,
        model = "YT-1300 light freighter",
        manufacturer = "Corellian Engineering Corporation",
        starshipClass = "Light freighter",
        costInCredits = "100000",
        length = "34.37",
        crew = "4",
        passengers = "6",
        maxAtmospheringSpeed = "1050",
        hyperdriveRating = "0.5",
        mglt = "75",
        cargoCapacity = "100000",
        consumables = "2 months",
        url = "https://swapi.dev/api/starships/$id/"
    )
}

fun testResponse(
    ships: List<Starship> = listOf(testStarship()),
    next: String? = null,
    previous: String? = null
): StarshipsResponse {
    return StarshipsResponse(
        count = ships.size,
        next = next,
        previous = previous,
        results = ships
    )
}
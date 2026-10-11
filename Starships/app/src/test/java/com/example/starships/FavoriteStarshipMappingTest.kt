package com.example.starships

import com.example.starships.data.local.FavoriteStarshipEntity
import com.example.starships.data.local.toFavoriteEntity
import org.junit.Assert.assertEquals
import org.junit.Test

class FavoriteStarshipMappingTest {

    @Test
    fun starshipIsConvertedToCorrectRoomEntity() {
        val ship = testStarship(
            id = "10",
            name = "Millennium Falcon"
        )

        val entity = ship.toFavoriteEntity()

        assertEquals(
            FavoriteStarshipEntity(
                id = "10",
                name = "Millennium Falcon",
                model = "YT-1300 light freighter",
                manufacturer = "Corellian Engineering Corporation",
                starshipClass = "Light freighter"
            ),
            entity
        )
    }
}
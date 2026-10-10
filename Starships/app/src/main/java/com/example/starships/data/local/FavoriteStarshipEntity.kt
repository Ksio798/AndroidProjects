package com.example.starships.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.starships.data.Starship

@Entity(tableName = "favorite_starships")
data class FavoriteStarshipEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val model: String,
    val manufacturer: String,
    val starshipClass: String
)

fun Starship.toFavoriteEntity(): FavoriteStarshipEntity {
    return FavoriteStarshipEntity(
        id = id,
        name = name,
        model = model,
        manufacturer = manufacturer,
        starshipClass = starshipClass
    )
}
package com.example.starships.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
abstract class FavoritesDao {

    @Query(
        "SELECT * FROM favorite_starships ORDER BY name COLLATE NOCASE"
    )
    abstract fun observeFavorites(): Flow<List<FavoriteStarshipEntity>>

    @Query(
        "SELECT EXISTS(SELECT 1 FROM favorite_starships WHERE id = :id)"
    )
    abstract suspend fun isFavorite(id: String): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(ship: FavoriteStarshipEntity)

    @Query("DELETE FROM favorite_starships WHERE id = :id")
    abstract suspend fun deleteById(id: String)

    @Transaction
    open suspend fun toggle(ship: FavoriteStarshipEntity) {
        if (isFavorite(ship.id)) {
            deleteById(ship.id)
        } else {
            insert(ship)
        }
    }
}
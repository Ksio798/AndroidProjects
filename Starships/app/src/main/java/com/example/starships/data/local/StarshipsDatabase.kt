package com.example.starships.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [FavoriteStarshipEntity::class],
    version = 1,
    exportSchema = true
)
abstract class StarshipsDatabase : RoomDatabase() {

    abstract fun favoritesDao(): FavoritesDao
}
package com.example.starships.data

import com.example.starships.data.local.FavoriteStarshipEntity
import com.example.starships.data.local.FavoritesDao
import com.example.starships.data.local.toFavoriteEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavoritesRepository @Inject constructor(
    private val dao: FavoritesDao
) {

    fun observeFavorites(): Flow<List<FavoriteStarshipEntity>> {
        return dao.observeFavorites()
    }

    suspend fun toggleFavorite(ship: Starship) {
        dao.toggle(ship.toFavoriteEntity())
    }

    suspend fun removeFavorite(id: String) {
        dao.deleteById(id)
    }
}
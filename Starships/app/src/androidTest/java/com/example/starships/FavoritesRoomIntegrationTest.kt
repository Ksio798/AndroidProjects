package com.example.starships

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.starships.data.FavoritesRepository
import com.example.starships.data.local.StarshipsDatabase
import com.example.starships.data.local.toFavoriteEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FavoritesRoomIntegrationTest {

    @Test
    fun repositoryAddsReadsAndRemovesFavoriteInRoom() = runBlocking {
        val context = ApplicationProvider
            .getApplicationContext<Context>()

        val database = Room.inMemoryDatabaseBuilder(
            context,
            StarshipsDatabase::class.java
        ).build()

        try {
            val repository = FavoritesRepository(
                database.favoritesDao()
            )

            val ship = testStarship()

            val initial = withTimeout(5_000) {
                repository.observeFavorites().first()
            }

            assertTrue(initial.isEmpty())

            repository.toggleFavorite(ship)

            val saved = withTimeout(5_000) {
                repository.observeFavorites().first()
            }

            assertEquals(
                listOf(ship.toFavoriteEntity()),
                saved
            )

            // Повторное нажатие должно удалить корабль,
            // поскольку toggle переключает состояние избранного.
            repository.toggleFavorite(ship)

            val afterToggle = withTimeout(5_000) {
                repository.observeFavorites().first()
            }

            assertTrue(afterToggle.isEmpty())

            // Проверяем также отдельную операцию удаления из списка.
            repository.toggleFavorite(ship)
            repository.removeFavorite(ship.id)

            val afterRemove = withTimeout(5_000) {
                repository.observeFavorites().first()
            }

            assertTrue(afterRemove.isEmpty())
        } finally {
            database.close()
        }
    }

    @Test
    fun favoriteSurvivesDatabaseCloseAndReopen() = runBlocking {
        val context = ApplicationProvider
            .getApplicationContext<Context>()

        val databaseName = "favorites-persistence-test.db"

        // Используем отдельную тестовую базу.
        context.deleteDatabase(databaseName)

        try {
            val ship = testStarship()

            val firstDatabase = Room.databaseBuilder(
                context,
                StarshipsDatabase::class.java,
                databaseName
            ).build()

            try {
                val repository = FavoritesRepository(
                    firstDatabase.favoritesDao()
                )

                repository.toggleFavorite(ship)

                val saved = withTimeout(5_000) {
                    repository.observeFavorites().first()
                }

                assertEquals(
                    listOf(ship.toFavoriteEntity()),
                    saved
                )
            } finally {
                firstDatabase.close()
            }

            // Создаём новые экземпляры базы и Repository.
            val reopenedDatabase = Room.databaseBuilder(
                context,
                StarshipsDatabase::class.java,
                databaseName
            ).build()

            try {
                val reopenedRepository = FavoritesRepository(
                    reopenedDatabase.favoritesDao()
                )

                val restored = withTimeout(5_000) {
                    reopenedRepository.observeFavorites().first()
                }

                assertEquals(
                    listOf(ship.toFavoriteEntity()),
                    restored
                )
            } finally {
                reopenedDatabase.close()
            }
        } finally {
            context.deleteDatabase(databaseName)
        }
    }
}
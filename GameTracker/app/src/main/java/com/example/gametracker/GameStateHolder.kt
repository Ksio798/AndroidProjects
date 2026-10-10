package com.example.gametracker

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class GameStateHolder {

    var uiState by mutableStateOf(
        GameUiState(
            games = listOf(
                Game(
                    1,
                    "The Witcher 3: Wild Hunt",
                    "RPG",
                    2015,
                    GameStatus.DONE
                ),
                Game(
                    2,
                    "Red Dead Redemption 2",
                    "Приключения",
                    2018,
                    GameStatus.IN_PROGRESS
                ),
                Game(
                    3,
                    "Yakuza: Like a Dragon",
                    "RPG",
                    2020,
                    GameStatus.PLANNED
                ),
                Game(
                    4,
                    "Grand Theft Auto V",
                    "Экшен",
                    2013,
                    GameStatus.DONE
                ),
                Game(
                    5,
                    "Cyberpunk 2077",
                    "RPG",
                    2020,
                    GameStatus.IN_PROGRESS
                ),
                Game(
                    6,
                    "Hogwarts Legacy",
                    "Приключения",
                    2023,
                    GameStatus.PLANNED
                )
            )
        )
    )
        private set

    fun onSearchQueryChange(query: String) {
        uiState = uiState.copy(
            searchQuery = query
        )
    }

    fun onFilterChange(filter: GameFilter) {
        uiState = uiState.copy(
            selectedFilter = filter
        )
    }

    fun onNextStatus(gameId: Int) {
        val updatedGames = uiState.games.map { game ->
            if (game.id == gameId) {
                game.copy(
                    status = game.status.next()
                )
            } else {
                game
            }
        }

        uiState = uiState.copy(
            games = updatedGames
        )
    }
}

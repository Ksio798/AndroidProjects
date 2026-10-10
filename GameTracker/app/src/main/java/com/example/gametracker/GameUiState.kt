package com.example.gametracker

enum class GameFilter(val title: String) {
    ALL("Все"),
    PLANNED("Запланировано"),
    IN_PROGRESS("В процессе"),
    DONE("Пройдено")
}

data class GameUiState(
    val games: List<Game> = emptyList(),
    val searchQuery: String = "",
    val selectedFilter: GameFilter = GameFilter.ALL
) {
    val filteredGames: List<Game>
        get() = games.filter { game ->
            val matchesSearch = game.title.contains(
                searchQuery.trim(),
                ignoreCase = true
            )

            val matchesFilter = when (selectedFilter) {
                GameFilter.ALL -> true
                GameFilter.PLANNED ->
                    game.status == GameStatus.PLANNED
                GameFilter.IN_PROGRESS ->
                    game.status == GameStatus.IN_PROGRESS
                GameFilter.DONE ->
                    game.status == GameStatus.DONE
            }

            matchesSearch && matchesFilter
        }

    val totalCount: Int
        get() = games.size

    val inProgressCount: Int
        get() = games.count {
            it.status == GameStatus.IN_PROGRESS
        }

    val doneCount: Int
        get() = games.count {
            it.status == GameStatus.DONE
        }
}

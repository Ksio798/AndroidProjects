package com.example.gametracker

enum class GameStatus(val title: String) {
    PLANNED("Запланировано"),
    IN_PROGRESS("В процессе"),
    DONE("Пройдено");

    fun next(): GameStatus {
        return when (this) {
            PLANNED -> IN_PROGRESS
            IN_PROGRESS -> DONE
            DONE -> PLANNED
        }
    }
}

data class Game(
    val id: Int,
    val title: String,
    val genre: String,
    val year: Int,
    val status: GameStatus
)

package com.example.starships.uiTools

import com.example.starships.data.Starship

data class StarshipsListState(
    val query: String = "",
    val page: Int = 1,
    val hasNextPage: Boolean = false,
    val hasPreviousPage: Boolean = false,
    val content: UiState<List<Starship>> = UiState.Loading
)

sealed interface StarshipsListEvent {

    data class QueryChanged(
        val query: String
    ) : StarshipsListEvent

    data object Search : StarshipsListEvent

    data object Retry : StarshipsListEvent

    data object NextPage : StarshipsListEvent

    data object PreviousPage : StarshipsListEvent
}
package com.example.starships.uiTools

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.starships.data.AppContainer
import com.example.starships.data.StarshipsRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class StarshipsListViewModel(
    private val repository: StarshipsRepository = AppContainer.repository
) : ViewModel() {

    private val _uiState = MutableStateFlow(StarshipsListState())
    val uiState = _uiState.asStateFlow()

    private var loadJob: Job? = null

    private var activeQuery = ""
    private var requestedPage = 1

    init {
        loadPage(1)
    }

    fun onEvent(event: StarshipsListEvent) {
        when (event) {
            is StarshipsListEvent.QueryChanged -> {
                _uiState.update {
                    it.copy(query = event.query)
                }
            }

            StarshipsListEvent.Search -> {
                activeQuery = _uiState.value.query.trim()
                loadPage(1)
            }

            StarshipsListEvent.Retry -> {
                loadPage(requestedPage)
            }

            StarshipsListEvent.NextPage -> {
                val state = _uiState.value

                if (
                    state.content is UiState.Success &&
                    state.hasNextPage
                ) {
                    loadPage(state.page + 1)
                }
            }

            StarshipsListEvent.PreviousPage -> {
                val state = _uiState.value

                if (
                    state.content is UiState.Success &&
                    state.hasPreviousPage
                ) {
                    loadPage(state.page - 1)
                }
            }
        }
    }

    private fun loadPage(page: Int) {
        loadJob?.cancel()

        requestedPage = page
        val queryForRequest = activeQuery

        _uiState.update {
            it.copy(
                page = page,
                hasNextPage = false,
                hasPreviousPage = false,
                content = UiState.Loading
            )
        }

        loadJob = viewModelScope.launch {
            try {
                val response = repository.getStarships(
                    page = page,
                    search = queryForRequest
                )

                _uiState.update {
                    it.copy(
                        page = page,
                        hasNextPage = response.next != null,
                        hasPreviousPage = response.previous != null,
                        content = if (response.results.isEmpty()) {
                            UiState.Empty
                        } else {
                            UiState.Success(response.results)
                        }
                    )
                }
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                _uiState.update {
                    it.copy(
                        content = UiState.Error(
                            exception.toUserMessage()
                        )
                    )
                }
            }
        }
    }
}
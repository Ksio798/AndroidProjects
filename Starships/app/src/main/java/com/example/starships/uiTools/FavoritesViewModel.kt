package com.example.starships.uiTools

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.starships.data.FavoritesRepository
import com.example.starships.data.Starship
import com.example.starships.data.local.FavoriteStarshipEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FavoritesUiState(
    val content: UiState<List<FavoriteStarshipEntity>> = UiState.Loading,
    val isUpdating: Boolean = false,
    val actionError: String? = null
)

sealed interface FavoritesEvent {

    data class Toggle(
        val ship: Starship
    ) : FavoritesEvent

    data class Remove(
        val id: String
    ) : FavoritesEvent

    data object Retry : FavoritesEvent
}

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val repository: FavoritesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FavoritesUiState())
    val uiState = _uiState.asStateFlow()

    private var observeJob: Job? = null

    init {
        observeFavorites()
    }

    fun onEvent(event: FavoritesEvent) {
        when (event) {
            is FavoritesEvent.Toggle -> {
                updateFavorites {
                    repository.toggleFavorite(event.ship)
                }
            }

            is FavoritesEvent.Remove -> {
                updateFavorites {
                    repository.removeFavorite(event.id)
                }
            }

            FavoritesEvent.Retry -> observeFavorites()
        }
    }

    private fun observeFavorites() {
        observeJob?.cancel()

        _uiState.update {
            it.copy(content = UiState.Loading)
        }

        observeJob = viewModelScope.launch {
            try {
                repository.observeFavorites().collect { ships ->
                    _uiState.update {
                        it.copy(
                            content = if (ships.isEmpty()) {
                                UiState.Empty
                            } else {
                                UiState.Success(ships)
                            }
                        )
                    }
                }
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                _uiState.update {
                    it.copy(
                        content = UiState.Error(
                            "Не удалось прочитать избранное. Попробуйте ещё раз."
                        )
                    )
                }
            }
        }
    }

    private fun updateFavorites(
        operation: suspend () -> Unit
    ) {
        if (_uiState.value.isUpdating) return

        _uiState.update {
            it.copy(
                isUpdating = true,
                actionError = null
            )
        }

        viewModelScope.launch {
            try {
                operation()
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                _uiState.update {
                    it.copy(
                        actionError =
                            "Не удалось изменить избранное. Попробуйте ещё раз."
                    )
                }
            } finally {
                _uiState.update {
                    it.copy(isUpdating = false)
                }
            }
        }
    }
}
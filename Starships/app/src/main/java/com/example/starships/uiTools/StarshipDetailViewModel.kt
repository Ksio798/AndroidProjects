package com.example.starships.uiTools

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import com.example.starships.data.Starship
import com.example.starships.data.StarshipsRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface StarshipDetailEvent {
    data object Retry : StarshipDetailEvent
}

@HiltViewModel
class StarshipDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: StarshipsRepository
) : ViewModel() {

    private val shipId: String =
        checkNotNull(savedStateHandle.get<String>("id")) {
            "В маршрут подробностей не передан id корабля"
        }

    private val _uiState =
        MutableStateFlow<UiState<Starship>>(UiState.Loading)

    val uiState = _uiState.asStateFlow()

    private var loadJob: Job? = null

    init {
        loadShip()
    }

    fun onEvent(event: StarshipDetailEvent) {
        when (event) {
            StarshipDetailEvent.Retry -> loadShip()
        }
    }

    private fun loadShip() {
        loadJob?.cancel()
        _uiState.value = UiState.Loading

        loadJob = viewModelScope.launch {
            try {
                val ship = repository.getStarship(shipId)
                _uiState.value = UiState.Success(ship)
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                _uiState.value = UiState.Error(
                    exception.toUserMessage()
                )
            }
        }
    }
}
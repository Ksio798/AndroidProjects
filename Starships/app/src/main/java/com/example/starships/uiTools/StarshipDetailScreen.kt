package com.example.starships.uiTools

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.starships.data.Starship

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StarshipDetailScreen(
    state: UiState<Starship>,
    isFavorite: Boolean,
    canUpdateFavorite: Boolean,
    favoriteError: String?,
    onEvent: (StarshipDetailEvent) -> Unit,
    onToggleFavorite: (Starship) -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("О корабле") },
                navigationIcon = {
                    TextButton(onClick = onBack) {
                        Text("Назад")
                    }
                }
            )
        }
    ) { innerPadding ->
        when (state) {
            UiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is UiState.Error -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(state.message)

                    Spacer(Modifier.height(12.dp))

                    Button(
                        onClick = {
                            onEvent(StarshipDetailEvent.Retry)
                        }
                    ) {
                        Text("Повторить")
                    }
                }
            }

            UiState.Empty -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Корабль не найден.")
                }
            }

            is UiState.Success -> {
                val ship = state.data

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = ship.name,
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Button(
                        onClick = {
                            onToggleFavorite(ship)
                        },
                        enabled = canUpdateFavorite,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            if (isFavorite) {
                                "Удалить из избранного"
                            } else {
                                "Добавить в избранное"
                            }
                        )
                    }

                    favoriteError?.let { message ->
                        Text(
                            text = message,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                    DetailField("Модель", ship.model)
                    DetailField("Производитель", ship.manufacturer)
                    DetailField("Класс", ship.starshipClass)
                    DetailField(
                        "Стоимость",
                        ship.costInCredits,
                        "кредитов"
                    )
                    DetailField("Длина", ship.length, "м")
                    DetailField("Экипаж", ship.crew)
                    DetailField("Пассажиры", ship.passengers)
                    DetailField(
                        "Скорость в атмосфере",
                        ship.maxAtmospheringSpeed
                    )
                    DetailField(
                        "Рейтинг гипердвигателя",
                        ship.hyperdriveRating
                    )
                    DetailField("Скорость MGLT", ship.mglt)
                    DetailField(
                        "Грузоподъёмность",
                        ship.cargoCapacity,
                        "кг"
                    )
                    DetailField(
                        "Автономность",
                        ship.consumables
                    )
                }
            }
        }
    }
}

@Composable
private fun DetailField(
    label: String,
    value: String,
    unit: String = ""
) {
    val displayValue = when {
        value.isBlank() || value.equals("unknown", ignoreCase = true) ->
            "Нет данных"

        value.equals("n/a", ignoreCase = true) ->
            "Не применимо"

        unit.isNotBlank() ->
            "$value $unit"

        else -> value
    }

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = displayValue,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

package com.example.gametracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                val stateHolder = remember {
                    GameStateHolder()
                }

                GameScreen(
                    uiState = stateHolder.uiState,
                    onSearchChange =
                        stateHolder::onSearchQueryChange,
                    onFilterChange =
                        stateHolder::onFilterChange,
                    onNextStatus =
                        stateHolder::onNextStatus
                )
            }
        }
    }
}

@Composable
fun GameScreen(
    uiState: GameUiState,
    onSearchChange: (String) -> Unit,
    onFilterChange: (GameFilter) -> Unit,
    onNextStatus: (Int) -> Unit
) {
    Scaffold(
        topBar = {
            GameHeader()
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {

            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = onSearchChange,
                label = {
                    Text("Поиск игры")
                },
                placeholder = {
                    Text("Введите название...")
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            GameFilterRow(
                selectedFilter = uiState.selectedFilter,
                onFilterChange = onFilterChange
            )

            Spacer(modifier = Modifier.height(16.dp))

            GameStatistics(uiState = uiState)

            Spacer(modifier = Modifier.height(16.dp))

            if (uiState.filteredGames.isEmpty()) {

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Ничего не найдено",
                        style = MaterialTheme.typography.titleMedium
                    )
                }

            } else {

                LazyColumn(
                    verticalArrangement =
                        Arrangement.spacedBy(12.dp),
                    contentPadding =
                        PaddingValues(bottom = 16.dp)
                ) {
                    items(
                        items = uiState.filteredGames,
                        key = { it.id }
                    ) { game ->

                        GameCard(
                            game = game,
                            onNextStatus = {
                                onNextStatus(game.id)
                            }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameHeader() {
    TopAppBar(
        title = {
            Column {
                Text("Game Tracker")
                Text(
                    text = "Моя коллекция видеоигр",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    )
}

@Composable
fun GameFilterRow(
    selectedFilter: GameFilter,
    onFilterChange: (GameFilter) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        GameFilter.entries.forEach { filter ->

            FilterChip(
                selected = selectedFilter == filter,
                onClick = {
                    onFilterChange(filter)
                },
                label = {
                    Text(filter.title)
                }
            )
        }
    }
}

@Composable
fun GameStatistics(uiState: GameUiState) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatisticItem(
                value = uiState.totalCount,
                label = "Всего"
            )

            StatisticItem(
                value = uiState.inProgressCount,
                label = "В процессе"
            )

            StatisticItem(
                value = uiState.doneCount,
                label = "Пройдено"
            )
        }
    }
}

@Composable
fun StatisticItem(
    value: Int,
    label: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value.toString(),
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
fun GameCard(
    game: Game,
    onNextStatus: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = game.title,
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = "${game.genre} · ${game.year}",
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = "Статус: ${game.status.title}",
                style = MaterialTheme.typography.bodyMedium
            )

            Button(
                onClick = onNextStatus,
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Следующий статус")
            }
        }
    }
}

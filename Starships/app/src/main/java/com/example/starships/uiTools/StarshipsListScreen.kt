package com.example.starships.uiTools

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StarshipsListScreen(
    state: StarshipsListState,
    onEvent: (StarshipsListEvent) -> Unit,
    onShipClick: (String) -> Unit,
    onFavoritesClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Корабли Star Wars") },
                actions = {
                    TextButton(onClick = onFavoritesClick) {
                        Text("Избранное")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = state.query,
                onValueChange = {
                    onEvent(StarshipsListEvent.QueryChanged(it))
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Название или модель корабля") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Search
                ),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        onEvent(StarshipsListEvent.Search)
                    }
                )
            )

            Button(
                onClick = {
                    onEvent(StarshipsListEvent.Search)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Найти")
            }

            when (val content = state.content) {
                UiState.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is UiState.Error -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(content.message)

                        Spacer(Modifier.height(12.dp))

                        Button(
                            onClick = {
                                onEvent(StarshipsListEvent.Retry)
                            }
                        ) {
                            Text("Повторить")
                        }
                    }
                }

                UiState.Empty -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Корабли не найдены. Измените запрос.")
                    }
                }

                is UiState.Success -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(bottom = 12.dp)
                    ) {
                        items(
                            items = content.data,
                            key = { it.id }
                        ) { ship ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onShipClick(ship.id)
                                    }
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement =
                                        Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = ship.name,
                                        style = MaterialTheme.typography.titleMedium
                                    )

                                    Text(
                                        text = ship.model,
                                        style = MaterialTheme.typography.bodyMedium
                                    )

                                    Text(
                                        text = "Класс: ${ship.starshipClass}",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = {
                                onEvent(StarshipsListEvent.PreviousPage)
                            },
                            enabled = state.hasPreviousPage
                        ) {
                            Text("Назад")
                        }

                        Text("Стр. ${state.page}")

                        OutlinedButton(
                            onClick = {
                                onEvent(StarshipsListEvent.NextPage)
                            },
                            enabled = state.hasNextPage
                        ) {
                            Text("Далее")
                        }
                    }
                }
            }
        }
    }
}
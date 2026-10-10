
package com.example.recipebook

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun RecipeListScreen(
    uiState: RecipeUiState,
    onSearchChange: (String) -> Unit,
    onFilterChange: (RecipeStatus?) -> Unit,
    onRecipeClick: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Книга рецептов",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = uiState.searchQuery,
            onValueChange = onSearchChange,
            label = { Text("Поиск рецептов") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            FilterChip(
                selected = uiState.selectedStatus == null,
                onClick = { onFilterChange(null) },
                label = { Text("Все") }
            )
            FilterChip(
                selected = uiState.selectedStatus ==
                        RecipeStatus.WANT_TO_COOK,
                onClick = {
                    onFilterChange(RecipeStatus.WANT_TO_COOK)
                },
                label = { Text("Хочу") }
            )
            FilterChip(
                selected = uiState.selectedStatus ==
                        RecipeStatus.COOKING,
                onClick = {
                    onFilterChange(RecipeStatus.COOKING)
                },
                label = { Text("Готовлю") }
            )
            FilterChip(
                selected = uiState.selectedStatus ==
                        RecipeStatus.COOKED,
                onClick = {
                    onFilterChange(RecipeStatus.COOKED)
                },
                label = { Text("Готово") }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Всего: ${uiState.recipes.size} | " +
                    "Приготовлено: ${
                        uiState.recipes.count {
                            it.status == RecipeStatus.COOKED
                        }
                    } | " +
                    "Готовлю: ${
                        uiState.recipes.count {
                            it.status == RecipeStatus.COOKING
                        }
                    }",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (uiState.filteredRecipes.isEmpty()) {
            Text("Ничего не найдено")
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    items = uiState.filteredRecipes,
                    key = { it.id }
                ) { recipe ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onRecipeClick(recipe.id)
                            }
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = recipe.name,
                                style = MaterialTheme.typography.titleLarge
                            )
                            Text(
                                text = "${recipe.category} • " +
                                        "${recipe.cookingTime} мин • " +
                                        recipe.difficulty
                            )
                            Text(
                                text = when (recipe.status) {
                                    RecipeStatus.WANT_TO_COOK ->
                                        "Хочу приготовить"
                                    RecipeStatus.COOKING -> "Готовлю"
                                    RecipeStatus.COOKED -> "Приготовлено"
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

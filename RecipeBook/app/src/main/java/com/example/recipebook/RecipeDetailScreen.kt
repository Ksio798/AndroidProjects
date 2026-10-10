
package com.example.recipebook

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun RecipeDetailScreen(
    recipe: Recipe?,
    onBack: () -> Unit,
    onNextStatus: () -> Unit
) {
    if (recipe == null) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Рецепт не найден")
            Button(onClick = onBack) {
                Text("Назад")
            }
        }
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            TextButton(onClick = onBack) {
                Text("← Назад")
            }
        }

        item {
            Text(
                text = recipe.name,
                style = MaterialTheme.typography.headlineMedium
            )
        }

        item {
            Text(recipe.description)
        }

        item {
            Text("Категория: ${recipe.category}")
            Text("Время: ${recipe.cookingTime} минут")
            Text("Сложность: ${recipe.difficulty}")
        }

        item {
            Text(
                text = "Ингредиенты",
                style = MaterialTheme.typography.titleLarge
            )
        }

        items(recipe.ingredients) { ingredient ->
            Text("• $ingredient")
        }

        item {
            Text(
                text = "Статус: " + when (recipe.status) {
                    RecipeStatus.WANT_TO_COOK -> "Хочу приготовить"
                    RecipeStatus.COOKING -> "Готовлю"
                    RecipeStatus.COOKED -> "Приготовлено"
                }
            )
        }

        item {
            Button(onClick = onNextStatus) {
                Text("Изменить статус")
            }
        }
    }
}

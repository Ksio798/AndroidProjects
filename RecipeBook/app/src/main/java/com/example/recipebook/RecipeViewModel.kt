
package com.example.recipebook

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

data class RecipeUiState(
    val recipes: List<Recipe> = emptyList(),
    val searchQuery: String = "",
    val selectedStatus: RecipeStatus? = null
) {
    val filteredRecipes: List<Recipe>
        get() = recipes.filter { recipe ->
            val matchesSearch = recipe.name.contains(
                searchQuery,
                ignoreCase = true
            )

            val matchesStatus = selectedStatus == null ||
                    recipe.status == selectedStatus

            matchesSearch && matchesStatus
        }
}

class RecipeViewModel : ViewModel() {

    var uiState by mutableStateOf(
        RecipeUiState(
            recipes = listOf(
                Recipe(
                    id = 1,
                    name = "Борщ",
                    description = "Традиционный суп со свёклой.",
                    ingredients = listOf(
                        "Свёкла", "Капуста", "Картофель",
                        "Морковь", "Мясо", "Лук"
                    ),
                    cookingTime = 90,
                    difficulty = "Средняя",
                    category = "Супы",
                    status = RecipeStatus.WANT_TO_COOK
                ),
                Recipe(
                    id = 2,
                    name = "Пельмени",
                    description = "Пельмени с мясной начинкой.",
                    ingredients = listOf(
                        "Мука", "Вода", "Мясной фарш",
                        "Лук", "Соль"
                    ),
                    cookingTime = 60,
                    difficulty = "Средняя",
                    category = "Основные блюда",
                    status = RecipeStatus.COOKING
                ),
                Recipe(
                    id = 3,
                    name = "Блины",
                    description = "Тонкие домашние блины.",
                    ingredients = listOf(
                        "Молоко", "Мука", "Яйца",
                        "Сахар", "Масло"
                    ),
                    cookingTime = 30,
                    difficulty = "Лёгкая",
                    category = "Десерты",
                    status = RecipeStatus.COOKED
                ),
                Recipe(
                    id = 4,
                    name = "Паста карбонара",
                    description = "Итальянская паста с сыром и яйцом.",
                    ingredients = listOf(
                        "Спагетти", "Яйца",
                        "Пекорино", "Гуанчале", "Перец"
                    ),
                    cookingTime = 25,
                    difficulty = "Средняя",
                    category = "Основные блюда",
                    status = RecipeStatus.WANT_TO_COOK
                ),
                Recipe(
                    id = 5,
                    name = "Салат Цезарь",
                    description = "Салат с курицей и сухариками.",
                    ingredients = listOf(
                        "Курица", "Салат", "Сухарики",
                        "Пармезан", "Соус"
                    ),
                    cookingTime = 20,
                    difficulty = "Лёгкая",
                    category = "Салаты",
                    status = RecipeStatus.COOKED
                )
            )
        )
    )
        private set

    fun updateSearch(query: String) {
        uiState = uiState.copy(searchQuery = query)
    }

    fun updateFilter(status: RecipeStatus?) {
        uiState = uiState.copy(selectedStatus = status)
    }

    fun getRecipe(id: Int): Recipe? {
        return uiState.recipes.find { it.id == id }
    }

    fun nextStatus(id: Int) {
        val updatedRecipes = uiState.recipes.map { recipe ->

            if (recipe.id == id) {
                val newStatus = when (recipe.status) {
                    RecipeStatus.WANT_TO_COOK -> RecipeStatus.COOKING
                    RecipeStatus.COOKING -> RecipeStatus.COOKED
                    RecipeStatus.COOKED -> RecipeStatus.WANT_TO_COOK
                }

                recipe.copy(status = newStatus)
            } else {
                recipe
            }
        }

        uiState = uiState.copy(recipes = updatedRecipes)
    }
}

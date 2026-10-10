
package com.example.recipebook

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.BackHandler
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .safeDrawingPadding()
                ) {
                    RecipeApp()
                }
            }
        }
    }
}

@Composable
fun RecipeApp(
    recipeViewModel: RecipeViewModel = viewModel()
) {
    val navController = rememberNavController()
    val uiState = recipeViewModel.uiState

    NavHost(
        navController = navController,
        startDestination = "list"
    ) {
        composable("list") {
            RecipeListScreen(
                uiState = uiState,
                onSearchChange = recipeViewModel::updateSearch,
                onFilterChange = recipeViewModel::updateFilter,
                onRecipeClick = { id ->
                    navController.navigate("detail/$id")
                }
            )
        }

        composable(
            route = "detail/{recipeId}",
            arguments = listOf(
                navArgument("recipeId") {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->

            val recipeId = backStackEntry.arguments
                ?.getInt("recipeId") ?: -1

            val recipe = recipeViewModel.getRecipe(recipeId)

            RecipeDetailScreen(
                recipe = recipe,
                onBack = {
                    navController.popBackStack()
                },
                onNextStatus = {
                    recipeViewModel.nextStatus(recipeId)
                }
            )
        }
    }
}

package com.example.starships.uiTools

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

@Composable
fun StarshipsApp() {
    val navController = rememberNavController()

    val favoritesViewModel: FavoritesViewModel = hiltViewModel()

    val favoritesState by favoritesViewModel.uiState
        .collectAsStateWithLifecycle()

    NavHost(
        navController = navController,
        startDestination = "list"
    ) {
        composable("list") {
            val listViewModel: StarshipsListViewModel = hiltViewModel()

            val state by listViewModel.uiState
                .collectAsStateWithLifecycle()

            StarshipsListScreen(
                state = state,
                onEvent = listViewModel::onEvent,
                onShipClick = { id ->
                    navController.navigate("detail/$id") {
                        launchSingleTop = true
                    }
                },
                onFavoritesClick = {
                    navController.navigate("favorites") {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(
            route = "detail/{id}",
            arguments = listOf(
                navArgument("id") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val detailViewModel: StarshipDetailViewModel = hiltViewModel()

            val state by detailViewModel.uiState
                .collectAsStateWithLifecycle()

            val shipId = backStackEntry.arguments?.getString("id")

            val favoritesContent = favoritesState.content

            val isFavorite = when (favoritesContent) {
                is UiState.Success -> {
                    favoritesContent.data.any { it.id == shipId }
                }

                else -> false
            }

            val favoritesReady =
                favoritesContent is UiState.Success ||
                        favoritesContent == UiState.Empty

            val favoriteError = favoritesState.actionError
                ?: (favoritesContent as? UiState.Error)?.message

            StarshipDetailScreen(
                state = state,
                isFavorite = isFavorite,
                canUpdateFavorite =
                    favoritesReady && !favoritesState.isUpdating,
                favoriteError = favoriteError,
                onEvent = detailViewModel::onEvent,
                onToggleFavorite = { ship ->
                    favoritesViewModel.onEvent(
                        FavoritesEvent.Toggle(ship)
                    )
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable("favorites") {
            FavoritesScreen(
                state = favoritesState,
                onEvent = favoritesViewModel::onEvent,
                onShipClick = { id ->
                    navController.navigate("detail/$id") {
                        launchSingleTop = true
                    }
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
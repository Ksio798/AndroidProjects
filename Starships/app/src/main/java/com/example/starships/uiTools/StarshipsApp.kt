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
        ) {
            val detailViewModel: StarshipDetailViewModel = hiltViewModel()

            val state by detailViewModel.uiState
                .collectAsStateWithLifecycle()

            StarshipDetailScreen(
                state = state,
                onEvent = detailViewModel::onEvent,
                onBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
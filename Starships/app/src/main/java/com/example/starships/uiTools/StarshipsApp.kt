package com.example.starships.uiTools

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.starships.data.AppContainer

private val listViewModelFactory = viewModelFactory {
    initializer {
        StarshipsListViewModel(
            repository = AppContainer.repository
        )
    }
}

private val detailViewModelFactory = viewModelFactory {
    initializer {
        StarshipDetailViewModel(
            savedStateHandle = createSavedStateHandle(),
            repository = AppContainer.repository
        )
    }
}

@Composable
fun StarshipsApp() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "list"
    ) {
        composable("list") {
            val listViewModel: StarshipsListViewModel = viewModel(
                factory = listViewModelFactory
            )

            val state by listViewModel.uiState.collectAsStateWithLifecycle()

            StarshipsListScreen(
                state = state,
                onEvent = listViewModel::onEvent,
                onShipClick = { id ->
                    navController.navigate("detail/$id")
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
            val detailViewModel: StarshipDetailViewModel = viewModel(
                factory = detailViewModelFactory
            )

            val state by detailViewModel.uiState.collectAsStateWithLifecycle()

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
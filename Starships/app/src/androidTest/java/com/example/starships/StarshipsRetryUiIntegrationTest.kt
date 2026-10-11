package com.example.starships

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.starships.data.Starship
import com.example.starships.data.StarshipsRepository
import com.example.starships.data.StarshipsResponse
import com.example.starships.data.SwapiApi
import com.example.starships.uiTools.StarshipsListScreen
import com.example.starships.uiTools.StarshipsListViewModel
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class StarshipsRetryUiIntegrationTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun retryButtonMakesNewRequestAndDisplaysLoadedShip() {
        val ship = testStarship()

        val api = RetryFakeApi(
            response = testResponse(ships = listOf(ship))
        )

        val factory = viewModelFactory {
            initializer {
                StarshipsListViewModel(
                    repository = StarshipsRepository(api)
                )
            }
        }

        composeRule.setContent {
            MaterialTheme {
                val listViewModel: StarshipsListViewModel = viewModel(
                    factory = factory
                )

                val state by listViewModel.uiState
                    .collectAsStateWithLifecycle()

                StarshipsListScreen(
                    state = state,
                    onEvent = listViewModel::onEvent,
                    onShipClick = {},
                    onFavoritesClick = {}
                )
            }
        }

        composeRule.onNodeWithText(
            "Не удалось подключиться к серверу. Проверьте интернет."
        ).assertIsDisplayed()

        composeRule.onNodeWithText(
            "Повторить"
        ).assertIsDisplayed()

        composeRule.runOnIdle {
            assertEquals(1, api.attempts)
        }

        composeRule.onNodeWithText(
            "Повторить"
        ).performClick()

        composeRule.onNodeWithText(
            "Millennium Falcon"
        ).assertIsDisplayed()

        composeRule.onNodeWithText(
            "Не удалось подключиться к серверу. Проверьте интернет."
        ).assertDoesNotExist()

        composeRule.runOnIdle {
            assertEquals(2, api.attempts)
        }
    }
}

private class RetryFakeApi(
    private val response: StarshipsResponse
) : SwapiApi {

    var attempts = 0
        private set

    override suspend fun getStarships(
        page: Int,
        search: String?
    ): StarshipsResponse {
        attempts++

        if (attempts == 1) {
            throw IOException("Test network error")
        }

        return response
    }

    override suspend fun getStarship(id: String): Starship {
        error("Этот тест не должен запрашивать подробности")
    }
}
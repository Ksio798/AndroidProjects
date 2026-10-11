package com.example.starships

import com.example.starships.data.StarshipsRepository
import com.example.starships.uiTools.StarshipsListEvent
import com.example.starships.uiTools.StarshipsListState
import com.example.starships.uiTools.StarshipsListViewModel
import com.example.starships.uiTools.UiState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class StarshipsListViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun initialStateIsLoadingBeforeRequestRuns() = runTest {
        val api = FakeSwapiApi()

        val viewModel = StarshipsListViewModel(
            repository = StarshipsRepository(api)
        )

        assertEquals(
            StarshipsListState(),
            viewModel.uiState.value
        )

        assertEquals(
            UiState.Loading,
            viewModel.uiState.value.content
        )

        // StandardTestDispatcher ещё не выполнил корутину запроса.
        assertTrue(api.listRequests.isEmpty())

        advanceUntilIdle()
    }

    @Test
    fun successfulLoadShowsShipsAndPagination() = runTest {
        val ship = testStarship()

        val api = FakeSwapiApi().apply {
            listHandler = { _, _ ->
                testResponse(
                    ships = listOf(ship),
                    next = "https://swapi.dev/api/starships/?page=2"
                )
            }
        }

        val viewModel = StarshipsListViewModel(
            repository = StarshipsRepository(api)
        )

        advanceUntilIdle()

        assertEquals(
            UiState.Success(listOf(ship)),
            viewModel.uiState.value.content
        )

        assertEquals(1, viewModel.uiState.value.page)
        assertTrue(viewModel.uiState.value.hasNextPage)
        assertEquals(false, viewModel.uiState.value.hasPreviousPage)

        assertEquals(
            listOf(ListRequest(page = 1, search = null)),
            api.listRequests
        )
    }

    @Test
    fun failedLoadShowsError() = runTest {
        val api = FakeSwapiApi().apply {
            listHandler = { _, _ ->
                throw IOException("Test network error")
            }
        }

        val viewModel = StarshipsListViewModel(
            repository = StarshipsRepository(api)
        )

        advanceUntilIdle()

        assertEquals(
            UiState.Error(
                "Не удалось подключиться к серверу. Проверьте интернет."
            ),
            viewModel.uiState.value.content
        )

        assertEquals(1, api.listRequests.size)
    }

    @Test
    fun retryAfterErrorMakesNewRequestAndShowsSuccess() = runTest {
        val ship = testStarship()

        val api = FakeSwapiApi().apply {
            listHandler = { _, _ ->
                throw IOException("Test network error")
            }
        }

        val viewModel = StarshipsListViewModel(
            repository = StarshipsRepository(api)
        )

        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.content is UiState.Error)
        assertEquals(1, api.listRequests.size)

        api.listHandler = { _, _ ->
            testResponse(ships = listOf(ship))
        }

        viewModel.onEvent(StarshipsListEvent.Retry)

        assertEquals(
            UiState.Loading,
            viewModel.uiState.value.content
        )

        advanceUntilIdle()

        assertEquals(
            UiState.Success(listOf(ship)),
            viewModel.uiState.value.content
        )

        // Проверяем именно новую попытку запроса.
        assertEquals(
            listOf(
                ListRequest(page = 1, search = null),
                ListRequest(page = 1, search = null)
            ),
            api.listRequests
        )
    }

    @Test
    fun emptySearchResultShowsEmptyInsteadOfSuccess() = runTest {
        val api = FakeSwapiApi()

        val viewModel = StarshipsListViewModel(
            repository = StarshipsRepository(api)
        )

        // Завершаем первоначальную загрузку.
        advanceUntilIdle()

        api.listHandler = { _, _ ->
            testResponse(ships = emptyList())
        }

        viewModel.onEvent(
            StarshipsListEvent.QueryChanged("  unknown-ship  ")
        )

        viewModel.onEvent(StarshipsListEvent.Search)

        advanceUntilIdle()

        assertEquals(
            UiState.Empty,
            viewModel.uiState.value.content
        )

        // Repository должен убрать пробелы по краям.
        assertEquals(
            ListRequest(
                page = 1,
                search = "unknown-ship"
            ),
            api.listRequests.last()
        )

        assertEquals(false, viewModel.uiState.value.hasNextPage)
        assertEquals(false, viewModel.uiState.value.hasPreviousPage)
    }
}
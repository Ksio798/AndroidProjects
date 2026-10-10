package com.example.starships

import com.example.starships.data.StarshipsRepository
import com.example.starships.data.StarshipsResponse
import com.example.starships.uiTools.StarshipsListEvent
import com.example.starships.uiTools.StarshipsListState
import com.example.starships.uiTools.StarshipsListViewModel
import com.example.starships.uiTools.UiState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class StarshipsListFlowTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun retryEmitsLoadingErrorLoadingSuccessInOrder() = runTest {
        val ship = testStarship()

        val retryResponse = CompletableDeferred<StarshipsResponse>()

        var attempts = 0

        val api = FakeSwapiApi().apply {
            listHandler = { _, _ ->
                attempts++

                if (attempts == 1) {
                    throw IOException("Test network error")
                }

                retryResponse.await()
            }
        }

        val viewModel = StarshipsListViewModel(
            repository = StarshipsRepository(api)
        )

        val emissions = mutableListOf<StarshipsListState>()

        backgroundScope.launch(
            UnconfinedTestDispatcher(testScheduler)
        ) {
            viewModel.uiState.collect { state ->
                emissions += state
            }
        }

        val initial = StarshipsListState()

        // Подписчик сразу получает исходное состояние.
        assertEquals(
            listOf(initial),
            emissions
        )

        // Выполняем первую попытку, которая заканчивается ошибкой.
        runCurrent()

        val errorState = initial.copy(
            content = UiState.Error(
                "Не удалось подключиться к серверу. Проверьте интернет."
            )
        )

        assertEquals(
            listOf(initial, errorState),
            emissions
        )

        viewModel.onEvent(StarshipsListEvent.Retry)

        // Retry немедленно возвращает экран в Loading.
        assertEquals(
            listOf(initial, errorState, initial),
            emissions
        )

        // Вторая попытка запущена, но ответ ещё не получен.
        runCurrent()

        assertEquals(2, attempts)

        assertEquals(
            listOf(initial, errorState, initial),
            emissions
        )

        // Разрешаем повторному запросу завершиться.
        retryResponse.complete(
            testResponse(ships = listOf(ship))
        )

        runCurrent()

        val successState = initial.copy(
            content = UiState.Success(listOf(ship))
        )

        // Проверяем всю последовательность и отсутствие лишних эмиссий.
        assertEquals(
            listOf(
                initial,
                errorState,
                initial,
                successState
            ),
            emissions
        )

        assertEquals(
            listOf(
                ListRequest(page = 1, search = null),
                ListRequest(page = 1, search = null)
            ),
            api.listRequests
        )
    }

    @Test
    fun newSearchCancelsOldRequestAndDoesNotEmitOldResult() = runTest {
        val oldShip = testStarship(
            id = "2",
            name = "Old ship"
        )

        val newShip = testStarship(
            id = "10",
            name = "Millennium Falcon"
        )

        val oldResponse = CompletableDeferred<StarshipsResponse>()
        val newResponse = CompletableDeferred<StarshipsResponse>()

        var oldRequestStarted = false
        var oldRequestCancelled = false

        val api = FakeSwapiApi().apply {
            listHandler = { _, search ->
                when (search) {
                    null -> {
                        oldRequestStarted = true

                        try {
                            oldResponse.await()
                        } catch (exception: CancellationException) {
                            oldRequestCancelled = true
                            throw exception
                        }
                    }

                    "Falcon" -> newResponse.await()

                    else -> error("Unexpected search: $search")
                }
            }
        }

        val viewModel = StarshipsListViewModel(
            repository = StarshipsRepository(api)
        )

        val emissions = mutableListOf<StarshipsListState>()

        backgroundScope.launch(
            UnconfinedTestDispatcher(testScheduler)
        ) {
            viewModel.uiState.collect { state ->
                emissions += state
            }
        }

        // Начальная загрузка запускается и ожидает старый ответ.
        runCurrent()

        assertTrue(oldRequestStarted)

        viewModel.onEvent(
            StarshipsListEvent.QueryChanged("Falcon")
        )

        viewModel.onEvent(StarshipsListEvent.Search)

        runCurrent()

        // Новый поиск должен отменить незавершённый старый запрос.
        assertTrue(oldRequestCancelled)

        assertEquals(
            listOf(
                ListRequest(page = 1, search = null),
                ListRequest(page = 1, search = "Falcon")
            ),
            api.listRequests
        )

        // Сначала приходит ответ на новый поиск.
        newResponse.complete(
            testResponse(ships = listOf(newShip))
        )

        runCurrent()

        val initial = StarshipsListState()

        val searching = initial.copy(
            query = "Falcon"
        )

        val success = searching.copy(
            content = UiState.Success(listOf(newShip))
        )

        val expectedEmissions = listOf(
            initial,
            searching,
            success
        )

        assertEquals(
            expectedEmissions,
            emissions
        )

        // Старый ответ приходит позже нового.
        oldResponse.complete(
            testResponse(ships = listOf(oldShip))
        )

        runCurrent()

        // Он не должен создавать новую эмиссию или заменять результат.
        assertEquals(
            expectedEmissions,
            emissions
        )

        assertEquals(
            success,
            viewModel.uiState.value
        )
    }
}
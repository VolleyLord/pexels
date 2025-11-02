package com.volleylord.pexels

import app.cash.turbine.test
import com.volleylord.core.domain.usecases.settings.SeedInitialApiKeyUseCase
import com.volleylord.pexels.navigation.PhotoListDestination
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class MainViewModelTest {

  private lateinit var viewModel: MainViewModel
  private val seedInitialApiKeyUseCase: SeedInitialApiKeyUseCase = mock()
  private val testDispatcher = StandardTestDispatcher()

  @Before
  fun setUp() {
    Dispatchers.setMain(testDispatcher)
  }

  @After
  fun tearDown() {
    Dispatchers.resetMain()
  }

  @Test
  fun `initializeApp seeds API key and navigates to PhotoListDestination`() = runTest {
    whenever(seedInitialApiKeyUseCase.invoke()).thenReturn(Unit)
    viewModel = MainViewModel(seedInitialApiKeyUseCase)

    viewModel.uiState.test {
      val readyState = awaitItem()
      assertTrue(readyState is MainUiState.Ready)
      assertEquals(PhotoListDestination, (readyState as MainUiState.Ready).startDestination)
    }
  }
}
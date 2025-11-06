package com.volleylord.feature.photos.detail

import androidx.compose.ui.graphics.Color
import app.cash.turbine.test
import com.volleylord.core.core.network.NetworkResult
import com.volleylord.core.domain.models.Photo
import com.volleylord.core.domain.usecases.photos.GetPhotoDetailUseCase
import com.volleylord.feature.photos.presentation.details.PhotoDetailUiState
import com.volleylord.feature.photos.presentation.details.PhotoDetailViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@ExperimentalCoroutinesApi
class PhotoDetailViewModelTest {

  private lateinit var viewModel: PhotoDetailViewModel
  private val getPhotoDetailUseCase: GetPhotoDetailUseCase = mock()
  private val testDispatcher = StandardTestDispatcher()

  @Before
  fun setUp() {
    Dispatchers.setMain(testDispatcher)
    viewModel = PhotoDetailViewModel(getPhotoDetailUseCase)
  }

  @After
  fun tearDown() {
    Dispatchers.resetMain()
  }

  @Test
  fun `loadPhoto success should emit Loading then Success`() = runTest {
    val photoId = 1
    val photo = Photo(1, "", 100, 100, "", "", "", 1L, Color.Companion.Red, "", "", "", false, "")
    val flow = flowOf(NetworkResult.Loading, NetworkResult.Success(photo))
    whenever(getPhotoDetailUseCase.invoke(photoId)).thenReturn(flow)

    viewModel.uiState.test {
      // Initial Loading state
      assertEquals(PhotoDetailUiState.Loading, awaitItem())

      viewModel.loadPhoto(photoId)

      // Skip initial loading and check for success
      assertEquals(PhotoDetailUiState.Loading, awaitItem())
      val successState = awaitItem()
      assertTrue(successState is PhotoDetailUiState.Success)
      assertEquals(photo, (successState as PhotoDetailUiState.Success).photo)
    }
  }

  @Test
  fun `loadPhoto error should emit Loading then Error`() = runTest {
    val photoId = 1
    val errorMessage = "Network Error"
    val flow = flowOf(NetworkResult.Loading, NetworkResult.Error(errorMessage))
    whenever(getPhotoDetailUseCase.invoke(photoId)).thenReturn(flow)

    viewModel.uiState.test {
      assertEquals(PhotoDetailUiState.Loading, awaitItem())
      viewModel.loadPhoto(photoId)

      assertEquals(PhotoDetailUiState.Loading, awaitItem())
      val errorState = awaitItem()
      assertTrue(errorState is PhotoDetailUiState.Error)
      assertEquals(errorMessage, (errorState as PhotoDetailUiState.Error).message)
    }
  }
}
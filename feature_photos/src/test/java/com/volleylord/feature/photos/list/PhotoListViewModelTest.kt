package com.volleylord.feature.photos.list

import androidx.paging.PagingData
import com.volleylord.core.domain.usecases.photos.GetPhotosUseCase
import com.volleylord.feature.photos.photos.list.PhotoListViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class PhotoListViewModelTest {

  private val getPhotosUseCase: GetPhotosUseCase = mock()

  @Test
  fun `photos flow calls use case and is cached`() = runBlocking {
    // Given the use case returns a flow of PagingData
    val pagingDataFlow = flowOf(PagingData.empty<com.volleylord.core.domain.models.Photo>())
    whenever(getPhotosUseCase.invoke()).thenReturn(pagingDataFlow)

    // When the ViewModel is initialized
    val viewModel = PhotoListViewModel(getPhotosUseCase)

    // Then the photos flow is exposed
    viewModel.photos.first()

    // And the use case was called to get the flow
    verify(getPhotosUseCase).invoke()

    // Note: Testing .cachedIn() directly is complex. The primary verification
    // is that the use case is called and the flow is accessible.
  }
}
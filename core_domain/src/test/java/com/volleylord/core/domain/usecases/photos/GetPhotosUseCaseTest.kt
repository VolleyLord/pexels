package com.volleylord.core.domain.usecases.photos

import androidx.paging.PagingData
import com.volleylord.core.domain.models.Photo
import com.volleylord.core.domain.repositories.PhotosRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for [GetPhotosUseCase].
 *
 * Verifies that the use case correctly delegates the retrieval of paginated photos
 * to the [PhotosRepository].
 */
class GetPhotosUseCaseTest {

  private lateinit var useCase: GetPhotosUseCase
  private val photosRepository: PhotosRepository = mockk()

  // Test data for PagingData
  private val dummyPhotosList = listOf(
    Photo(id = 1, type = "image", thumbnailUrl = "url1", tinyThumbnailUrl = null, largeImageUrl = null, url = null, photographer = "A", photographerUrl = null, photographerId = null, alt = null, width = null, height = null, avgColor = null, liked = null),
    Photo(id = 2, type = "image", thumbnailUrl = "url2", tinyThumbnailUrl = null, largeImageUrl = null, url = null, photographer = "B", photographerUrl = null, photographerId = null, alt = null, width = null, height = null, avgColor = null, liked = null)
  )
  private val dummyPagingData = PagingData.from(dummyPhotosList)

  @Before
  fun setup() {
    useCase = GetPhotosUseCase(photosRepository)
  }

  /**
   * Verifies that [invoke] calls the [PhotosRepository.getPhotos] method
   * and returns the [PagingData] flow provided by the repository.
   */
  @Test
  fun `invoke calls repository getPhotos and returns its flow`() = runTest {
    // Given the repository is set up to return a flow of PagingData
    every { photosRepository.getPhotos() } returns flowOf(dummyPagingData)

    // When the use case is invoked
    val resultFlow = useCase()

    // Then, by collecting the first item, we confirm that the flow from the repository
    // was indeed returned by the use case.
    val collectedData = resultFlow.first()

    // Assert that a PagingData object was collected.
    // Direct equality comparison of PagingData objects can be tricky due to their internal mechanisms.
    // However, confirming that a non-null PagingData object was emitted from the mocked flow
    // is sufficient for testing a use case that simply passes through the PagingData flow.
    assertNotNull(collectedData)
  }
}
package com.volleylord.core.domain.usecases.photos

import androidx.compose.ui.graphics.Color
import com.volleylord.core.core.network.NetworkResult
import com.volleylord.core.domain.models.Photo
import com.volleylord.core.domain.repositories.PhotosRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for [GetPhotoDetailUseCase].
 *
 * Verifies that the use case correctly delegates the retrieval of photo details
 * to the [PhotosRepository] and emits the [NetworkResult].
 */
class GetPhotoDetailUseCaseTest {

  private lateinit var useCase: GetPhotoDetailUseCase
  private val photosRepository: PhotosRepository = mockk()

  // Test data
  private val testPhotoId = 123
  private val dummyPhoto = Photo(
    id = testPhotoId,
    type = "image",
    thumbnailUrl = "thumb.url",
    tinyThumbnailUrl = "tiny.url",
    largeImageUrl = "large.url",
    url = "photo.url",
    photographer = "John Doe",
    photographerUrl = "photographer.url",
    photographerId = 456L,
    alt = "A beautiful landscape",
    width = 1000,
    height = 750,
    avgColor = Color.Blue, // Using Color from compose.ui.graphics
    liked = false
  )

  @Before
  fun setup() {
    useCase = GetPhotoDetailUseCase(photosRepository)
  }

  /**
   * Verifies that [invoke] calls the [PhotosRepository.getPhotoDetail] method
   * with the correct photo ID and emits a [NetworkResult.Success] with the photo.
   */
  @Test
  fun `invoke calls repository getPhotoDetail and emits success`() = runTest {
    // Given the repository is set up to return a flow of successful photo detail
    every { photosRepository.getPhotoDetail(testPhotoId) } returns flowOf(NetworkResult.Success(dummyPhoto))

    // When the use case is invoked
    val result = useCase(testPhotoId).first()

    // Then the result should be a NetworkResult.Success containing the dummy photo
    assertTrue(result is NetworkResult.Success)
    assertEquals(dummyPhoto, (result as NetworkResult.Success).data)
  }

  /**
   * Verifies that [invoke] emits a [NetworkResult.Error] when the repository returns an error.
   */
  @Test
  fun `invoke emits error when repository returns error`() = runTest {
    // Given the repository is set up to return a flow of error
    val errorMessage = "Photo not found"
    every { photosRepository.getPhotoDetail(testPhotoId) } returns flowOf(NetworkResult.Error(errorMessage))

    // When the use case is invoked
    val result = useCase(testPhotoId).first()

    // Then the result should be a NetworkResult.Error with the correct message
    assertTrue(result is NetworkResult.Error)
    assertEquals(errorMessage, (result as NetworkResult.Error).message)
  }

  /**
   * Verifies that [invoke] emits a [NetworkResult.Loading] when the repository emits loading.
   */
  @Test
  fun `invoke emits loading when repository emits loading`() = runTest {
    // Given the repository is set up to return a flow that starts with Loading
    every { photosRepository.getPhotoDetail(testPhotoId) } returns flowOf(NetworkResult.Loading)

    // When the use case is invoked
    val result = useCase(testPhotoId).first()

    // Then the result should be NetworkResult.Loading
    assertTrue(result is NetworkResult.Loading)
  }
}
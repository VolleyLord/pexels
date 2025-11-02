package com.volleylord.core.data.remote.paging

import androidx.paging.PagingSource
import com.volleylord.core.data.local.dao.PhotoDao
import com.volleylord.core.data.mappers.PhotoMapper
import com.volleylord.core.data.remote.api.PhotosApi
import com.volleylord.core.data.remote.dto.PhotoDto
import com.volleylord.core.data.remote.dto.PhotosResponseDto
import com.volleylord.core.domain.models.ApiKey
import com.volleylord.core.domain.repositories.SettingsRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.IOException

/**
 * Unit tests for [PhotosPagingSource].
 *
 * This test class verifies the core functionality of the [PhotosPagingSource], ensuring it
 * correctly handles data loading, pagination, and error states such as network failures or
 * missing API keys.
 */
class PhotosPagingSourceTest {

  private val photosApi: PhotosApi = mockk()
  private val settingsRepository: SettingsRepository = mockk()
  private val photoDao: PhotoDao = mockk(relaxed = true) // Relaxed mock as its interactions aren't the focus
  private val photoMapper = PhotoMapper() // Using a real mapper instance for accurate transformations

  private lateinit var photosPagingSource: PhotosPagingSource

  // Test data
  private val mockPhotoDto = PhotoDto(id = 1, alt = "A beautiful landscape")
  private val mockPhoto = photoMapper.mapDtoToDomain(mockPhotoDto)
  private val validApiKey = ApiKey("valid_key")

  @Before
  fun setup() {
    photosPagingSource = PhotosPagingSource(photosApi, settingsRepository, photoMapper, photoDao)
  }

  /**
   * Verifies that `load` returns a `LoadResult.Page` with the correct data and keys
   * when the API call is successful.
   */
  @Test
  fun `load returns page on successful API call`() = runTest {
    coEvery { settingsRepository.getApiKey() } returns validApiKey
    val response = PhotosResponseDto(
      page = 1,
      perPage = 1,
      photos = listOf(mockPhotoDto),
      totalResults = 1,
      nextPage = "next_page_url"
    )
    coEvery { photosApi.getPhotos(any(), any(), any()) } returns response

    val result = photosPagingSource.load(
      PagingSource.LoadParams.Refresh(key = 1, loadSize = 1, placeholdersEnabled = false)
    )

    assertTrue("Result should be a LoadResult.Page", result is PagingSource.LoadResult.Page)
    val page = result as PagingSource.LoadResult.Page
    assertEquals("Page data should match the mock photo list", listOf(mockPhoto), page.data)
    assertEquals("Next key should be the current page + 1", 2, page.nextKey)
    assertEquals("Previous key should be null for the first page", null, page.prevKey)
  }

  /**
   * Verifies that `load` returns a `LoadResult.Error` when the API key is not found
   * in the settings repository.
   */
  @Test
  fun `load returns error when API key is unavailable`() = runTest {
    coEvery { settingsRepository.getApiKey() } returns null

    val result = photosPagingSource.load(
      PagingSource.LoadParams.Refresh(key = 1, loadSize = 1, placeholdersEnabled = false)
    )

    assertTrue("Result should be a LoadResult.Error", result is PagingSource.LoadResult.Error)
    val error = result as PagingSource.LoadResult.Error
    assertEquals(
      "Error message should indicate the API key was not found",
      "API key not found",
      error.throwable.message
    )
  }

  /**
   * Verifies that `load` returns a `LoadResult.Error` when the API call throws
   * a network-related exception.
   */
  @Test
  fun `load returns error on network failure`() = runTest {
    coEvery { settingsRepository.getApiKey() } returns validApiKey
    val networkException = IOException("Network request failed")
    coEvery { photosApi.getPhotos(any(), any(), any()) } throws networkException

    val result = photosPagingSource.load(
      PagingSource.LoadParams.Refresh(key = 1, loadSize = 1, placeholdersEnabled = false)
    )

    assertTrue("Result should be a LoadResult.Error", result is PagingSource.LoadResult.Error)
    val error = result as PagingSource.LoadResult.Error
    assertEquals("The throwable should be the caught network exception", networkException, error.throwable)
  }
}
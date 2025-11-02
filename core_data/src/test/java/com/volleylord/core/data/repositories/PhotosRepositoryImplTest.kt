package com.volleylord.core.data.repositories

import androidx.paging.PagingData
import app.cash.turbine.test
import com.volleylord.core.core.network.NetworkResult
import com.volleylord.core.data.local.dao.PhotoDao
import com.volleylord.core.data.local.entities.PhotoEntity
import com.volleylord.core.data.mappers.PhotoMapper
import com.volleylord.core.data.remote.api.PhotosApi
import com.volleylord.core.data.remote.dto.PhotoDto
import com.volleylord.core.domain.models.ApiKey
import com.volleylord.core.domain.models.Photo
import com.volleylord.core.domain.repositories.SettingsRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for [PhotosRepositoryImpl].
 *
 * Verifies the repository's cache-first strategy, proper interaction with
 * dependencies ([PhotosApi], [PhotoDao], [PhotoMapper], [SettingsRepository]),
 * and correct handling of network failures with graceful fallback to cached data.
 */
class PhotosRepositoryImplTest {

  private lateinit var repository: PhotosRepositoryImpl
  private val api: PhotosApi = mockk()
  private val dao: PhotoDao = mockk()
  private val mapper: PhotoMapper = mockk()
  private val settingsRepository: SettingsRepository = mockk()

  // Test data
  private val testApiKey = "test_api_key"
  private val testPhotoId = 123
  private val testPhotoDto: PhotoDto = mockk()
  private val testPhotoEntity: PhotoEntity = mockk()
  private val testPhoto: Photo = mockk()
  private val testApiKeyModel = ApiKey(value = testApiKey)

  @Before
  fun setup() {
    repository = PhotosRepositoryImpl(api, dao, mapper, settingsRepository)
  }

  /**
   * Verifies that `getPhotos` returns a proper Flow of PagingData.
   */
  @Test
  fun `getPhotos returns PagingData flow`() {
    val result = repository.getPhotos()
    assertTrue(result is Flow<PagingData<Photo>>)
  }

  /**
   * Verifies that `getPhotoDetail` with cached data and successful network call returns only fresh data.
   * Expected flow: Loading -> Fresh Success
   */
  @Test
  fun `getPhotoDetail with cached data and successful network call returns fresh data only`() =
    runTest {
      // Given
      coEvery { dao.getPhotoById(testPhotoId) } returnsMany listOf(testPhotoEntity, testPhotoEntity)
      coEvery { settingsRepository.getApiKey() } returns testApiKeyModel
      coEvery { api.getPhoto(testApiKey, testPhotoId) } returns testPhotoDto
      every { mapper.mapDtoToDomain(testPhotoDto) } returns testPhoto
      every { mapper.mapDomainToEntity(testPhoto) } returns testPhotoEntity
      every { mapper.mapEntityToDomain(testPhotoEntity) } returns testPhoto
      coEvery { dao.insertPhotos(listOf(testPhotoEntity)) } returns Unit

      // When & Then
      repository.getPhotoDetail(testPhotoId).test {
        val loading = awaitItem()
        assertTrue("First emission should be Loading", loading is NetworkResult.Loading)

        val freshResult = awaitItem()
        assertTrue("Second emission should be fresh Success", freshResult is NetworkResult.Success)
        assertEquals(
          "Fresh data should match expected photo", testPhoto, (freshResult as
              NetworkResult.Success).data
        )

        awaitComplete()
      }

      // Verify all expected interactions occurred
      coVerify { dao.getPhotoById(testPhotoId) }
      coVerify { settingsRepository.getApiKey() }
      coVerify { api.getPhoto(testApiKey, testPhotoId) }
      coVerify { dao.insertPhotos(listOf(testPhotoEntity)) }
    }

  /**
   * Verifies that `getPhotoDetail` with no cached data and successful network call returns fresh data only.
   * This test case remains mostly the same, as its original expectation aligns with the new strategy.
   */
  @Test
  fun `getPhotoDetail with no cached data and successful network call returns fresh data only`() =
    runTest {
      // Given
      coEvery { dao.getPhotoById(testPhotoId) } returnsMany listOf(null, testPhotoEntity)
      coEvery { settingsRepository.getApiKey() } returns testApiKeyModel
      coEvery { api.getPhoto(testApiKey, testPhotoId) } returns testPhotoDto
      every { mapper.mapDtoToDomain(testPhotoDto) } returns testPhoto
      every { mapper.mapDomainToEntity(testPhoto) } returns testPhotoEntity
      every { mapper.mapEntityToDomain(testPhotoEntity) } returns testPhoto
      coEvery { dao.insertPhotos(listOf(testPhotoEntity)) } returns Unit

      // When & Then
      repository.getPhotoDetail(testPhotoId).test {
        val loading = awaitItem()
        assertTrue("First emission should be Loading", loading is NetworkResult.Loading)

        val freshResult = awaitItem()
        assertTrue("Second emission should be fresh Success", freshResult is NetworkResult.Success)
        assertEquals(
          "Fresh data should match expected photo", testPhoto, (freshResult as
              NetworkResult.Success).data
        )

        awaitComplete()
      }
    }

  /**
   * Verifies that `getPhotoDetail` gracefully falls back to cached data when API key is unavailable.
   */
  @Test
  fun `getPhotoDetail with no API key and cached data returns cached data only`() = runTest {
    // Given
    coEvery { dao.getPhotoById(testPhotoId) } returns testPhotoEntity
    coEvery { settingsRepository.getApiKey() } returns null
    every { mapper.mapEntityToDomain(testPhotoEntity) } returns testPhoto

    // When & Then
    repository.getPhotoDetail(testPhotoId).test {
      val loading = awaitItem()
      assertTrue("First emission should be Loading", loading is NetworkResult.Loading)

      val cachedResult = awaitItem()
      assertTrue("Second emission should be cached Success", cachedResult is NetworkResult.Success)
      assertEquals(
        "Cached data should match expected photo", testPhoto, (cachedResult
            as NetworkResult.Success).data
      )

      awaitComplete()
    }

    // Verify API was not called when no API key is available
    coVerify(exactly = 0) { api.getPhoto(any(), any()) }
  }

  /**
   * Verifies that `getPhotoDetail` returns error when both API key and cached data are unavailable.
   */
  @Test
  fun `getPhotoDetail with no API key and no cached data returns error`() = runTest {
    // Given
    coEvery { dao.getPhotoById(testPhotoId) } returns null
    coEvery { settingsRepository.getApiKey() } returns null

    // When & Then
    repository.getPhotoDetail(testPhotoId).test {
      val loading = awaitItem()
      assertTrue("First emission should be Loading", loading is NetworkResult.Loading)

      val errorResult = awaitItem()
      assertTrue("Second emission should be Error", errorResult is NetworkResult.Error)
      assertEquals(
        "Error message should indicate missing API key and cache",
        "API key not found and no cached data available.",
        (errorResult as NetworkResult.Error).message
      )

      awaitComplete()
    }
  }

  /**
   * Verifies that `getPhotoDetail` falls back to cached data when network call fails.
   */
  @Test
  fun `getPhoto with network error and cached data returns cached data only`() = runTest {
    // Given
    coEvery { dao.getPhotoById(testPhotoId) } returns testPhotoEntity
    coEvery { settingsRepository.getApiKey() } returns testApiKeyModel
    coEvery { api.getPhoto(testApiKey, testPhotoId) } throws RuntimeException("Network error")
    every { mapper.mapEntityToDomain(testPhotoEntity) } returns testPhoto

    // When & Then
    repository.getPhotoDetail(testPhotoId).test {
      val loading = awaitItem()
      assertTrue("First emission should be Loading", loading is NetworkResult.Loading)

      val cachedResult = awaitItem()
      assertTrue(
        "Should fallback to cached Success on network error",
        cachedResult is NetworkResult.Success
      )
      assertEquals(
        "Cached data should match expected photo", testPhoto, (cachedResult as
            NetworkResult.Success).data
      )

      awaitComplete()
    }
  }

  /**
   * Verifies that `getPhotoDetail` returns error when network fails and no cached data is available.
   */
  @Test
  fun `getPhotoDetail with network error and no cached data returns error`() = runTest {
    // Given
    coEvery { dao.getPhotoById(testPhotoId) } returns null
    coEvery { settingsRepository.getApiKey() } returns testApiKeyModel
    coEvery { api.getPhoto(testApiKey, testPhotoId) } throws RuntimeException("Network error")

    repository.getPhotoDetail(testPhotoId).test {
      val loading = awaitItem()
      assertTrue("First emission should be Loading", loading is NetworkResult.Loading)

      val errorResult = awaitItem()
      assertTrue(
        "Should emit Error when network fails and no cache",
        errorResult is NetworkResult.Error
      )
      assertEquals(
        "Error message should match exception", "Network error", (errorResult as
            NetworkResult.Error).message
      )

      awaitComplete()
    }
  }

  /**
   * Verifies that `getPhotoDetail` provides default error message for exceptions with null messages.
   */
  @Test
  fun `getPhotoDetail with network error having null message returns default error message`() = runTest {
    // Given
    coEvery { dao.getPhotoById(testPhotoId) } returns null
    coEvery { settingsRepository.getApiKey() } returns testApiKeyModel
    coEvery { api.getPhoto(testApiKey, testPhotoId) } throws RuntimeException()

    // When & Then
    repository.getPhotoDetail(testPhotoId).test {
      val loading = awaitItem()
      assertTrue("First emission should be Loading", loading is NetworkResult.Loading)

      val errorResult = awaitItem()
      assertTrue("Should emit Error with default message", errorResult is NetworkResult.Error)
      assertEquals(
        "Should provide default error message for null exception messages",
        "Unknown error occurred",
        (errorResult as NetworkResult.Error).message
      )

      awaitComplete()

    }
  }
}
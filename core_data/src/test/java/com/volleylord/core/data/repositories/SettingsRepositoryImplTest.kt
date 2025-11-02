package com.volleylord.core.data.repositories

import com.volleylord.core.data.local.preferences.SecurePreferencesDataSource
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for [SettingsRepositoryImpl].
 *
 * Verifies interactions with [SecurePreferencesDataSource]
 * and correct behavior for saving, retrieving API keys,
 * and managing initial seed completion state.
 */
class SettingsRepositoryImplTest {

  private lateinit var repository: SettingsRepositoryImpl
  private val securePreferencesDataSource: SecurePreferencesDataSource = mockk()

  // Test data
  private val testApiKey = "test_key"

  @Before
  fun setup() {
    repository = SettingsRepositoryImpl(securePreferencesDataSource)
  }

  /**
   * Verifies that saving an API key calls the underlying data source with the correct key.
   */
  @Test
  fun `saveApiKey calls data source`() = runTest {
    coEvery { securePreferencesDataSource.saveApiKey(testApiKey) } returns Unit

    repository.saveApiKey(testApiKey)

    coVerify { securePreferencesDataSource.saveApiKey(testApiKey) }
  }

  /**
   * Verifies that saving a blank API key throws [IllegalArgumentException].
   */
  @Test(expected = IllegalArgumentException::class)
  fun `saveApiKey with blank key throws exception`() = runTest {
    repository.saveApiKey(" ")
  }

  /**
   * Verifies that retrieving an existing API key returns a valid, non-null value.
   */
  @Test
  fun `getApiKey returns key when it exists`() = runTest {
    every { securePreferencesDataSource.getApiKey() } returns flowOf(testApiKey)

    val result = repository.getApiKey()

    assertNotNull(result)
    assertEquals(testApiKey, result?.value)
  }

  /**
   * Verifies that retrieving a non-existent API key returns null.
   */
  @Test
  fun `getApiKey returns null when it does not exist`() = runTest {
    every { securePreferencesDataSource.getApiKey() } returns flowOf(null)

    val result = repository.getApiKey()

    assertNull(result)
  }

  /**
   * Verifies that isInitialSeedComplete] forwards the call to the data source correctly.
   */
  @Test
  fun `isInitialSeedComplete forwards the call`() = runTest {
    every { securePreferencesDataSource.isInitialSeedComplete() } returns flowOf(true)

    val result = repository.isInitialSeedComplete().first()

    assertTrue(result)
  }

  /**
   * Verifies that setting the initial seed completion forwards the call to the data source.
   */
  @Test
  fun `setInitialSeedComplete forwards the call`() = runTest {
    coEvery { securePreferencesDataSource.setInitialSeedComplete() } returns Unit

    repository.setInitialSeedComplete()

    coVerify { securePreferencesDataSource.setInitialSeedComplete() }
  }
}

package com.volleylord.core.domain.usecases.settings

import com.volleylord.core.domain.repositories.SettingsRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for [SeedInitialApiKeyUseCase].
 *
 * Verifies that the use case correctly delegates the seeding of an initial API key
 * to the [SettingsRepository].
 */
class SeedInitialApiKeyUseCaseTest {

  private lateinit var useCase: SeedInitialApiKeyUseCase
  private val settingsRepository: SettingsRepository = mockk()

  @Before
  fun setup() {
    useCase = SeedInitialApiKeyUseCase(settingsRepository)
  }

  /**
   * Verifies that [invoke] calls the [SettingsRepository.seedInitialApiKeyIfNeeded] method.
   * Any internal logic for checking if the seed is already complete should be handled
   * within the repository's implementation, not by this use case.
   */
  @Test
  fun `invoke calls repository's seedInitialApiKeyIfNeeded`() = runTest {
    // Given the repository is set up to successfully perform the seeding
    coEvery { settingsRepository.seedInitialApiKeyIfNeeded() } returns Unit

    // When the use case is invoked
    useCase()

    // Then the repository's seedInitialApiKeyIfNeeded method should be called exactly once
    coVerify(exactly = 1) { settingsRepository.seedInitialApiKeyIfNeeded() }
  }

  /**
   * Verifies that exceptions from the repository are propagated when calling [seedInitialApiKeyIfNeeded].
   */
  @Test(expected = SecurityException::class)
  fun `invoke propagates SecurityException from repository`() = runTest {
    // Given the repository throws a SecurityException when seeding
    coEvery { settingsRepository.seedInitialApiKeyIfNeeded() } throws SecurityException("Encryption error")

    // When the use case is invoked
    useCase()

    // Then the exception should be propagated
  }

  /**
   * Verifies that exceptions from the repository are propagated when calling [seedInitialApiKeyIfNeeded].
   */
  @Test(expected = java.io.IOException::class)
  fun `invoke propagates IOException from repository`() = runTest {
    // Given the repository throws an IOException when seeding
    coEvery { settingsRepository.seedInitialApiKeyIfNeeded() } throws java.io.IOException("Storage error")

    // When the use case is invoked
    useCase()

    // Then the exception should be propagated
  }
}
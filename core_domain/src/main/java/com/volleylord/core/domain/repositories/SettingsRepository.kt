package com.volleylord.core.domain.repositories

import com.volleylord.core.domain.models.ApiKey
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for managing settings data, such as API keys.
 */
interface SettingsRepository {

  /**
   * Retrieves the stored API key.
   *
   * @return The [ApiKey] if set, or null if not available.
   * @throws java.io.IOException If there is an issue accessing the storage.
   */
  suspend fun getApiKey(): ApiKey?

  /**
   * Seeds an initial API key if none is set.
   *
   * @throws SecurityException If there is an issue with encryption or secure storage.
   * @throws java.io.IOException If there is an issue accessing the storage.
   */
  suspend fun seedInitialApiKeyIfNeeded()

  /**
   * Saves the provided API key.
   *
   * @param apiKey The API key to save.
   * @throws SecurityException If there is an issue with encryption or secure storage.
   * @throws java.io.IOException If there is an issue accessing the storage.
   */
  suspend fun saveApiKey(apiKey: String)

  /**
   * Checks if the initial API key seeding is complete.
   *
   * @return A [Flow] emitting true if the initial seed is complete, false otherwise.
   */
  fun isInitialSeedComplete(): Flow<Boolean>

  /**
   * Marks the initial API key seeding as complete.
   *
   * @throws java.io.IOException If there is an issue accessing the storage.
   */
  suspend fun setInitialSeedComplete()
}
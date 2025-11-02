package com.volleylord.core.data.repositories

import com.volleylord.core.BuildConfig
import com.volleylord.core.data.local.preferences.SecurePreferencesDataSource
import com.volleylord.core.domain.models.ApiKey
import com.volleylord.core.domain.repositories.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of [SettingsRepository] for managing settings data.
 */
@Singleton
class SettingsRepositoryImpl @Inject constructor(
  private val securePreferencesDataSource: SecurePreferencesDataSource
) : SettingsRepository {

  /**
   * Seeds an initial API key if none is set.
   *
   * @throws SecurityException If there is an issue with encryption or secure storage.
   * @throws java.io.IOException If there is an issue accessing the storage.
   */
  override suspend fun seedInitialApiKeyIfNeeded() {
    val isSeedComplete = securePreferencesDataSource.isInitialSeedComplete().first()
    if (!isSeedComplete) {
      val apiKeyFromProperties = BuildConfig.API_KEY
      if (apiKeyFromProperties.isNotBlank()) {
        saveApiKey(apiKeyFromProperties)
      }
      // Mark as complete even if the key was blank, to prevent re-running.
      securePreferencesDataSource.setInitialSeedComplete()
    }
  }

  /**
   * Saves the provided API key.
   *
   * @param apiKey The API key to save.
   * @throws SecurityException If there is an issue with encryption or secure storage.
   * @throws java.io.IOException If there is an issue accessing the storage.
   */
  override suspend fun saveApiKey(apiKey: String) {
    if (apiKey.isBlank()) {
      throw IllegalArgumentException("API key cannot be blank")
    }

    try {
      securePreferencesDataSource.saveApiKey(apiKey)
    } catch (e: SecurityException) {
      throw e
    } catch (e: Exception) {
      // Wrap other exceptions
      throw RuntimeException("Failed to save API key", e)
    }
  }

  /**
   * Retrieves the stored API key.
   *
   * @return The [ApiKey] if set, or null if not available.
   * @throws java.io.IOException If there is an issue accessing the storage.
   */
  override suspend fun getApiKey(): ApiKey? {
    return try {
      val apiKeyValue = securePreferencesDataSource.getApiKey().first()
      apiKeyValue?.let { ApiKey(it) }
    } catch (e: Exception) {
      // If we can't decrypt or retrieve the key, return null
      null
    }
  }

  /**
   * Checks if the initial API key seeding is complete.
   *
   * @return A [Flow] emitting true if the initial seed is complete, false otherwise.
   */
  override fun isInitialSeedComplete(): Flow<Boolean> {
    return securePreferencesDataSource.isInitialSeedComplete()
  }

  /**
   * Marks the initial API key seeding as complete.
   *
   * @throws java.io.IOException If there is an issue accessing the storage.
   */
  override suspend fun setInitialSeedComplete() {
    securePreferencesDataSource.setInitialSeedComplete()
  }
}
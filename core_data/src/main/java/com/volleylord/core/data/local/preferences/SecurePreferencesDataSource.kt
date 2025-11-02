package com.volleylord.core.data.local.preferences

import android.content.Context
import android.util.Base64
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.volleylord.core.data.security.CryptoManager
import com.volleylord.core.data.security.EncryptedData
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Extension property to create a DataStore for secure settings.
 */
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "secure_settings")

/**
 * Data source for securely storing and retrieving API keys using DataStore and encryption.
 */
@Singleton
class SecurePreferencesDataSource @Inject constructor(
  @ApplicationContext private val context: Context,
  private val cryptoManager: CryptoManager
) {

  /**
   * Object containing preference keys for DataStore.
   */
  private object PreferencesKeys {
    val ENCRYPTED_API_KEY = stringPreferencesKey("encrypted_api_key")
    val API_KEY_IV = stringPreferencesKey("api_key_iv")
    val INITIAL_SEED_COMPLETE = booleanPreferencesKey("initial_seed_complete")
  }

  /**
   * Constant for the API key encryption alias.
   */
  companion object {
    private const val API_KEY_ALIAS = "api_key_encryption_key"
  }

  /**
   * Retrieves the stored API key.
   *
   * @return A [Flow] emitting the decrypted API key as a [String], or null if not available.
   * @throws SecurityException If decryption fails due to keystore issues.
   */
  fun getApiKey(): Flow<String?> {
    return context.dataStore.data
      .catch { emit(androidx.datastore.preferences.core.emptyPreferences()) }
      .map { preferences ->
        try {
          val encryptedKeyBase64 = preferences[PreferencesKeys.ENCRYPTED_API_KEY]
          val ivBase64 = preferences[PreferencesKeys.API_KEY_IV]

          if (encryptedKeyBase64 != null && ivBase64 != null) {
            val encryptedData = EncryptedData(
              data = Base64.decode(encryptedKeyBase64, Base64.DEFAULT),
              iv = Base64.decode(ivBase64, Base64.DEFAULT)
            )
            cryptoManager.decrypt(encryptedData, API_KEY_ALIAS)
          } else {
            null
          }
        } catch (e: Exception) {
          // If decryption fails, return null
          // You might want to log this for debugging
          null
        }
      }
  }

  /**
   * Saves the provided API key securely using encryption.
   *
   * @param apiKey The API key to save.
   * @throws SecurityException If encryption fails.
   */
  suspend fun saveApiKey(apiKey: String) {
    try {
      val encryptedData = cryptoManager.encrypt(apiKey, API_KEY_ALIAS)

      context.dataStore.edit { preferences ->
        preferences[PreferencesKeys.ENCRYPTED_API_KEY] =
          Base64.encodeToString(encryptedData.data, Base64.DEFAULT)
        preferences[PreferencesKeys.API_KEY_IV] =
          Base64.encodeToString(encryptedData.iv, Base64.DEFAULT)
      }
    } catch (e: Exception) {
      // Handle encryption failure
      throw SecurityException("Failed to encrypt API key", e)
    }
  }

  /**
   * Clears the stored API key from DataStore.
   */
  suspend fun clearApiKey() {
    context.dataStore.edit { preferences ->
      preferences.remove(PreferencesKeys.ENCRYPTED_API_KEY)
      preferences.remove(PreferencesKeys.API_KEY_IV)
    }
  }

  /**
   * Checks if the initial API key seeding is complete.
   *
   * @return A [Flow] emitting true if the initial seed is complete, false otherwise.
   */
  fun isInitialSeedComplete(): Flow<Boolean> {
    return context.dataStore.data
      .catch { emit(androidx.datastore.preferences.core.emptyPreferences()) }
      .map { preferences ->
        preferences[PreferencesKeys.INITIAL_SEED_COMPLETE] ?: false
      }
  }

  /**
   * Marks the initial API key seeding as complete.
   */
  suspend fun setInitialSeedComplete() {
    context.dataStore.edit { preferences ->
      preferences[PreferencesKeys.INITIAL_SEED_COMPLETE] = true
    }
  }
}
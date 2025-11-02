package com.volleylord.core.di

import android.content.Context
import com.volleylord.core.data.local.preferences.SecurePreferencesDataSource
import com.volleylord.core.data.security.CryptoManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Dagger Hilt module that provides secure local storage and cryptographic components.
 */
@Module
@InstallIn(SingletonComponent::class)
object StorageModule {

  /**
   * Provides the [CryptoManager] for encryption and decryption operations.
   *
   * @return A new instance of [CryptoManager].
   */
  @Provides
  @Singleton
  fun provideCryptoManager(): CryptoManager {
    return CryptoManager()
  }

  /**
   * Provides the [SecurePreferencesDataSource] for securely storing preferences.
   *
   * @param context The application context for accessing SharedPreferences.
   * @param cryptoManager The [CryptoManager] used for encryption.
   * @return An instance of [SecurePreferencesDataSource].
   */
  @Provides
  @Singleton
  fun providePreferencesDataSource(
    @ApplicationContext context: Context,
    cryptoManager: CryptoManager
  ): SecurePreferencesDataSource {
    return SecurePreferencesDataSource(context, cryptoManager)
  }
}
package com.volleylord.core.di

import com.volleylord.core.data.repositories.PhotosRepositoryImpl
import com.volleylord.core.data.repositories.SettingsRepositoryImpl
import com.volleylord.core.domain.repositories.PhotosRepository
import com.volleylord.core.domain.repositories.SettingsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Dagger Hilt module that binds repository implementations to their domain interfaces.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

  /**
   * Binds the implementation of [PhotosRepository] to [PhotosRepositoryImpl].
   *
   * @param photosRepositoryImpl The concrete implementation of [PhotosRepository].
   * @return The bound [PhotosRepository] interface.
   */
  @Binds
  @Singleton
  abstract fun bindPhotosRepository(
    photosRepositoryImpl: PhotosRepositoryImpl
  ): PhotosRepository

  /**
   * Binds the implementation of [SettingsRepository] to [SettingsRepositoryImpl].
   *
   * @param settingsRepositoryImpl The concrete implementation of [SettingsRepository].
   * @return The bound [SettingsRepository] interface.
   */
  @Binds
  @Singleton
  abstract fun bindSettingsRepository(
    settingsRepositoryImpl: SettingsRepositoryImpl
  ): SettingsRepository
}
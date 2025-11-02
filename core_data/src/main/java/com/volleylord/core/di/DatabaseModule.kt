package com.volleylord.core.di

import android.content.Context
import androidx.room.Room
import com.volleylord.core.data.local.dao.PhotoDao
import com.volleylord.core.data.local.database.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Dagger Hilt module that provides database-related dependencies, including Room database
 * and DAO instances.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

  /**
   * Provides the singleton instance of [AppDatabase] configured with Room.
   *
   * @param context The application context.
   * @return The [AppDatabase] instance.
   */
  @Provides
  @Singleton
  fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
    return Room.databaseBuilder(
      context,
      AppDatabase::class.java,
      "photos_database"
    ).build()
  }

  /**
   * Provides the [PhotoDao] instance from the [AppDatabase].
   *
   * @param appDatabase The Room database instance.
   * @return The [PhotoDao] used for photo-related database operations.
   */
  @Provides
  @Singleton
  fun providePhotoDao(appDatabase: AppDatabase): PhotoDao {
    return appDatabase.photoDao()
  }
}
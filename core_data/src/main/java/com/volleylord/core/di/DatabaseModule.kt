package com.volleylord.core.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
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
  /**
   * Migration from version 1 to 2: Add cachedAt and queryType columns.
   */
  private val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
      database.execSQL("ALTER TABLE photos ADD COLUMN cachedAt INTEGER NOT NULL DEFAULT ${System.currentTimeMillis()}")
      database.execSQL("ALTER TABLE photos ADD COLUMN queryType TEXT NOT NULL DEFAULT ''")
    }
  }

  @Provides
  @Singleton
  fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
    return Room.databaseBuilder(
      context,
      AppDatabase::class.java,
      "photos_database"
    )
      .addMigrations(MIGRATION_1_2)
      .fallbackToDestructiveMigration() // For development only - remove in production
      .build()
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
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
   * Migration from version 1 to 2: Add cachedAt and queryType columns.
   */
  private val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
      db.execSQL("ALTER TABLE photos ADD COLUMN cachedAt INTEGER NOT NULL DEFAULT ${System.currentTimeMillis()}")
      db.execSQL("ALTER TABLE photos ADD COLUMN queryType TEXT NOT NULL DEFAULT ''")
    }
  }

  /**
   * Migration from version 2 to 3: Add isBookmarked column.
   */
  private val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
      db.execSQL("ALTER TABLE photos ADD COLUMN isBookmarked INTEGER NOT NULL DEFAULT 0")
    }
  }

  /**
   * Migration from version 3 to 4: Add originalImageUrl and large2xImageUrl columns.
   */
  private val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
      db.execSQL("ALTER TABLE photos ADD COLUMN originalImageUrl TEXT")
      db.execSQL("ALTER TABLE photos ADD COLUMN large2xImageUrl TEXT")
    }
  }

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
    )
        .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
        .fallbackToDestructiveMigration(false) // For development only - remove in production
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
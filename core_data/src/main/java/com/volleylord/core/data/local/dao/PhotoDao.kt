package com.volleylord.core.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.volleylord.core.data.local.entities.PhotoEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) for photo-related database operations.
 */
@Dao
interface PhotoDao {

  /**
   * Inserts a list of photos into the database, replacing existing entries with the same ID.
   *
   * @param photos The list of [PhotoEntity] objects to insert.
   */
  @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
  suspend fun insertPhotos(photos: List<PhotoEntity>)

  /**
   * Retrieves all photos from the database, ordered by ID in descending order.
   *
   * @return A [Flow] emitting a list of [PhotoEntity] objects.
   */
  @Query("SELECT * FROM photos ORDER BY id DESC")
  fun getAllPhotos(): Flow<List<PhotoEntity>>

  /**
   * Retrieves a photo by its ID.
   *
   * @param photoId The ID of the photo to retrieve.
   * @return The [PhotoEntity] if found, or null if not available.
   */
  @Query("SELECT * FROM photos WHERE id = :photoId")
  suspend fun getPhotoById(photoId: Int): PhotoEntity?

  /**
   * Deletes all photos from the database.
   */
  @Query("DELETE FROM photos")
  suspend fun clearAllPhotos()

  /**
   * Checks if there are any cached photos in the database.
   *
   * @return The count of cached photos.
   */
  @Query("SELECT COUNT(*) FROM photos")
  suspend fun getCachedPhotosCount(): Int

  /**
   * Retrieves cached photos for a specific query type that are still valid (not expired).
   * Cache is valid for 1 hour (3600000 milliseconds).
   *
   * @param queryType The query type (empty string for curated/popular photos, or search query).
   * @param currentTime The current timestamp in milliseconds.
   * @param cacheValidityMillis Cache validity period in milliseconds (default: 1 hour).
   * @return List of cached photos that are still valid.
   */
  @Query("""
    SELECT * FROM photos 
    WHERE queryType = :queryType 
    AND cachedAt > (:currentTime - :cacheValidityMillis)
    ORDER BY id DESC
    LIMIT 30
  """)
  suspend fun getCachedPhotosByQuery(
    queryType: String,
    currentTime: Long,
    cacheValidityMillis: Long = 3600000L
  ): List<PhotoEntity>

  /**
   * Clears expired cache entries (older than 1 hour).
   *
   * @param currentTime The current timestamp in milliseconds.
   * @param cacheValidityMillis Cache validity period in milliseconds (default: 1 hour).
   */
  @Query("""
    DELETE FROM photos 
    WHERE cachedAt <= (:currentTime - :cacheValidityMillis)
  """)
  suspend fun clearExpiredCache(
    currentTime: Long,
    cacheValidityMillis: Long = 3600000L
  )

  /**
   * Clears cache for a specific query type.
   *
   * @param queryType The query type to clear cache for.
   */
  @Query("DELETE FROM photos WHERE queryType = :queryType")
  suspend fun clearCacheForQuery(queryType: String)
}
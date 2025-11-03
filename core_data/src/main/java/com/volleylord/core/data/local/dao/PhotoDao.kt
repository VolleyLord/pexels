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

  /**
   * Retrieves all bookmarked photos from the database, ordered by ID in descending order.
   * Used for pagination with page size 30.
   *
   * @param limit Maximum number of photos to retrieve.
   * @param offset Number of photos to skip (for pagination).
   * @return List of bookmarked [PhotoEntity] objects.
   */
  @Query("SELECT * FROM photos WHERE isBookmarked = 1 ORDER BY id DESC LIMIT :limit OFFSET :offset")
  suspend fun getBookmarkedPhotos(limit: Int, offset: Int): List<PhotoEntity>

  /**
   * Gets the total count of bookmarked photos.
   *
   * @return The count of bookmarked photos.
   */
  @Query("SELECT COUNT(*) FROM photos WHERE isBookmarked = 1")
  suspend fun getBookmarkedPhotosCount(): Int

  /**
   * Checks if a photo is bookmarked.
   *
   * @param photoId The ID of the photo to check.
   * @return true if the photo is bookmarked, false otherwise.
   */
  @Query("SELECT isBookmarked FROM photos WHERE id = :photoId")
  suspend fun isPhotoBookmarked(photoId: Int): Boolean?

  /**
   * Updates the bookmark status of a photo.
   *
   * @param photoId The ID of the photo.
   * @param isBookmarked The new bookmark status.
   */
  @Query("UPDATE photos SET isBookmarked = :isBookmarked WHERE id = :photoId")
  suspend fun updateBookmarkStatus(photoId: Int, isBookmarked: Boolean)
}
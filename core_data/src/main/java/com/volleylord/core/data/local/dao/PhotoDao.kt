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
}
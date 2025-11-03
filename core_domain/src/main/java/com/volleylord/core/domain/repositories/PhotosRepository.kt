package com.volleylord.core.domain.repositories

import androidx.paging.PagingData
import com.volleylord.core.core.network.NetworkResult
import com.volleylord.core.domain.models.Photo
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for accessing photo data.
 */
interface PhotosRepository {

  /**
   * Retrieves a paginated list of curated photos.
   *
   * @return A [Flow] of [PagingData] containing [Photo] objects.
   */
  fun getPhotos(): Flow<PagingData<Photo>>

  /**
   * Searches for photos by query (category/keyword).
   *
   * @param query The search query (e.g., category name like "ice", "watches").
   * @return A [Flow] of [PagingData] containing [Photo] objects.
   */
  fun searchPhotos(query: String): Flow<PagingData<Photo>>

  /**
   * Retrieves details of a specific photo by its ID.
   *
   * @param photoId The ID of the photo to retrieve.
   * @param isFromBookmarks Whether to load from database (bookmarks) instead of network.
   * @return A [Flow] emitting a [NetworkResult] containing the [Photo] or an error.
   */
  fun getPhotoDetail(photoId: Int, isFromBookmarks: Boolean = false): Flow<NetworkResult<Photo>>

  /**
   * Retrieves a paginated list of bookmarked photos from the local database.
   *
   * @return A [Flow] of [PagingData] containing [Photo] objects.
   */
  fun getBookmarks(): Flow<PagingData<Photo>>

  /**
   * Toggles the bookmark status of a photo.
   *
   * @param photoId The ID of the photo to bookmark/unbookmark.
   * @param isBookmarked Whether the photo should be bookmarked.
   */
  suspend fun toggleBookmark(photoId: Int, isBookmarked: Boolean)
}
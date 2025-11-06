package com.volleylord.core.data.remote.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.volleylord.core.core.network.NetworkErrorUtils
import com.volleylord.core.data.local.dao.PhotoDao
import com.volleylord.core.data.mappers.PhotoMapper
import com.volleylord.core.data.remote.api.PhotosApi
import com.volleylord.core.domain.models.Photo
import com.volleylord.core.domain.repositories.SettingsRepository

/**
 * Paging source for loading paginated photo data from the Pexels API.
 */
class PhotosPagingSource(
  private val photosApi: PhotosApi,
  private val settingsRepository: SettingsRepository,
  private val photoMapper: PhotoMapper,
  private val photoDao: PhotoDao
) : PagingSource<Int, Photo>() {

  companion object {
    private const val QUERY_TYPE_CURATED = "" // Empty string for curated/popular photos
    private const val CACHE_VALIDITY_HOURS = 1L
    private const val CACHE_VALIDITY_MILLIS = CACHE_VALIDITY_HOURS * 60 * 60 * 1000
  }

  /**
   * Loads a page of photos from the API.
   * On network errors, attempts to load from cache if available (for page 1 only).
   *
   * @param params The parameters for loading the page, including page size.
   * @return A [LoadResult] containing the loaded photos, next/previous keys, or an error.
   */
  override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Photo> {
    val page = params.key ?: 1
    val currentTime = System.currentTimeMillis()
    
    return try {
      val apiKey = settingsRepository.getApiKey()?.value
        ?: return LoadResult.Error(Exception("API key not found"))

      // Before making network request, clear expired cache but keep valid cache
      if (page == 1) {
        photoDao.clearExpiredCache(currentTime, CACHE_VALIDITY_MILLIS)
        // Only clear curated cache if we successfully get new data
        // Don't clear it here - we'll clear it after successful load
      }

      val response = photosApi.getPhotos(apiKey, page, params.loadSize)
      val photos = response.photos.map { photoMapper.mapDtoToDomain(it) }

      // Save to cache with queryType = "" for curated photos
      // Only save first page to cache (limit to 30 photos for curated)
      if (page == 1) {
        // Clear old curated cache only after successful load (but keep bookmarks)
        photoDao.clearCacheForQuery(QUERY_TYPE_CURATED)

        // Preserve bookmark flags when writing cache
        val bookmarkedIds = photoDao.getBookmarkedIds().toHashSet()
        val photosToCache = photos.take(30) // Cache only first 30 curated photos
        photoDao.insertPhotos(
          photosToCache.map {
            val isBookmarked = it.id in bookmarkedIds
            photoMapper.mapDomainToEntity(
              domain = it,
              queryType = QUERY_TYPE_CURATED,
              cachedAt = currentTime,
              isBookmarked = isBookmarked
            )
          }
        )
      }

      LoadResult.Page(
        data = photos,
        prevKey = if (page == 1) null else page - 1,
        nextKey = if (response.nextPage == null) null else page + 1
      )
    } catch (exception: Exception) {
      // On network error for first page, try to load from cache BEFORE clearing
      if (page == 1 && NetworkErrorUtils.isNetworkError(exception)) {
        // Clear expired cache but keep valid cache
        photoDao.clearExpiredCache(currentTime, CACHE_VALIDITY_MILLIS)
        
        // Try to load valid cache for curated photos
        val cachedPhotos = photoDao.getCachedPhotosByQuery(
          QUERY_TYPE_CURATED,
          currentTime,
          CACHE_VALIDITY_MILLIS
        )
        
        if (cachedPhotos.isNotEmpty()) {
          val photos = cachedPhotos.map { photoMapper.mapEntityToDomain(it) }
          return LoadResult.Page(
            data = photos,
            prevKey = null,
            nextKey = null // No pagination for cached data
          )
        }
      }
      
      // If no cache available or not a network error, return error
      return LoadResult.Error(exception)
    }
  }

  /**
   * Gets the refresh key for the current paging state.
   *
   * @param state The current [PagingState].
   * @return The key to use for refreshing the data, or null if not applicable.
   */
  override fun getRefreshKey(state: PagingState<Int, Photo>): Int? {
    return state.anchorPosition?.let { anchorPosition ->
      state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
        ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
    }
  }
}
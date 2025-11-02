package com.volleylord.core.data.remote.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
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

  /**
   * Loads a page of photos from the API.
   *
   * @param params The parameters for loading the page, including page size.
   * @return A [LoadResult] containing the loaded photos, next/previous keys, or an error.
   */
  override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Photo> {
    val page = params.key ?: 1
    return try {
      val apiKey = settingsRepository.getApiKey()?.value
        ?: return LoadResult.Error(Exception("API key not found"))

      val response = photosApi.getPhotos(apiKey, page, params.loadSize)
      val photos = response.photos.map { photoMapper.mapDtoToDomain(it) }

      if (page == 1) {
        photoDao.clearAllPhotos()
      }
      photoDao.insertPhotos(photos.map { photoMapper.mapDomainToEntity(it) })

      LoadResult.Page(
        data = photos,
        prevKey = if (page == 1) null else page - 1,
        nextKey = if (response.nextPage == null) null else page + 1
      )
    } catch (exception: Exception) {
      // Any exception thrown in the try block (network, parsing, DB, etc.)
      // is caught here and converted into a state that the UI can handle.
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
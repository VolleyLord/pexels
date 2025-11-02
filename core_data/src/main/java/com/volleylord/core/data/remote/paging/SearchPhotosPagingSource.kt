package com.volleylord.core.data.remote.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.volleylord.core.data.local.dao.PhotoDao
import com.volleylord.core.data.mappers.PhotoMapper
import com.volleylord.core.data.remote.api.PhotosApi
import com.volleylord.core.domain.models.Photo
import com.volleylord.core.domain.repositories.SettingsRepository

/**
 * Paging source for loading paginated search results from the Pexels API.
 */
class SearchPhotosPagingSource(
  private val photosApi: PhotosApi,
  private val settingsRepository: SettingsRepository,
  private val photoMapper: PhotoMapper,
  private val photoDao: PhotoDao,
  private val query: String
) : PagingSource<Int, Photo>() {

  /**
   * Loads a page of search results from the API.
   *
   * @param params The parameters for loading the page, including page size.
   * @return A [LoadResult] containing the loaded photos, next/previous keys, or an error.
   */
  override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Photo> {
    val page = params.key ?: 1
    return try {
      val apiKey = settingsRepository.getApiKey()?.value
        ?: return LoadResult.Error(Exception("API key not found"))

      val response = photosApi.searchPhotos(apiKey, query, page, params.loadSize)
      val photos = response.photos.map { photoMapper.mapDtoToDomain(it) }

      // Cache search results (optional - можно пропустить для поиска)
      photoDao.insertPhotos(photos.map { photoMapper.mapDomainToEntity(it) })

      LoadResult.Page(
        data = photos,
        prevKey = if (page == 1) null else page - 1,
        nextKey = if (response.nextPage == null) null else page + 1
      )
    } catch (exception: Exception) {
      LoadResult.Error(exception)
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


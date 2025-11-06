package com.volleylord.core.data.remote.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.volleylord.core.data.local.dao.PhotoDao
import com.volleylord.core.data.mappers.PhotoMapper
import com.volleylord.core.domain.models.Photo

/**
 * [PagingSource] implementation for loading bookmarked photos from the local database.
 * Supports pagination with 30 photos per page.
 *
 * @param photoDao The [PhotoDao] for database operations.
 * @param photoMapper The [PhotoMapper] for converting entities to domain models.
 */
class BookmarksPagingSource(
  private val photoDao: PhotoDao,
  private val photoMapper: PhotoMapper
) : PagingSource<Int, Photo>() {

  companion object {
    private const val PAGE_SIZE = 30
  }

  override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Photo> {
    return try {
      val page = params.key ?: 0
      val limit = params.loadSize
      val offset = page * limit

      android.util.Log.d("BookmarksPagingSource", "Loading bookmarks - page: $page, offset: $offset, limit: $limit")
      
      // Проверяем общее количество закладок в БД для диагностики
      val totalCount = photoDao.getBookmarkedPhotosCount()
      android.util.Log.d("BookmarksPagingSource", "Total bookmarked photos in DB: $totalCount")

      val photos = photoDao.getBookmarkedPhotos(limit, offset)
      android.util.Log.d("BookmarksPagingSource", "Raw photos from DB query: ${photos.size}")
      
      val mappedPhotos = photos.map { photoMapper.mapEntityToDomain(it) }

      android.util.Log.d("BookmarksPagingSource", "Loaded ${mappedPhotos.size} bookmarked photos from database")
      if (mappedPhotos.isNotEmpty()) {
        android.util.Log.d("BookmarksPagingSource", "First photo ID: ${mappedPhotos.first().id}, Last photo ID: ${mappedPhotos.last().id}")
      } else {
        android.util.Log.w("BookmarksPagingSource", "WARNING: No photos loaded! Total count in DB: $totalCount, but query returned 0")
      }

      val nextKey = if (mappedPhotos.size < limit) {
        null // Last page
      } else {
        page + 1
      }

      LoadResult.Page(
        data = mappedPhotos,
        prevKey = if (page == 0) null else page - 1,
        nextKey = nextKey
      )
    } catch (exception: Exception) {
      android.util.Log.e("BookmarksPagingSource", "Error loading bookmarks", exception)
      LoadResult.Error(exception)
    }
  }

  override fun getRefreshKey(state: PagingState<Int, Photo>): Int? {
    return state.anchorPosition?.let { anchorPosition ->
      val anchorPage = state.closestPageToPosition(anchorPosition)
      anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
    }
  }
}

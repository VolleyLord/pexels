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

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Photo> {
    return try {
      val page = params.key ?: 0
      val limit = params.loadSize
      val offset = page * limit

      val photos = photoDao.getBookmarkedPhotos(limit, offset)
      val mappedPhotos = photos.map { photoMapper.mapEntityToDomain(it) }

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

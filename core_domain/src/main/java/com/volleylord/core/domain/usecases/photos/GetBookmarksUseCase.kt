package com.volleylord.core.domain.usecases.photos

import androidx.paging.PagingData
import com.volleylord.core.domain.models.Photo
import com.volleylord.core.domain.repositories.PhotosRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for retrieving a paginated list of bookmarked photos.
 *
 * @param photosRepository Repository for accessing photo data.
 */
class GetBookmarksUseCase @Inject constructor(
  private val photosRepository: PhotosRepository
) {

  /**
   * Fetches a paginated list of bookmarked photos from the repository.
   *
   * @return A [Flow] of [PagingData] containing [Photo] objects.
   */
  operator fun invoke(): Flow<PagingData<Photo>> {
    return photosRepository.getBookmarks()
  }
}

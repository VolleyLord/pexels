package com.volleylord.core.domain.usecases.photos

import com.volleylord.core.core.network.NetworkResult
import com.volleylord.core.domain.models.Photo
import com.volleylord.core.domain.repositories.PhotosRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for retrieving details of a specific photo.
 */
class GetPhotoDetailUseCase @Inject constructor(
  private val photosRepository: PhotosRepository
) {

  /**
   * Fetches the details of a photo by its ID.
   *
   * @param photoId The ID of the photo to retrieve.
   * @param isFromBookmarks Whether the photo is being loaded from bookmarks (database).
   * @return A [Flow] emitting a [NetworkResult] containing the [Photo] or an error.
   */
  operator fun invoke(photoId: Int, isFromBookmarks: Boolean = false): Flow<NetworkResult<Photo>> {
    return photosRepository.getPhotoDetail(photoId, isFromBookmarks)
  }
}
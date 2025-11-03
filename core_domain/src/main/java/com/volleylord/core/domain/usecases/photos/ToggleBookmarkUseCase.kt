package com.volleylord.core.domain.usecases.photos

import com.volleylord.core.domain.repositories.PhotosRepository
import javax.inject.Inject

/**
 * Use case for toggling the bookmark status of a photo.
 *
 * @param photosRepository Repository for accessing photo data.
 */
class ToggleBookmarkUseCase @Inject constructor(
  private val photosRepository: PhotosRepository
) {

  /**
   * Toggles the bookmark status of a photo.
   *
   * @param photoId The ID of the photo to bookmark/unbookmark.
   * @param isBookmarked Whether the photo should be bookmarked.
   */
  suspend operator fun invoke(photoId: Int, isBookmarked: Boolean) {
    photosRepository.toggleBookmark(photoId, isBookmarked)
  }
}

package com.volleylord.core.domain.usecases.photos

import androidx.paging.PagingData
import com.volleylord.core.domain.models.Photo
import com.volleylord.core.domain.repositories.PhotosRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for retrieving a paginated list of photos.
 * 
 * @param photosRepository Repository for accessing photo data.
 */
class GetPhotosUseCase @Inject constructor(
  private val photosRepository: PhotosRepository
) {

  /**
   * Fetches a paginated list of curated photos from the repository.
   *
   * @return A [Flow] of [PagingData] containing [Photo] objects.
   */
  operator fun invoke(): Flow<PagingData<Photo>> {
    return photosRepository.getPhotos()
  }

  /**
   * Searches for photos by query (category/keyword).
   *
   * @param query The search query (e.g., category name like "ice", "watches").
   * @return A [Flow] of [PagingData] containing [Photo] objects.
   */
  operator fun invoke(query: String): Flow<PagingData<Photo>> {
    return if (query.isBlank()) {
      photosRepository.getPhotos() // Если запрос пустой, возвращаем curated photos
    } else {
      photosRepository.searchPhotos(query)
    }
  }
}
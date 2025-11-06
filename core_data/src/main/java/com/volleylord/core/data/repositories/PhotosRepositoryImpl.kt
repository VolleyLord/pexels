package com.volleylord.core.data.repositories

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.volleylord.core.core.Constants
import com.volleylord.core.core.network.NetworkResult
import com.volleylord.core.data.local.dao.PhotoDao
import com.volleylord.core.data.mappers.PhotoMapper
import com.volleylord.core.data.remote.api.PhotosApi
import com.volleylord.core.data.remote.paging.BookmarksPagingSource
import com.volleylord.core.data.remote.paging.PhotosPagingSource
import com.volleylord.core.data.remote.paging.SearchPhotosPagingSource
import com.volleylord.core.domain.models.Photo
import com.volleylord.core.domain.repositories.PhotosRepository
import com.volleylord.core.domain.repositories.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of [PhotosRepository] for fetching photo data from a remote API.
 */
@Singleton
class PhotosRepositoryImpl @Inject constructor(
  private val api: PhotosApi,
  private val dao: PhotoDao,
  private val mapper: PhotoMapper,
  private val settingsRepository: SettingsRepository
) : PhotosRepository {

  /**
   * Retrieves a paginated list of curated photos.
   *
   * @return A [Flow] of [PagingData] containing [Photo] objects.
   */
  override fun getPhotos(): Flow<PagingData<Photo>> {
    return Pager(
      config = PagingConfig(
        pageSize = Constants.PAGE_SIZE,
        enablePlaceholders = false
      ),
      pagingSourceFactory = {
        PhotosPagingSource(api, settingsRepository, mapper, dao)
      }
    ).flow
  }

  /**
   * Searches for photos by query (category/keyword).
   *
   * @param query The search query (e.g., category name like "ice", "watches").
   * @return A [Flow] of [PagingData] containing [Photo] objects.
   */
  override fun searchPhotos(query: String): Flow<PagingData<Photo>> {
    return Pager(
      config = PagingConfig(
        pageSize = Constants.PAGE_SIZE,
        enablePlaceholders = false
      ),
      pagingSourceFactory = {
        SearchPhotosPagingSource(api, settingsRepository, mapper, dao, query)
      }
    ).flow
  }

  /**
   * Fetches from [PhotosApi] or [PhotoDao] depending on source.
   *
   * @param photoId The ID of the photo to retrieve.
   * @param isFromBookmarks Whether to load from database only (for bookmarks).
   * @return A [Flow] emitting a [NetworkResult] containing the [Photo] or an error.
   */
  override fun getPhotoDetail(photoId: Int, isFromBookmarks: Boolean): Flow<NetworkResult<Photo>> = flow {
    emit(NetworkResult.Loading)

    if (isFromBookmarks) {
      val photoEntity = dao.getPhotoById(photoId)
      if (photoEntity != null) {
        emit(NetworkResult.Success(mapper.mapEntityToDomain(photoEntity)))
      } else {
        emit(NetworkResult.Error("Photo not found in bookmarks"))
      }
      return@flow
    }

    val cachedPhotoEntity = dao.getPhotoById(photoId)
    try {
      val apiKey = settingsRepository.getApiKey()?.value
      if (apiKey.isNullOrBlank()) {
        if (cachedPhotoEntity == null) {
          emit(NetworkResult.Error("API key not found and no cached data available."))
        } else {
          // Синхронизируем статус bookmark из БД
          val photo = mapper.mapEntityToDomain(cachedPhotoEntity)
          val bookmarkStatus = dao.isPhotoBookmarked(photoId) ?: false
          emit(NetworkResult.Success(photo.copy(liked = bookmarkStatus)))
        }
        return@flow
      }

      val response = api.getPhoto(apiKey = apiKey, id = photoId)
      val freshPhoto = mapper.mapDtoToDomain(response)
      val currentTime = System.currentTimeMillis()

      // Сохраняем фото в БД, сохраняя текущий статус bookmark если фото уже было в БД
      val existingBookmarkStatus = dao.isPhotoBookmarked(photoId) ?: false
      dao.insertPhotos(listOf(mapper.mapDomainToEntity(freshPhoto, "", currentTime, isBookmarked = existingBookmarkStatus)))

      // Получаем обновленное фото из БД с актуальным статусом bookmark
      val updatedPhotoEntity = dao.getPhotoById(photoId)
      if (updatedPhotoEntity != null) {
        val photo = mapper.mapEntityToDomain(updatedPhotoEntity)
        emit(NetworkResult.Success(photo))
      } else {
        emit(NetworkResult.Success(freshPhoto.copy(liked = existingBookmarkStatus)))
      }
    } catch (e: Exception) {
      if (cachedPhotoEntity == null) {
        emit(NetworkResult.Error(e.message ?: "Unknown error occurred"))
      } else {
        // Синхронизируем статус bookmark из БД
        val photo = mapper.mapEntityToDomain(cachedPhotoEntity)
        val bookmarkStatus = dao.isPhotoBookmarked(photoId) ?: false
        emit(NetworkResult.Success(photo.copy(liked = bookmarkStatus)))
      }
    }
  }

  /**
   * Retrieves a paginated list of bookmarked photos from the local database.
   *
   * @return A [Flow] of [PagingData] containing [Photo] objects.
   */
  override fun getBookmarks(): Flow<PagingData<Photo>> {
    return Pager(
      config = PagingConfig(
        pageSize = Constants.PAGE_SIZE,
        enablePlaceholders = false
      ),
      pagingSourceFactory = {
        BookmarksPagingSource(dao, mapper)
      }
    ).flow
  }

  /**
   * Toggles the bookmark status of a photo.
   *
   * @param photoId The ID of the photo to bookmark/unbookmark.
   * @param isBookmarked Whether the photo should be bookmarked.
   */
  override suspend fun toggleBookmark(photoId: Int, isBookmarked: Boolean) {
    android.util.Log.d("PhotosRepository", "Toggle bookmark called - photoId: $photoId, isBookmarked: $isBookmarked")
    
    // Сначала проверяем, существует ли фото в БД
    val existingPhoto = dao.getPhotoById(photoId)
    
    if (existingPhoto == null && isBookmarked) {
      // Фото не существует, но мы хотим его добавить в bookmarks
      // Нужно сначала получить фото из API
      try {
        val apiKey = settingsRepository.getApiKey()?.value
        if (!apiKey.isNullOrBlank()) {
          val response = api.getPhoto(apiKey = apiKey, id = photoId)
          val photo = mapper.mapDtoToDomain(response)
          val currentTime = System.currentTimeMillis()
          dao.insertPhotos(
            listOf(mapper.mapDomainToEntity(photo, "", currentTime, isBookmarked = true))
          )
        }
      } catch (e: Exception) {
        e.printStackTrace()
        // Если не удалось получить фото из API, не можем добавить в bookmarks
        return
      }
    } else if (existingPhoto != null) {
      // Фото существует, обновляем статус bookmark
      android.util.Log.d("PhotosRepository", "Updating bookmark status in database for photoId: $photoId")
      dao.updateBookmarkStatus(photoId, isBookmarked)
      android.util.Log.d("PhotosRepository", "Bookmark status updated successfully")
      
      // Проверяем, что изменения применились
      val updatedPhoto = dao.getPhotoById(photoId)
      val actualBookmarkStatus = dao.isPhotoBookmarked(photoId)
      val totalBookmarksCount = dao.getBookmarkedPhotosCount()
      android.util.Log.d("PhotosRepository", "Verification after update - photo exists: ${updatedPhoto != null}, isBookmarked: $actualBookmarkStatus, total bookmarks in DB: $totalBookmarksCount")
    } else {
      android.util.Log.d("PhotosRepository", "Photo not found in DB and not bookmarking - nothing to do")
    }
    // Если existingPhoto == null && !isBookmarked - ничего не делаем, так как фото не в БД
  }
}
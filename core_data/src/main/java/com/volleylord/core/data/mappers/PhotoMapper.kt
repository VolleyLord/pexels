package com.volleylord.core.data.mappers

import androidx.compose.ui.graphics.Color
import androidx.core.graphics.toColorInt
import com.volleylord.core.data.local.entities.PhotoEntity
import com.volleylord.core.data.remote.dto.PhotoDto
import com.volleylord.core.data.remote.dto.PhotosResponseDto
import com.volleylord.core.domain.models.Photo
import com.volleylord.core.domain.models.Photos
import javax.inject.Inject

/**
 * Mapper class for converting between network DTOs, database entities, and domain models for photos.
 */
class PhotoMapper @Inject constructor() {

  /**
   * Maps a network response DTO to a domain model.
   *
   * @param dto The [PhotosResponseDto] to map.
   * @return A [Photos] domain model containing the list of photos and pagination metadata.
   */
  fun mapDtoToDomain(dto: PhotosResponseDto): Photos {
    return Photos(
      photos = dto.photos.map { mapDtoToDomain(it) },
      totalResults = dto.totalResults,
      page = dto.page,
      perPage = dto.perPage,
      hasNextPage = !dto.nextPage.isNullOrEmpty()
    )
  }

  /**
   * Maps a single photo DTO to a domain model.
   *
   * @param dto The [PhotoDto] to map.
   * @return A [Photo] domain model.
   */
  fun mapDtoToDomain(dto: PhotoDto): Photo {
    return Photo(
      id = dto.id,
      type = dto.type,
      width = dto.width,
      height = dto.height,
      url = dto.url,
      photographer = dto.photographer,
      photographerUrl = dto.photographerUrl,
      photographerId = dto.photographerId,
      avgColor = parseAvgColor(dto.avgColor), // Use the helper
      thumbnailUrl = dto.src?.medium,
      tinyThumbnailUrl = dto.src?.tiny,
      largeImageUrl = dto.src?.large,
      liked = dto.liked,
      alt = dto.alt
    )
  }

  /**
   * Maps a database entity to a domain model.
   *
   * @param entity The [PhotoEntity] to map.
   * @return A [Photo] domain model.
   */
  fun mapEntityToDomain(entity: PhotoEntity): Photo {
    return Photo(
      id = entity.id,
      type = null, // 'type' is not stored in the database
      width = entity.width,
      height = entity.height,
      url = entity.url,
      photographer = entity.photographer,
      photographerUrl = entity.photographerUrl,
      photographerId = entity.photographerId,
      avgColor = parseAvgColor(entity.avgColor), // Use the same helper
      thumbnailUrl = entity.thumbnailUrl,
      tinyThumbnailUrl = entity.tinyThumbnailUrl,
      largeImageUrl = entity.largeImageUrl,
      liked = null, // 'liked' is not stored in the database
      alt = entity.alt
    )
  }

  /**
   * Maps a database entity to a domain model.
   *
   * @param entity The [PhotoEntity] to map.
   * @return A [Photo] domain model.
   */
  fun mapDomainToEntity(domain: Photo): PhotoEntity {
    return PhotoEntity(
      id = domain.id,
      width = domain.width,
      height = domain.height,
      url = domain.url,
      photographer = domain.photographer,
      photographerUrl = domain.photographerUrl,
      photographerId = domain.photographerId,
      avgColor = domain.avgColor?.let { String.format("#%08X", it.value.toLong()) },
      thumbnailUrl = domain.thumbnailUrl,
      tinyThumbnailUrl = domain.tinyThumbnailUrl,
      largeImageUrl = domain.largeImageUrl,
      alt = domain.alt
    )
  }

  /**
   * Parses a color string to a Compose [Color] object.
   *
   * @param colorString The hex color string to parse (e.g., "#FF0000").
   * @return The parsed [Color], or [Color.LightGray] if parsing fails or the input is null.
   */
  private fun parseAvgColor(colorString: String?): Color {
    val defaultColor = Color.LightGray
    return try {
      colorString?.toColorInt()?.let { Color(it) } ?: defaultColor
    } catch (_: IllegalArgumentException) {
      defaultColor
    }
  }
}
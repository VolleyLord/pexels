package com.volleylord.core.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity representing a photo in the local database.
 */
@Entity(tableName = "photos")
data class PhotoEntity(
  /** The unique identifier of the photo. */
  @PrimaryKey val id: Int,
  /** The width of the photo in pixels, if available. */
  val width: Int?,
  /** The height of the photo in pixels, if available. */
  val height: Int?,
  /** The URL to view the photo on the web, if available. */
  val url: String?,
  /** The name of the photographer, if available. */
  val photographer: String?,
  /** The URL of the photographer's profile, if available. */
  val photographerUrl: String?,
  /** The ID of the photographer, if available. */
  val photographerId: Long?,
  /** The average color of the photo as a hex string, if available. */
  val avgColor: String?,
  /** The URL of the thumbnail image, if available. */
  val thumbnailUrl: String?,
  /** The URL of a smaller thumbnail image, if available. */
  val tinyThumbnailUrl: String?,
  /** The URL of the full-size image, if available. */
  val largeImageUrl: String?,
  /** The alternative text description for accessibility, if available. */
  val alt: String?,
  /** Timestamp when the photo was cached (in milliseconds since epoch). */
  val cachedAt: Long = System.currentTimeMillis(),
  /** Query type: empty string for curated/popular photos, or search query. */
  val queryType: String = "",
  /** Whether this photo is bookmarked/saved by the user. */
  val isBookmarked: Boolean = false
)
package com.volleylord.core.domain.models

import androidx.compose.ui.graphics.Color

/**
 * Data class representing a photo in the application.
 */
data class Photo(
  /** The unique identifier of the photo. */
  val id: Int,
  /** The type of the photo. */
  val type: String?,
  /** The URL of the thumbnail image. */
  val thumbnailUrl: String?,
  /** The URL of a smaller thumbnail image. */
  val tinyThumbnailUrl: String?,
  /** The URL of the full-size image. */
  val largeImageUrl: String?,
  /** The URL of the original full-size image. */
  val originalImageUrl: String?,
  /** The URL of the 2x large image. */
  val large2xImageUrl: String?,
  /** The URL to view the photo on the web. */
  val url: String?,
  /** The name of the photographer, if available. */
  val photographer: String?,
  /** The URL to view the photographer on the web. */
  val photographerUrl: String?,
  /** The unique identifier of the photographer. */
  val photographerId: Long?,
  /** The alternative text description for accessibility, if available. */
  val alt: String?,
  /** The width of the photo in pixels, if available. */
  val width: Int?,
  /** The height of the photo in pixels, if available. */
  val height: Int?,
  /** The average color of the photo, if available. */
  val avgColor: Color?,
  /** Whether the photo is liked by the user. */
  val liked: Boolean?
)
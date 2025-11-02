package com.volleylord.core.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Data class representing the JSON structure of a successful photos response from the Pexels API.
 */
@Serializable
data class PhotosResponseDto(
  /** The current page number. */
  val page: Int,
  /** The number of photos per page. */
  @SerialName("per_page")
  val perPage: Int,
  /** The list of photos in the current page. */
  val photos: List<PhotoDto>,
  /** The total number of photos available across all pages. */
  @SerialName("total_results")
  val totalResults: Int,
  /** The URL of the previous page, if available. */
  @SerialName("prev_page")
  val prevPage: String? = null,
  /** The URL of the next page, if available. */
  @SerialName("next_page")
  val nextPage: String? = null
)

/**
 * Data class representing a single photo in the Pexels API response.
 */
@Serializable
data class PhotoDto(
  /** The type of the photo, if available. */
  val type: String? = null,
  /** The unique identifier of the photo. */
  val id: Int,
  /** The width of the photo in pixels, if available. */
  val width: Int? = null,
  /** The height of the photo in pixels, if available. */
  val height: Int? = null,
  /** The URL to view the photo on the web, if available. */
  val url: String? = null,
  /** The name of the photographer, if available. */
  val photographer: String? = null,
  /** The URL of the photographer's profile, if available. */
  @SerialName("photographer_url")
  val photographerUrl: String? = null,
  /** The ID of the photographer, if available. */
  @SerialName("photographer_id")
  val photographerId: Long? = null,
  /** The average color of the photo as a hex string, if available. */
  @SerialName("avg_color")
  val avgColor: String? = null,
  /** The source URLs for different photo sizes, if available. */
  val src: PhotoSourceDto? = null,
  /** Whether the photo is liked by the user, if available. */
  val liked: Boolean? = null,
  /** The alternative text description for accessibility, if available. */
  val alt: String? = null
)

/**
 * Data class representing the source URLs for different sizes of a photo in the Pexels API response.
 */
@Serializable
data class PhotoSourceDto(
  /** The URL of the original photo, if available. */
  val original: String? = null,
  /** The URL of the 2x large photo, if available. */
  val large2x: String? = null,
  /** The URL of the large photo, if available. */
  val large: String? = null,
  /** The URL of the medium photo, if available. */
  val medium: String? = null,
  /** The URL of the small photo, if available. */
  val small: String? = null,
  /** The URL of the portrait-oriented photo, if available. */
  val portrait: String? = null,
  /** The URL of the landscape-oriented photo, if available. */
  val landscape: String? = null,
  /** The URL of the tiny photo, if available. */
  val tiny: String? = null
)
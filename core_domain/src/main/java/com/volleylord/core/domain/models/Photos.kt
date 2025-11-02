package com.volleylord.core.domain.models

/**
 * Data class representing a paginated collection of photos with metadata.
 */
data class Photos(
  /** The list of photos in the current page. */
  val photos: List<Photo> = emptyList(),
  /** The total number of photos available across all pages. */
  val totalResults: Int = 0,
  /** The current page number. */
  val page: Int = 0,
  /** The number of photos per page. */
  val perPage: Int = 0,
  /** Indicates whether there is a next page of photos available. */
  val hasNextPage: Boolean
)
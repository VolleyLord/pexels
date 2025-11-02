package com.volleylord.feature.photos.photos.list

import com.volleylord.core.domain.models.Collection
import com.volleylord.core.domain.models.Photo

/**
 * Represents the UI state for the photo list screen.
 */
data class PhotoListUiState(
  /** The current search query. */
  val searchQuery: String = "",
  /** The list of featured collections. */
  val featuredCollections: List<Collection> = emptyList(),
  /** The currently selected collection, if any. */
  val selectedCollection: Collection? = null
)
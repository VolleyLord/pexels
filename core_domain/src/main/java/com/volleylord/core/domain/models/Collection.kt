package com.volleylord.core.domain.models

/**
 * Data class representing a featured collection for search.
 * Collections are categories like "Ice", "Watches", "Drawing", etc.
 */
data class Collection(
  /** The unique identifier of the collection. */
  val id: String,
  /** The display name of the collection. */
  val name: String,
  /** Whether this collection is currently selected. */
  val isSelected: Boolean = false
)


package com.volleylord.feature.photos.presentation.home

import com.volleylord.core.domain.models.Collection

data class PhotoListUiState(
  val searchQuery: String = "",
  val featuredCollections: List<Collection> = emptyList(),
  val selectedCollection: Collection? = null
)



package com.volleylord.feature.photos.presentation.details

import com.volleylord.core.domain.models.Photo

sealed interface PhotoDetailUiState {
  data object Loading : PhotoDetailUiState
  data class Error(val message: String?) : PhotoDetailUiState
  data class Success(val photo: Photo?) : PhotoDetailUiState
}



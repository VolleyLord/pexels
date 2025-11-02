package com.volleylord.feature.photos.photos.detail

import com.volleylord.core.domain.models.Photo

/**
 * Represents the UI state for the photo detail screen.
 */
sealed interface PhotoDetailUiState {

  /**
   * Indicates the screen is in a loading state while fetching photo details.
   */
  data object Loading : PhotoDetailUiState

  /**
   * Indicates an error occurred while fetching photo details.
   *
   * @param message The error message, if available.
   */
  data class Error(val message: String?) : PhotoDetailUiState

  /**
   * Indicates the photo details were successfully loaded.
   *
   * @param photo The photo data to display.
   */
  data class Success(val photo: Photo?) : PhotoDetailUiState
}
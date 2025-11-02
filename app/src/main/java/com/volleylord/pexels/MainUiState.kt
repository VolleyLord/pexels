package com.volleylord.pexels

import com.volleylord.pexels.navigation.AppDestination

/**
 * Represents the UI state controlling app initialization flow and navigation decision.
 */
sealed interface MainUiState {

  /**
   * The app is currently performing initial setup operations.
   */
  data object Loading : MainUiState

  /**
   * The app is ready and the initial navigation destination has been resolved.
   *
   * @property startDestination The destination to navigate to, which must be an [AppDestination].
   */
  data class Ready(val startDestination: AppDestination) : MainUiState
}
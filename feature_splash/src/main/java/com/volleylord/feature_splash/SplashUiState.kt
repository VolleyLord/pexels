package com.volleylord.feature_splash

/**
 * Represents the UI state of the splash screen.
 */
sealed interface SplashUiState {
  /**
   * The splash screen is currently loading initial data.
   */
  data object Loading : SplashUiState

  /**
   * The splash screen has finished loading and is ready to navigate.
   */
  data object Ready : SplashUiState
}


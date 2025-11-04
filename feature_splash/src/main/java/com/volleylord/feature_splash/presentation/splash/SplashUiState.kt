package com.volleylord.feature_splash.presentation.splash

sealed interface SplashUiState {
  data object Loading : SplashUiState
  data object Ready : SplashUiState
}



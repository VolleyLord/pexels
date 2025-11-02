package com.volleylord.pexels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.volleylord.core.domain.usecases.settings.SeedInitialApiKeyUseCase
import com.volleylord.pexels.navigation.PhotoListDestination
import com.volleylord.pexels.navigation.AppDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for managing the app's initial state and determining the start destination.
 *
 * It seeds the initial API key if needed and always starts with PhotoListDestination.
 *
 * @property seedInitialApiKeyUseCase Use case for seeding a default or previously stored API key.
 */
@HiltViewModel
class MainViewModel @Inject constructor(
  private val seedInitialApiKeyUseCase: SeedInitialApiKeyUseCase
) : ViewModel() {

  private val _uiState = MutableStateFlow<MainUiState>(MainUiState.Loading)
  val uiState = _uiState.asStateFlow()

  init {
    initializeApp()
  }

  /**
   * Initializes the app by seeding the API key if needed.
   * Always navigates to PhotoListDestination as the start destination.
   */
  private fun initializeApp() {
    viewModelScope.launch {
      seedInitialApiKeyUseCase()
      _uiState.value = MainUiState.Ready(PhotoListDestination)
    }
  }
}
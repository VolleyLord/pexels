package com.volleylord.feature_splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.volleylord.core.domain.usecases.photos.GetPhotosUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for managing the splash screen state and loading initial data.
 *
 * @param getPhotosUseCase Use case for fetching photos to preload initial data.
 */
@HiltViewModel
class SplashViewModel @Inject constructor(
  private val getPhotosUseCase: GetPhotosUseCase
) : ViewModel() {

  private val _uiState = MutableStateFlow<SplashUiState>(SplashUiState.Loading)
  val uiState = _uiState.asStateFlow()

  init {
    loadInitialData()
  }

  /**
   * Loads initial data for the home screen.
   * Starts preloading the first page of photos so they're ready when user navigates to home.
   * Also ensures minimum splash display time for better UX.
   */
  private fun loadInitialData() {
    viewModelScope.launch {
      try {
        // Start preloading photos in parallel
        val photosFlow: Flow<PagingData<*>> = getPhotosUseCase("")
        
        // Wait for first page to be requested (PagingData emits when first page loads)
        val preloadJob = launch {
          photosFlow.first()
        }
        
        // Minimum splash display time
        delay(1000)
        
        // Wait for preload to complete or timeout
        preloadJob.join()
        
        _uiState.value = SplashUiState.Ready
      } catch (e: Exception) {
        // Even if loading fails, navigate to home screen
        // Home screen will handle error state
        // Still ensure minimum display time
        delay(1000)
        _uiState.value = SplashUiState.Ready
      }
    }
  }
}


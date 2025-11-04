package com.volleylord.feature_splash.presentation.splash

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

@HiltViewModel
class SplashViewModel @Inject constructor(
  private val getPhotosUseCase: GetPhotosUseCase
) : ViewModel() {

  private val _uiState = MutableStateFlow<SplashUiState>(SplashUiState.Loading)
  val uiState = _uiState.asStateFlow()

  init {
    loadInitialData()
  }

  private fun loadInitialData() {
    viewModelScope.launch {
      try {
        val photosFlow: Flow<PagingData<*>> = getPhotosUseCase("")
        val preloadJob = launch { photosFlow.first() }
        delay(1000)
        preloadJob.join()
        _uiState.value = SplashUiState.Ready
      } catch (e: Exception) {
        delay(1000)
        _uiState.value = SplashUiState.Ready
      }
    }
  }
}



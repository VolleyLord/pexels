package com.volleylord.feature.photos.photos.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.volleylord.core.core.network.NetworkResult
import com.volleylord.core.domain.usecases.photos.GetPhotoDetailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

/**
 * ViewModel for managing photo detail data and UI state.
 *
 * @param getPhotoDetailUseCase Use case for fetching photo details.
 */
@HiltViewModel
class PhotoDetailViewModel @Inject constructor(
  private val getPhotoDetailUseCase: GetPhotoDetailUseCase
) : ViewModel() {

  private val _uiState = MutableStateFlow<PhotoDetailUiState>(PhotoDetailUiState.Loading)
  val uiState: StateFlow<PhotoDetailUiState> = _uiState.asStateFlow()

  /**
   * Loads the photo details for the given photo ID.
   *
   * @param photoId The ID of the photo to load.
   */
  fun loadPhoto(photoId: Int) {
    getPhotoDetailUseCase(photoId).onEach { result ->
      val newState = when (result) {
        is NetworkResult.Success -> PhotoDetailUiState.Success(result.data)
        is NetworkResult.Error -> PhotoDetailUiState.Error(result.message)
        is NetworkResult.Loading -> PhotoDetailUiState.Loading
      }
      _uiState.value = newState
    }.launchIn(viewModelScope)
  }
}
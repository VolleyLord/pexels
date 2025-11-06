package com.volleylord.feature.photos.presentation.details

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.volleylord.core.core.network.NetworkResult
import com.volleylord.core.domain.models.Photo
import com.volleylord.core.domain.usecases.photos.GetPhotoDetailUseCase
import com.volleylord.core.domain.usecases.photos.ToggleBookmarkUseCase
import com.volleylord.core.domain.services.PhotoDownloader
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PhotoDetailViewModel @Inject constructor(
    private val getPhotoDetailUseCase: GetPhotoDetailUseCase,
    private val toggleBookmarkUseCase: ToggleBookmarkUseCase,
    private val photoDownloader: PhotoDownloader
) : ViewModel() {

    private val _uiState = MutableStateFlow<PhotoDetailUiState>(PhotoDetailUiState.Loading)
    val uiState: StateFlow<PhotoDetailUiState> = _uiState.asStateFlow()

    fun loadPhoto(photoId: Int, isFromBookmarks: Boolean = false) {
        getPhotoDetailUseCase(photoId, isFromBookmarks).onEach { result ->
            val newState = when (result) {
                is NetworkResult.Success -> PhotoDetailUiState.Success(result.data)
                is NetworkResult.Error -> PhotoDetailUiState.Error(result.message)
                is NetworkResult.Loading -> PhotoDetailUiState.Loading
            }
            _uiState.value = newState
        }.launchIn(viewModelScope)
    }

    fun downloadPhoto(photo: Photo, context: Context) {
        viewModelScope.launch { photoDownloader.download(context, photo) }
    }

    fun toggleBookmark(photo: Photo) {
        viewModelScope.launch {
            val newBookmarkStatus = !(photo.liked ?: false)
            toggleBookmarkUseCase(photo.id, newBookmarkStatus)
            
            // update local state without refreshing screen
            val currentState = _uiState.value
            if (currentState is PhotoDetailUiState.Success) {
                val updatedPhoto = photo.copy(liked = newBookmarkStatus)
                _uiState.value = PhotoDetailUiState.Success(updatedPhoto)
            }
        }
    }
}



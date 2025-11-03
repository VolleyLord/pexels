package com.volleylord.feature.photos.photos.detail

import android.content.Context
import android.os.Build
import android.os.Environment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.volleylord.core.core.network.NetworkResult
import com.volleylord.core.domain.models.Photo
import com.volleylord.core.domain.usecases.photos.GetPhotoDetailUseCase
import com.volleylord.core.domain.usecases.photos.ToggleBookmarkUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.net.URL
import javax.inject.Inject

@HiltViewModel
class PhotoDetailViewModel @Inject constructor(
    private val getPhotoDetailUseCase: GetPhotoDetailUseCase,
    private val toggleBookmarkUseCase: ToggleBookmarkUseCase
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
        viewModelScope.launch {
            try {
                val imageUrl = photo.largeImageUrl ?: photo.thumbnailUrl
                if (imageUrl == null) return@launch

                withContext(Dispatchers.IO) {
                    val fileName = "pexels_${photo.id}.jpg"
                    val file: File

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        val downloadsDir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
                            ?: context.filesDir
                        file = File(downloadsDir, fileName)
                    } else {
                        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                        if (!downloadsDir.exists()) {
                            downloadsDir.mkdirs()
                        }
                        file = File(downloadsDir, fileName)
                    }

                    val url = URL(imageUrl)
                    val connection = url.openConnection()
                    connection.connect()

                    val input: InputStream = connection.getInputStream()
                    val output = FileOutputStream(file)

                    input.copyTo(output)
                    output.close()
                    input.close()

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        android.media.MediaScannerConnection.scanFile(
                            context,
                            arrayOf(file.absolutePath),
                            null,
                            null
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun toggleBookmark(photo: Photo) {
        viewModelScope.launch {
            val newBookmarkStatus = !(photo.liked ?: false)
            // Update bookmark status in database
            toggleBookmarkUseCase(photo.id, newBookmarkStatus)
            // Update UI state
            val updatedPhoto = photo.copy(liked = newBookmarkStatus)
            _uiState.value = PhotoDetailUiState.Success(updatedPhoto)
        }
    }
}